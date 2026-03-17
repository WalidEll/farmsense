package ma.farmsense.dto.poultry;

import jakarta.validation.constraints.Size;
import ma.farmsense.entity.HousingLocationType;

public record UpdateHousingLocationRequest(
        @Size(max = 255) String name,
        @Size(max = 255) String nameAr,
        @Size(max = 255) String nameEn,
        HousingLocationType locationType,
        String notes
) {}
