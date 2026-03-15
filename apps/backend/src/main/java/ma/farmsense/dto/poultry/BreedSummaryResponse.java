package ma.farmsense.dto.poultry;

import ma.farmsense.entity.Breed;
import ma.farmsense.entity.BreedCategory;
import ma.farmsense.entity.BreedPurpose;

import java.math.BigDecimal;
import java.util.UUID;

public record BreedSummaryResponse(
        UUID id,
        String name,
        String nameAr,
        String nameEn,
        BreedCategory category,
        BreedPurpose purpose,
        String origin,
        String imageUrl,
        Boolean isSystem,
        String climateSuitability,
        BigDecimal avgWeightMaleKg,
        BigDecimal avgWeightFemaleKg
) {
    public static BreedSummaryResponse from(Breed breed) {
        return new BreedSummaryResponse(
                breed.getId(),
                breed.getName(),
                breed.getNameAr(),
                breed.getNameEn(),
                breed.getCategory(),
                breed.getPurpose(),
                breed.getOrigin(),
                breed.getImageUrl(),
                breed.getIsSystem(),
                breed.getClimateSuitability(),
                breed.getAvgWeightMaleKg(),
                breed.getAvgWeightFemaleKg()
        );
    }
}
