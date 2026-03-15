package ma.farmsense.dto.cropplan;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ma.farmsense.entity.PlanSeason;

public record CreateCropPlanRequest(
        @NotBlank String name,
        String nameAr,
        String nameEn,
        String description,
        String descriptionAr,
        String descriptionEn,
        @NotNull PlanSeason season,
        @NotNull Integer year
) {}
