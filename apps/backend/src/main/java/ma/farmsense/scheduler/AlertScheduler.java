package ma.farmsense.scheduler;

import ma.farmsense.entity.*;
import ma.farmsense.repository.*;
import ma.farmsense.service.WhatsAppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.List;
import java.util.Optional;

/**
 * Runs every 15 minutes.
 * For each plant with an active sensor:
 * 1. Fetch the last 2 readings
 * 2. Check each threshold
 * 3. If breached for 2 consecutive readings → fire alert
 * 4. Suppress if same alert fired within suppress-hours
 * 5. Respect user alert preferences (enabled types + quiet hours)
 */
@Component
@ConditionalOnProperty(name = "farmsense.features.plant-management", havingValue = "true", matchIfMissing = true)
public class AlertScheduler {

    private static final Logger log = LoggerFactory.getLogger(AlertScheduler.class);

    private final PlantRepository plantRepo;
    private final DeviceRepository deviceRepo;
    private final SensorReadingRepository readingRepo;
    private final AlertRepository alertRepo;
    private final AlertPreferenceRepository alertPrefRepo;
    private final WhatsAppService whatsApp;

    public AlertScheduler(PlantRepository plantRepo, DeviceRepository deviceRepo,
                          SensorReadingRepository readingRepo, AlertRepository alertRepo,
                          AlertPreferenceRepository alertPrefRepo, WhatsAppService whatsApp) {
        this.plantRepo = plantRepo;
        this.deviceRepo = deviceRepo;
        this.readingRepo = readingRepo;
        this.alertRepo = alertRepo;
        this.alertPrefRepo = alertPrefRepo;
        this.whatsApp = whatsApp;
    }

    @Value("${farmsense.alert.suppress-hours:4}")
    private int suppressHours;

    @Value("${farmsense.alert.daylight-start-hour:7}")
    private int daylightStart;

    @Value("${farmsense.alert.daylight-end-hour:19}")
    private int daylightEnd;

    @Scheduled(fixedDelayString = "${farmsense.alert.check-interval-ms:900000}")
    public void checkThresholds() {
        log.debug("AlertScheduler running at {}", Instant.now());

        List<Plant> plants = plantRepo.findAllActive();
        for (Plant plant : plants) {
            try {
                checkPlant(plant);
            } catch (Exception e) {
                log.error("Error checking plant {}: {}", plant.getId(), e.getMessage());
            }
        }
    }

    private void checkPlant(Plant plant) {
        List<Device> devices = deviceRepo.findByPlantId(plant.getId());
        for (Device device : devices) {
            checkDeviceForPlant(plant, device);
        }
    }

    private void checkDeviceForPlant(Plant plant, Device device) {
        List<SensorReading> last2 = readingRepo.findLatest2ByPlantAndDeviceId(plant.getId(), device.getDeviceId());
        if (last2.size() < 2)
            return;

        SensorReading r1 = last2.get(0);
        SensorReading r2 = last2.get(1);

        // ── Soil moisture ──────────────────────────────────────
        if (r1.getSoilMoisture() != null && r2.getSoilMoisture() != null) {
            if (r1.getSoilMoisture() < plant.getSoilMin() && r2.getSoilMoisture() < plant.getSoilMin()) {
                fire(plant, Alert.AlertType.SOIL_DRY, r1.getSoilMoisture().doubleValue());
            }
        }

        // ── Temperature high/low ──────────────────────────────
        if (r1.getTemperature() != null && r2.getTemperature() != null) {
            if (r1.getTemperature() > plant.getTempMax() && r2.getTemperature() > plant.getTempMax()) {
                fire(plant, Alert.AlertType.TEMP_HIGH, r1.getTemperature());
            }
            if (r1.getTemperature() < plant.getTempMin() && r2.getTemperature() < plant.getTempMin()) {
                fire(plant, Alert.AlertType.TEMP_LOW, r1.getTemperature());
            }
        }

        // ── Light — only during daylight hours ─────────────────
        int currentHour = ZonedDateTime.now(ZoneId.of("Africa/Casablanca")).getHour();
        if (currentHour >= daylightStart && currentHour < daylightEnd) {
            if (r1.getLightLux() != null && r2.getLightLux() != null) {
                if (r1.getLightLux() < plant.getLightMin() && r2.getLightLux() < plant.getLightMin()) {
                    fire(plant, Alert.AlertType.LIGHT_LOW, r1.getLightLux());
                }
            }
        }
    }

