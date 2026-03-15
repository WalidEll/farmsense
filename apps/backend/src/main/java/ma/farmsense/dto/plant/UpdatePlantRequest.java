package ma.farmsense.dto.plant;

public record UpdatePlantRequest(
        String name,
        String species,
        String location,
        String photoUrl,
        Integer soilMin,
        Integer soilMax,
        Double tempMin,
        Double tempMax,
        Integer lightMin
) {}
