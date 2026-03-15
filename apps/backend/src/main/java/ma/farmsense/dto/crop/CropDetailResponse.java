package ma.farmsense.dto.crop;

import ma.farmsense.entity.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CropDetailResponse(
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
        Instant updatedAt,
        CropRequirementResponse requirements,
        List<CropGrowthStageResponse> stages,
        CropNutrientResponse nutrients,
        List<CropIssueResponse> issues
) {
    public static CropDetailResponse from(Crop c,
                                           CropRequirement req,
                                           List<CropGrowthStage> stages,
                                           CropNutrient nut,
                                           List<CropIssue> issues) {
        return new CropDetailResponse(
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
                c.getUpdatedAt(),
                req != null ? CropRequirementResponse.from(req) : null,
                stages.stream().map(CropGrowthStageResponse::from).toList(),
                nut != null ? CropNutrientResponse.from(nut) : null,
                issues.stream().map(CropIssueResponse::from).toList()
        );
    }
}
