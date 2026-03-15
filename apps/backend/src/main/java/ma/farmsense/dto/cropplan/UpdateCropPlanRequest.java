package ma.farmsense.dto.cropplan;

import ma.farmsense.entity.PlanSeason;
import ma.farmsense.entity.PlanStatus;

public record UpdateCropPlanRequest(
        String name,
        String nameAr,
        String nameEn,
        String description,
        String descriptionAr,
        String descriptionEn,
        PlanSeason season,
        Integer year,
        PlanStatus status
) {}
