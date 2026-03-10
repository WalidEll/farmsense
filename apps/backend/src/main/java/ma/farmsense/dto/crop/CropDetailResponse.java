package ma.farmsense.dto.crop;

import lombok.Builder;
import lombok.Data;
import ma.farmsense.entity.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class CropDetailResponse {

    private UUID id;
    private String name;
    private String nameAr;
    private String nameEn;
    private String scientificName;
    private CropCategory category;
    private String description;
    private String descriptionAr;
    private String descriptionEn;
    private String imageUrl;
    private String growingSeason;
    private Integer daysToHarvest;
    private CropDifficulty difficulty;
    private Instant createdAt;
    private Instant updatedAt;

    private CropRequirementResponse requirements;
    private List<CropGrowthStageResponse> stages;
    private CropNutrientResponse nutrients;
    private List<CropIssueResponse> issues;

    public static CropDetailResponse from(Crop c,
                                           CropRequirement req,
                                           List<CropGrowthStage> stages,
                                           CropNutrient nut,
                                           List<CropIssue> issues) {
        return CropDetailResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .nameAr(c.getNameAr())
                .nameEn(c.getNameEn())
                .scientificName(c.getScientificName())
                .category(c.getCategory())
                .description(c.getDescription())
                .descriptionAr(c.getDescriptionAr())
                .descriptionEn(c.getDescriptionEn())
                .imageUrl(c.getImageUrl())
                .growingSeason(c.getGrowingSeason())
                .daysToHarvest(c.getDaysToHarvest())
                .difficulty(c.getDifficulty())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .requirements(req != null ? CropRequirementResponse.from(req) : null)
                .stages(stages.stream().map(CropGrowthStageResponse::from).toList())
                .nutrients(nut != null ? CropNutrientResponse.from(nut) : null)
                .issues(issues.stream().map(CropIssueResponse::from).toList())
                .build();
    }
}
