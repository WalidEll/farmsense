package ma.farmsense.dto.crop;

import lombok.Builder;
import lombok.Data;
import ma.farmsense.entity.CropNutrient;
import ma.farmsense.entity.NutrientLevel;

import java.util.UUID;

@Data
@Builder
public class CropNutrientResponse {

    private UUID id;
    private NutrientLevel nitrogenNeed;
    private NutrientLevel phosphorusNeed;
    private NutrientLevel potassiumNeed;
    private String fertilizerType;
    private String fertilizerTypeAr;
    private String fertilizerTypeEn;
    private String applicationFrequency;

    public static CropNutrientResponse from(CropNutrient n) {
        return CropNutrientResponse.builder()
                .id(n.getId())
                .nitrogenNeed(n.getNitrogenNeed())
                .phosphorusNeed(n.getPhosphorusNeed())
                .potassiumNeed(n.getPotassiumNeed())
                .fertilizerType(n.getFertilizerType())
                .fertilizerTypeAr(n.getFertilizerTypeAr())
                .fertilizerTypeEn(n.getFertilizerTypeEn())
                .applicationFrequency(n.getApplicationFrequency())
                .build();
    }
}
