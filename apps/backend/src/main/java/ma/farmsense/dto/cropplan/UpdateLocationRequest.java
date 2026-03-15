package ma.farmsense.dto.cropplan;

import ma.farmsense.entity.LocationType;

public record UpdateLocationRequest(
        String name,
        String nameAr,
        String nameEn,
        String description,
        String descriptionAr,
        String descriptionEn,
        LocationType locationType,
        Double areaM2,
        Double latitude,
        Double longitude
) {}
