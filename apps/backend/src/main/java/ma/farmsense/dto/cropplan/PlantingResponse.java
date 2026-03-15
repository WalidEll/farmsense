package ma.farmsense.dto.cropplan;

import ma.farmsense.entity.Planting;
import ma.farmsense.entity.PlantingStatus;
import ma.farmsense.entity.YieldUnit;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record PlantingResponse(
        UUID id,
        UUID cropPlanId,
        UUID cropId,
        String cropName,
        UUID farmLocationId,
        String farmLocationName,
        PlantingStatus status,
        Integer quantity,
        Double areaM2,
        String notes,
        LocalDate plannedSowDate,
        LocalDate plannedTransplantDate,
        LocalDate plannedHarvestDate,
        LocalDate actualSowDate,
        LocalDate actualTransplantDate,
        LocalDate actualHarvestDate,
        Double yieldAmount,
        YieldUnit yieldUnit,
        Integer qualityRating,
        String harvestNotes,
        String failureReason,
        Instant createdAt,
        Instant updatedAt
) {
    public static PlantingResponse from(Planting p) {
        return new PlantingResponse(
                p.getId(),
                p.getCropPlan().getId(),
                p.getCrop().getId(),
                p.getCrop().getName(),
                p.getFarmLocation() != null ? p.getFarmLocation().getId() : null,
                p.getFarmLocation() != null ? p.getFarmLocation().getName() : null,
                p.getStatus(),
                p.getQuantity(),
                p.getAreaM2(),
                p.getNotes(),
                p.getPlannedSowDate(),
                p.getPlannedTransplantDate(),
                p.getPlannedHarvestDate(),
                p.getActualSowDate(),
                p.getActualTransplantDate(),
                p.getActualHarvestDate(),
                p.getYieldAmount(),
                p.getYieldUnit(),
                p.getQualityRating(),
                p.getHarvestNotes(),
                p.getFailureReason(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }
}
