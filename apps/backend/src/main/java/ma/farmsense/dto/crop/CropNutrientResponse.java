package ma.farmsense.dto.crop;

import ma.farmsense.entity.CropNutrient;
import ma.farmsense.entity.NutrientLevel;

import java.util.UUID;

public record CropNutrientResponse(
        UUID id,
        NutrientLevel nitrogenNeed,
        NutrientLevel phosphorusNeed,
        NutrientLevel potassiumNeed,
        String fertilizerType,
        String fertilizerTypeAr,
        String fertilizerTypeEn,
        String applicationFrequency
) {
    public static CropNutrientResponse from(CropNutrient n) {
        return new CropNutrientResponse(
                n.getId(),
                n.getNitrogenNeed(),
                n.getPhosphorusNeed(),
                n.getPotassiumNeed(),
                n.getFertilizerType(),
                n.getFertilizerTypeAr(),
                n.getFertilizerTypeEn(),
                n.getApplicationFrequency()
        );
    }
}
