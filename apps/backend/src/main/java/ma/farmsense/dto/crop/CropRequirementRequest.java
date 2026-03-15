package ma.farmsense.dto.crop;

public record CropRequirementRequest(
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
) {}