    private void fire(Plant plant, Alert.AlertType type, double value) {
        User user = plant.getUser();

        // Load user's alert preferences (defaults if none saved)
        AlertPreference pref = alertPrefRepo.findByUser(user).orElse(null);

        // Check if this alert type is enabled
        if (pref != null && !isAlertTypeEnabled(pref, type)) {
            log.debug("Alert type {} disabled by user {} preferences", type, user.getId());
            return;
        }

        // Suppress check
        Optional<Alert> recent = alertRepo.findMostRecentByPlantAndType(
                plant.getId(), type, Instant.now().minusSeconds(suppressHours * 3600L));
        if (recent.isPresent()) {
            log.debug("Suppressing {} for plant {} — fired {}h ago",
                    type, plant.getId(), suppressHours);
            return;
        }

        Alert alert = new Alert();
        alert.setUser(user);
        alert.setPlant(plant);
        alert.setType(type);
        alert.setSeverity(Alert.Severity.MEDIUM);
        alert.setSensorValue(value);
        alert.setMsgEn(buildMsg(type, value, "en", plant.getName()));
        alert.setMsgFr(buildMsg(type, value, "fr", plant.getName()));
        alert.setMsgAr(buildMsg(type, value, "ar", plant.getName()));

        alertRepo.save(alert);
        log.info("Alert fired: {} for plant {} ({})", type, plant.getName(), value);

        // Check quiet hours — skip WhatsApp if within quiet window, but alert is still
        // saved
        boolean inQuietHours = isInQuietHours(pref);

        // Send WhatsApp (respect quiet hours + channel preference)
        boolean whatsappEnabled = pref == null || pref.isChannelWhatsapp();
        if (user.getPhoneWa() != null && whatsappEnabled && !inQuietHours) {
            String msg = switch (user.getLang()) {
                case EN -> alert.getMsgEn();
                case AR -> alert.getMsgAr();
                default -> alert.getMsgFr();
            };
            whatsApp.send(user.getPhoneWa(), msg);
            alert.setWaSent(true);
            alertRepo.save(alert);
        }
    }

    /** Check whether the given alert type is enabled in preferences */
    private boolean isAlertTypeEnabled(AlertPreference pref, Alert.AlertType type) {
        return switch (type) {
            case SOIL_DRY -> pref.isSoilDryEnabled();
            case SOIL_WET -> pref.isSoilWetEnabled();
            case TEMP_HIGH -> pref.isTempHighEnabled();
            case TEMP_LOW -> pref.isTempLowEnabled();
            case LIGHT_LOW -> pref.isLightLowEnabled();
            case DEVICE_OFFLINE -> pref.isDeviceOfflineEnabled();
        };
    }

    /** Check whether the current time falls within quiet hours */
    private boolean isInQuietHours(AlertPreference pref) {
        if (pref == null || pref.getQuietHoursStart() == null || pref.getQuietHoursEnd() == null) {
            return false;
        }
        int currentHour = ZonedDateTime.now(ZoneId.of("Africa/Casablanca")).getHour();
        int start = pref.getQuietHoursStart();
        int end = pref.getQuietHoursEnd();

        if (start <= end) {
            // e.g. 22 <= current < 7 doesn't apply; 9 <= current < 17 does
            return currentHour >= start && currentHour < end;
        } else {
            // Wraps midnight: e.g. start=22, end=7 → quiet from 22:00 to 07:00
            return currentHour >= start || currentHour < end;
        }
    }

    private String buildMsg(Alert.AlertType type, double value, String lang, String plantName) {
        return switch (type) {
            case SOIL_DRY -> switch (lang) {
                case "en" -> String.format("🌱 Soil is dry! Water now — %s (%.0f%%)", plantName, value);
                case "ar" -> String.format("🌱 التربة جافة! اسقِ الآن — %s (%.0f%%)", plantName, value);
                default -> String.format("🌱 Sol sec ! Arrosez maintenant — %s (%.0f%%)", plantName, value);
            };
            case TEMP_HIGH -> switch (lang) {
                case "en" -> String.format("🌡️ Too hot! — %s (%.1f°C)", plantName, value);
                case "ar" -> String.format("🌡️ حرارة مرتفعة جداً! — %s (%.1f°C)", plantName, value);
                default -> String.format("🌡️ Trop chaud ! — %s (%.1f°C)", plantName, value);
            };
            case TEMP_LOW -> switch (lang) {
                case "en" -> String.format("🥶 Too cold! — %s (%.1f°C)", plantName, value);
                case "ar" -> String.format("🥶 برودة شديدة! — %s (%.1f°C)", plantName, value);
                default -> String.format("🥶 Trop froid ! — %s (%.1f°C)", plantName, value);
            };
            case LIGHT_LOW -> switch (lang) {
                case "en" -> String.format("☀️ Not enough light — %s (%.0f lux)", plantName, value);
                case "ar" -> String.format("☀️ الضوء غير كافٍ — %s (%.0f lux)", plantName, value);
                default -> String.format("☀️ Pas assez de lumière — %s (%.0f lux)", plantName, value);
            };
            default -> String.format("⚠️ Alert %s — %s", type, plantName);
        };
    }
}
