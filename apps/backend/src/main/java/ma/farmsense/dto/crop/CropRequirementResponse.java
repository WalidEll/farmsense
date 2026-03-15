package ma.farmsense.dto.crop;

import ma.farmsense.entity.CropRequirement;

import java.util.UUID;

public record CropRequirementResponse(
        UUID id,
        Integer soilMoistureMin,
        Integer soilMoistureMax,
        Double tempMin,
        Double tempMax,
        Integer lightMin,
        Integer lightMax,
        Double humidityMin,
        Double humidityMax,
        String soilType,
        Double phMin,
        Double phMax,
        String waterFrequency
) {
    public static CropRequirementResponse from(CropRequirement r) {
        return new CropRequirementResponse(
                r.getId(),
                r.getSoilMoistureMin(),
                r.getSoilMoistureMax(),
                r.getTempMin(),
                r.getTempMax(),
                r.getLightMin(),
                r.getLightMax(),
                r.getHumidityMin(),
                r.getHumidityMax(),
                r.getSoilType(),
                r.getPhMin(),
                r.getPhMax(),
                r.getWaterFrequency()
        );
    }
}
