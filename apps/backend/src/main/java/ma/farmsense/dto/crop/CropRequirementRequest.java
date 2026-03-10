package ma.farmsense.dto.crop;

import lombok.Data;

@Data
public class CropRequirementRequest {

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
}
