package ma.farmsense.dto.cropplan;

import ma.farmsense.entity.CropPlan;
import ma.farmsense.entity.PlanSeason;
import ma.farmsense.entity.PlanStatus;

import java.time.Instant;
import java.util.UUID;

public record CropPlanResponse(
        UUID id,
        String name,
        String nameAr,
        String nameEn,
        String description,
        String descriptionAr,
        String descriptionEn,
        PlanSeason season,
        Integer year,
        PlanStatus status,
        long plantingCount,
        Instant createdAt,
        Instant updatedAt
) {
    public static CropPlanResponse from(CropPlan p, long plantingCount) {
        return new CropPlanResponse(
                p.getId(),
                p.getName(),
                p.getNameAr(),
                p.getNameEn(),
                p.getDescription(),
                p.getDescriptionAr(),
                p.getDescriptionEn(),
                p.getSeason(),
                p.getYear(),
                p.getStatus(),
                plantingCount,
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }
}
