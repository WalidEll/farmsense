package ma.farmsense.dto.poultry;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ma.farmsense.entity.HousingLocationType;

public record CreateHousingLocationRequest(
        @NotBlank @Size(max = 255) String name,
        @Size(max = 255) String nameAr,
        @Size(max = 255) String nameEn,
        @NotNull HousingLocationType locationType,
        String notes
) {}
