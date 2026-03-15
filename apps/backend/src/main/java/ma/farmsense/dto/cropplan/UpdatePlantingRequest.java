package ma.farmsense.dto.cropplan;

import ma.farmsense.entity.PlantingStatus;

import java.time.LocalDate;
import java.util.UUID;

public record UpdatePlantingRequest(
        UUID farmLocationId,
        PlantingStatus status,
        Integer quantity,
        Double areaM2,
        String notes,
        LocalDate plannedSowDate,
        LocalDate plannedTransplantDate,
        LocalDate plannedHarvestDate,
        String failureReason
) {}
