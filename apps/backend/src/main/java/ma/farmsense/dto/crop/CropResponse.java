package ma.farmsense.dto.crop;

import ma.farmsense.entity.Crop;
import ma.farmsense.entity.CropCategory;
import ma.farmsense.entity.CropDifficulty;

import java.time.Instant;
import java.util.UUID;

public record CropResponse(
        UUID id,
        String name,
        String nameAr,
        String nameEn,
        String scientificName,
        CropCategory category,
        String description,
        String descriptionAr,
        String descriptionEn,
        String imageUrl,
        String growingSeason,
        Integer daysToHarvest,
        CropDifficulty difficulty,
        Instant createdAt,
        Instant updatedAt
) {
    public static CropResponse from(Crop c) {
        return new CropResponse(
                c.getId(),
                c.getName(),
                c.getNameAr(),
                c.getNameEn(),
                c.getScientificName(),
                c.getCategory(),
                c.getDescription(),
                c.getDescriptionAr(),
                c.getDescriptionEn(),
                c.getImageUrl(),
                c.getGrowingSeason(),
                c.getDaysToHarvest(),
                c.getDifficulty(),
                c.getCreatedAt(),
                c.getUpdatedAt()
        );
    }
}
