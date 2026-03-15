package ma.farmsense.dto.poultry;

import jakarta.validation.constraints.NotBlank;
import ma.farmsense.entity.GrowthStage;
import ma.farmsense.entity.HousingGuideline;

import java.util.UUID;

public record HousingGuidelineDto(
        UUID id,
        @NotBlank String parameterName,
        @NotBlank String recommendedValue,
        String unit,
        GrowthStage growthStage,
        String notes,
        Integer sortOrder
) {
    public static HousingGuidelineDto from(HousingGuideline guideline) {
        return new HousingGuidelineDto(
                guideline.getId(),
                guideline.getParameterName(),
                guideline.getRecommendedValue(),
                guideline.getUnit(),
                guideline.getGrowthStage(),
                guideline.getNotes(),
                guideline.getSortOrder()
        );
    }
}
