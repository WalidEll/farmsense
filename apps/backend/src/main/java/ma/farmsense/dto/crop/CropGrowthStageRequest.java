package ma.farmsense.dto.crop;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CropGrowthStageRequest(
        @NotNull Integer stageOrder,
        @NotBlank String name,
        String nameAr,
        String nameEn,
        Integer durationDays,
        String description,
        String descriptionAr,
        String descriptionEn
) {}
