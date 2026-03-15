package ma.farmsense.dto.plant;

import jakarta.validation.constraints.NotBlank;

public record CreatePlantRequest(
        @NotBlank String name,
        String species,
        String location,
        String photoUrl,
        Integer soilMin,
        Integer soilMax,
        Double tempMin,
        Double tempMax,
        Integer lightMin
) {}
