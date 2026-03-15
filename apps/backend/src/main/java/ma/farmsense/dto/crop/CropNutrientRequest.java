package ma.farmsense.dto.crop;

import ma.farmsense.entity.NutrientLevel;

public record CropNutrientRequest(
        NutrientLevel nitrogenNeed,
        NutrientLevel phosphorusNeed,
        NutrientLevel potassiumNeed,
        String fertilizerType,
        String fertilizerTypeAr,
        String fertilizerTypeEn,
        String applicationFrequency
) {}
