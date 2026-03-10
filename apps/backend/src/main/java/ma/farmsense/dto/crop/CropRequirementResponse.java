package ma.farmsense.dto.crop;

import lombok.Builder;
import lombok.Data;
import ma.farmsense.entity.CropRequirement;

import java.util.UUID;

@Data
@Builder
public class CropRequirementResponse {

    private UUID id;
    private Integer soilMoistureMin;
    private Integer soilMoistureMax;
    private Double tempMin;
    private Double tempMax;
    private Integer lightMin;
    private Integer lightMax;
    private Double humidityMin;
    private Double humidityMax;
    private String soilType;
    private Double phMin;
    private Double phMax;
    private String waterFrequency;

    public static CropRequirementResponse from(CropRequirement r) {
        return CropRequirementResponse.builder()
                .id(r.getId())
                .soilMoistureMin(r.getSoilMoistureMin())
                .soilMoistureMax(r.getSoilMoistureMax())
                .tempMin(r.getTempMin())
                .tempMax(r.getTempMax())
                .lightMin(r.getLightMin())
                .lightMax(r.getLightMax())
                .humidityMin(r.getHumidityMin())
                .humidityMax(r.getHumidityMax())
                .soilType(r.getSoilType())
                .phMin(r.getPhMin())
                .phMax(r.getPhMax())
                .waterFrequency(r.getWaterFrequency())
                .build();
    }
}
