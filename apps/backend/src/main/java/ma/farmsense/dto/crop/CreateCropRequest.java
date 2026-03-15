package ma.farmsense.dto.crop;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ma.farmsense.entity.CropCategory;
import ma.farmsense.entity.CropDifficulty;

public record CreateCropRequest(
        @NotBlank String name,
        @NotNull CropCategory category,
        String nameAr,
        String nameEn,
        String scientificName,
        String description,
        String descriptionAr,
        String descriptionEn,
        String imageUrl,
        String growingSeason,
        Integer daysToHarvest,
        CropDifficulty difficulty
) {}
