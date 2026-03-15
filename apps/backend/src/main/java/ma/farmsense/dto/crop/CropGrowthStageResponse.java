package ma.farmsense.dto.crop;

import ma.farmsense.entity.CropGrowthStage;

import java.util.UUID;

public record CropGrowthStageResponse(
        UUID id,
        Integer stageOrder,
        String name,
        String nameAr,
        String nameEn,
        Integer durationDays,
        String description,
        String descriptionAr,
        String descriptionEn
) {
    public static CropGrowthStageResponse from(CropGrowthStage s) {
        return new CropGrowthStageResponse(
                s.getId(),
                s.getStageOrder(),
                s.getName(),
                s.getNameAr(),
                s.getNameEn(),
                s.getDurationDays(),
                s.getDescription(),
                s.getDescriptionAr(),
                s.getDescriptionEn()
        );
    }
}
