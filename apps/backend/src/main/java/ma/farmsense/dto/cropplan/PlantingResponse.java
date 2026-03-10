package ma.farmsense.dto.cropplan;

import lombok.Builder;
import lombok.Data;
import ma.farmsense.entity.Planting;
import ma.farmsense.entity.PlantingStatus;
import ma.farmsense.entity.YieldUnit;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class PlantingResponse {

    private UUID id;
    private UUID cropPlanId;
    private UUID cropId;
    private String cropName;
    private UUID farmLocationId;
    private String farmLocationName;
    private PlantingStatus status;
    private Integer quantity;
    private Double areaM2;
    private String notes;
    private LocalDate plannedSowDate;
    private LocalDate plannedTransplantDate;
    private LocalDate plannedHarvestDate;
    private LocalDate actualSowDate;
    private LocalDate actualTransplantDate;
    private LocalDate actualHarvestDate;
    private Double yieldAmount;
    private YieldUnit yieldUnit;
    private Integer qualityRating;
    private String harvestNotes;
    private String failureReason;
    private Instant createdAt;
    private Instant updatedAt;

    public static PlantingResponse from(Planting p) {
        return PlantingResponse.builder()
                .id(p.getId())
                .cropPlanId(p.getCropPlan().getId())
                .cropId(p.getCrop().getId())
                .cropName(p.getCrop().getName())
                .farmLocationId(p.getFarmLocation() != null ? p.getFarmLocation().getId() : null)
                .farmLocationName(p.getFarmLocation() != null ? p.getFarmLocation().getName() : null)
                .status(p.getStatus())
                .quantity(p.getQuantity())
                .areaM2(p.getAreaM2())
                .notes(p.getNotes())
                .plannedSowDate(p.getPlannedSowDate())
                .plannedTransplantDate(p.getPlannedTransplantDate())
                .plannedHarvestDate(p.getPlannedHarvestDate())
                .actualSowDate(p.getActualSowDate())
                .actualTransplantDate(p.getActualTransplantDate())
                .actualHarvestDate(p.getActualHarvestDate())
                .yieldAmount(p.getYieldAmount())
                .yieldUnit(p.getYieldUnit())
                .qualityRating(p.getQualityRating())
                .harvestNotes(p.getHarvestNotes())
                .failureReason(p.getFailureReason())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
