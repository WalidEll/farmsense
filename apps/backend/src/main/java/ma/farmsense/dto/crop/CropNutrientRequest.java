package ma.farmsense.dto.crop;

import lombok.Data;
import ma.farmsense.entity.NutrientLevel;

@Data
public class CropNutrientRequest {

    private NutrientLevel nitrogenNeed;
    private NutrientLevel phosphorusNeed;
    private NutrientLevel potassiumNeed;
    private String fertilizerType;
    private String fertilizerTypeAr;
    private String fertilizerTypeEn;
    private String applicationFrequency;
}
