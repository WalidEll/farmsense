package ma.farmsense.dto.poultry;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ma.farmsense.entity.FeedingProgramTemplate;
import ma.farmsense.entity.GrowthStage;

import java.math.BigDecimal;
import java.util.UUID;

public record FeedingProgramTemplateDto(
        UUID id,
        @NotNull GrowthStage growthStage,
        @NotNull @Min(0) Integer ageStartDays,
        @NotNull Integer ageEndDays,
        @NotBlank String feedType,
        @NotNull @DecimalMin("0.01") BigDecimal dailyQuantityGrams,
        @NotNull @Min(1) Integer feedingFrequency,
        String notes,
        Integer sortOrder
) {
    public static FeedingProgramTemplateDto from(FeedingProgramTemplate template) {
        return new FeedingProgramTemplateDto(
                template.getId(),
                template.getGrowthStage(),
                template.getAgeStartDays(),
                template.getAgeEndDays(),
                template.getFeedType(),
                template.getDailyQuantityGrams(),
                template.getFeedingFrequency(),
                template.getNotes(),
                template.getSortOrder()
        );
    }
}
