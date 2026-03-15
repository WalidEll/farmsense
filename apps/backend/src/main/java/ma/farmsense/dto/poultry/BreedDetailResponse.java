package ma.farmsense.dto.poultry;

import ma.farmsense.entity.Breed;
import ma.farmsense.entity.BreedCategory;
import ma.farmsense.entity.BreedPurpose;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record BreedDetailResponse(
        UUID id,
        String name,
        String nameAr,
        String nameEn,
        BreedCategory category,
        BreedPurpose purpose,
        String origin,
        String description,
        String descriptionAr,
        String descriptionEn,
        String imageUrl,
        Boolean isSystem,
        String climateSuitability,
        BigDecimal avgWeightMaleKg,
        BigDecimal avgWeightFemaleKg,
        List<FeedingProgramTemplateDto> feedingPrograms,
        List<VaccinationTemplateDto> vaccinationSchedule,
        List<ProductionBenchmarkDto> productionBenchmarks,
        List<HousingGuidelineDto> housingGuidelines,
        Instant createdAt,
        Instant updatedAt
) {
    public static BreedDetailResponse from(Breed breed) {
        return new BreedDetailResponse(
                breed.getId(),
                breed.getName(),
                breed.getNameAr(),
                breed.getNameEn(),
                breed.getCategory(),
                breed.getPurpose(),
                breed.getOrigin(),
                breed.getDescription(),
                breed.getDescriptionAr(),
                breed.getDescriptionEn(),
                breed.getImageUrl(),
                breed.getIsSystem(),
                breed.getClimateSuitability(),
                breed.getAvgWeightMaleKg(),
                breed.getAvgWeightFemaleKg(),
                breed.getFeedingPrograms().stream().map(FeedingProgramTemplateDto::from).toList(),
                breed.getVaccinationSchedule().stream().map(VaccinationTemplateDto::from).toList(),
                breed.getProductionBenchmarks().stream().map(ProductionBenchmarkDto::from).toList(),
                breed.getHousingGuidelines().stream().map(HousingGuidelineDto::from).toList(),
                breed.getCreatedAt(),
                breed.getUpdatedAt()
        );
    }
}
