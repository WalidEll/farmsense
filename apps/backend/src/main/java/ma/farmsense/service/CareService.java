package ma.farmsense.service;

import lombok.RequiredArgsConstructor;
import ma.farmsense.dto.care.CareScheduleResponse;
import ma.farmsense.entity.CareLog;
import ma.farmsense.entity.Plant;
import ma.farmsense.entity.SensorReading;
import ma.farmsense.entity.User;
import ma.farmsense.exception.AppException;
import ma.farmsense.repository.CareLogRepository;
import ma.farmsense.repository.PlantRepository;
import ma.farmsense.repository.SensorReadingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CareService {

    private final PlantRepository plantRepository;
    private final CareLogRepository careLogRepository;
    private final SensorReadingRepository sensorReadingRepository;

    // ── Species defaults ────────────────────────────────────────
    private record Intervals(int water, int fertilise, int repot) {}

    private static final Map<String, Intervals> SPECIES_DEFAULTS = Map.of(
        "peace lily",   new Intervals(7,  30, 365),
        "spider plant",  new Intervals(10, 14, 365),
        "basil",         new Intervals(3,  14, 90),
        "rosemary",      new Intervals(10, 30, 365),
        "mint",          new Intervals(4,  14, 180)
    );

    private static final Intervals DEFAULT = new Intervals(7, 30, 180);

    // ── Public API ──────────────────────────────────────────────

    /**
     * Called from PlantService.create(). Sets species-based intervals
     * and initialises lastXxxAt to now (so no immediate overdue).
     */
    @Transactional
    public void initSchedule(Plant plant) {
        Intervals iv = resolveDefaults(plant.getSpecies());
        plant.setWateringIntervalDays(iv.water());
        plant.setFertilisingIntervalDays(iv.fertilise());
        plant.setRepottingIntervalDays(iv.repot());

        Instant now = Instant.now();
        plant.setLastWateredAt(now);
        plant.setLastFertilisedAt(now);
        plant.setLastRepottedAt(now);

        plantRepository.save(plant);
    }

    /**
     * Returns the current care schedule with computed next-due dates,
     * days remaining, overdue flags, and recent care history.
     */
    public CareScheduleResponse getSchedule(User user, UUID plantId) {
        Plant plant = getOwned(user, plantId);

        // Build care tasks
        CareScheduleResponse.CareTask watering = buildTask(
                plant.getWateringIntervalDays(),
                plant.getLastWateredAt());
        CareScheduleResponse.CareTask fertilising = buildTask(
                plant.getFertilisingIntervalDays(),
                plant.getLastFertilisedAt());
        CareScheduleResponse.CareTask repotting = buildTask(
                plant.getRepottingIntervalDays(),
                plant.getLastRepottedAt());

        // Dynamic watering adjustment: if soil moisture < soilMin → overdue
        adjustWateringBySensor(plant, watering);

        // Recent care history
        List<CareScheduleResponse.CareLogEntry> recentLog =
                careLogRepository.findTop10ByPlantOrderByDoneAtDesc(plant)
                        .stream()
                        .map(log -> CareScheduleResponse.CareLogEntry.builder()
                                .id(log.getId())
                                .taskType(log.getTaskType().name())
                                .doneAt(log.getDoneAt())
                                .notes(log.getNotes())
                                .build())
                        .toList();

        return CareScheduleResponse.builder()
                .watering(watering)
                .fertilising(fertilising)
                .repotting(repotting)
                .recentLog(recentLog)
                .build();
    }

    /**
     * Marks a care task as done: creates a CareLog entry,
     * updates lastXxxAt on the Plant, returns the updated schedule.
     */
    @Transactional
    public CareScheduleResponse markDone(User user, UUID plantId, CareLog.TaskType taskType) {
        Plant plant = getOwned(user, plantId);
        Instant now = Instant.now();

        // Create care log entry
        CareLog log = CareLog.builder()
                .plant(plant)
                .taskType(taskType)
                .doneAt(now)
                .build();
        careLogRepository.save(log);

        // Update plant's last-done timestamp
        switch (taskType) {
            case WATER     -> plant.setLastWateredAt(now);
            case FERTILISE -> plant.setLastFertilisedAt(now);
            case REPOT     -> plant.setLastRepottedAt(now);
        }
        plantRepository.save(plant);

        return getSchedule(user, plantId);
    }

    // ── Helpers ─────────────────────────────────────────────────

    private CareScheduleResponse.CareTask buildTask(int intervalDays, Instant lastDoneAt) {
        if (lastDoneAt == null) {
            // No history yet — treat as due now
            return CareScheduleResponse.CareTask.builder()
                    .intervalDays(intervalDays)
                    .lastDoneAt(null)
                    .nextDueAt(Instant.now())
                    .daysRemaining(0)
                    .overdue(true)
                    .build();
        }

        Instant nextDue = lastDoneAt.plus(Duration.ofDays(intervalDays));
        long daysRemaining = Duration.between(Instant.now(), nextDue).toDays();

        return CareScheduleResponse.CareTask.builder()
                .intervalDays(intervalDays)
                .lastDoneAt(lastDoneAt)
                .nextDueAt(nextDue)
                .daysRemaining((int) daysRemaining)
                .overdue(daysRemaining < 0)
                .build();
    }

    /**
     * If the latest soil sensor reading is below the plant's soilMin
     * threshold, force the watering task to show as overdue/now.
     */
    private void adjustWateringBySensor(Plant plant,
                                         CareScheduleResponse.CareTask watering) {
        sensorReadingRepository.findLatestByPlantId(plant.getId())
                .ifPresent(reading -> {
                    if (reading.getSoilMoisture() != null
                            && reading.getSoilMoisture() < plant.getSoilMin()) {
                        watering.setOverdue(true);
                        watering.setDaysRemaining(0);
                        watering.setNextDueAt(Instant.now());
                    }
                });
    }

    private Intervals resolveDefaults(String species) {
        if (species == null || species.isBlank()) return DEFAULT;
        String lower = species.toLowerCase().trim();
        return SPECIES_DEFAULTS.entrySet().stream()
                .filter(e -> lower.contains(e.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(DEFAULT);
    }

    private Plant getOwned(User user, UUID plantId) {
        return plantRepository.findByIdAndUserAndDeletedAtIsNull(plantId, user)
                .orElseThrow(() -> AppException.notFound("Plant not found"));
    }
}
