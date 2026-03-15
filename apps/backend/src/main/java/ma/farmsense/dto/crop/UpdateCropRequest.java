package ma.farmsense.dto.crop;

import ma.farmsense.entity.CropCategory;
import ma.farmsense.entity.CropDifficulty;

public record UpdateCropRequest(
        String name,
        CropCategory category,
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
