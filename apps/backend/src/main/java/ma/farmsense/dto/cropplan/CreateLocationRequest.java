package ma.farmsense.dto.cropplan;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ma.farmsense.entity.LocationType;

public record CreateLocationRequest(
        @NotBlank String name,
        String nameAr,
        String nameEn,
        String description,
        String descriptionAr,
        String descriptionEn,
        @NotNull LocationType locationType,
        Double areaM2,
        Double latitude,
        Double longitude
) {}
