package ma.farmsense.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.farmsense.entity.*;
import ma.farmsense.repository.*;
import ma.farmsense.service.WhatsAppService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.List;
import java.util.Optional;

/**
 * Runs every 15 minutes.
 * For each plant with an active sensor:
 *  1. Fetch the last 2 readings
 *  2. Check each threshold
 *  3. If breached for 2 consecutive readings → fire alert
 *  4. Suppress if same alert fired within suppress-hours
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AlertScheduler {

    private final PlantRepository plantRepo;
    private final SensorReadingRepository readingRepo;
    private final AlertRepository alertRepo;
    private final WhatsAppService whatsApp;

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
        List<SensorReading> last2 = readingRepo.findLatest2ByPlant(plant.getId());
        if (last2.size() < 2) return;

        SensorReading r1 = last2.get(0);
        SensorReading r2 = last2.get(1);

        // ── Soil moisture ──────────────────────────────────────
        if (r1.getSoilMoisture() != null && r2.getSoilMoisture() != null) {
            if (r1.getSoilMoisture() < plant.getSoilMin() && r2.getSoilMoisture() < plant.getSoilMin()) {
                fire(plant, Alert.AlertType.SOIL_DRY, r1.getSoilMoisture().doubleValue());
            }
        }

        // ── Temperature high ───────────────────────────────────
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
        // Suppress check
        Optional<Alert> recent = alertRepo.findMostRecentByPlantAndType(
                plant.getId(), type, Instant.now().minusSeconds(suppressHours * 3600L));
        if (recent.isPresent()) {
            log.debug("Suppressing {} for plant {} — fired {}h ago",
                    type, plant.getId(), suppressHours);
            return;
        }

        Alert alert = Alert.builder()
                .user(plant.getUser())
                .plant(plant)
                .type(type)
                .severity(Alert.Severity.MEDIUM)
                .sensorValue(value)
                .msgDarija(buildMsg(type, value, "darija", plant.getName()))
                .msgFr(buildMsg(type, value, "fr", plant.getName()))
                .msgAr(buildMsg(type, value, "ar", plant.getName()))
                .build();

        alertRepo.save(alert);
        log.info("Alert fired: {} for plant {} ({})", type, plant.getName(), value);

        // Send WhatsApp
        if (plant.getUser().getPhoneWa() != null) {
            String msg = switch (plant.getUser().getLang()) {
                case DARIJA -> alert.getMsgDarija();
                case AR     -> alert.getMsgAr();
                default     -> alert.getMsgFr();
            };
            whatsApp.send(plant.getUser().getPhoneWa(), msg);
            alert.setWaSent(true);
            alertRepo.save(alert);
        }
    }

    private String buildMsg(Alert.AlertType type, double value, String lang, String plantName) {
        return switch (type) {
            case SOIL_DRY -> switch (lang) {
                case "darija" -> String.format("🌱 Trab jaf! Sqi daba — %s (%.0f%%)", plantName, value);
                case "ar"     -> String.format("🌱 التربة جافة! اسقِ الآن — %s (%.0f%%)", plantName, value);
                default       -> String.format("🌱 Sol sec ! Arrosez maintenant — %s (%.0f%%)", plantName, value);
            };
            case TEMP_HIGH -> switch (lang) {
                case "darija" -> String.format("🌡️ Skhana bzzaf! — %s (%.1f°C)", plantName, value);
                case "ar"     -> String.format("🌡️ حرارة مرتفعة جداً! — %s (%.1f°C)", plantName, value);
                default       -> String.format("🌡️ Trop chaud ! — %s (%.1f°C)", plantName, value);
            };
            case TEMP_LOW -> switch (lang) {
                case "darija" -> String.format("🥶 Bard bzzaf! — %s (%.1f°C)", plantName, value);
                case "ar"     -> String.format("🥶 برودة شديدة! — %s (%.1f°C)", plantName, value);
                default       -> String.format("🥶 Trop froid ! — %s (%.1f°C)", plantName, value);
            };
            case LIGHT_LOW -> switch (lang) {
                case "darija" -> String.format("☀️ Ma3ndha3 daw ikafi — %s (%.0f lux)", plantName, value);
                case "ar"     -> String.format("☀️ الضوء غير كافٍ — %s (%.0f lux)", plantName, value);
                default       -> String.format("☀️ Pas assez de lumière — %s (%.0f lux)", plantName, value);
            };
            default -> String.format("⚠️ Alert %s — %s", type, plantName);
        };
    }
}
