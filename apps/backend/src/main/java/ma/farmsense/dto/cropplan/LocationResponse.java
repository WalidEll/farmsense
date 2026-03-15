package ma.farmsense.dto.cropplan;

import ma.farmsense.entity.FarmLocation;
import ma.farmsense.entity.LocationType;

import java.time.Instant;
import java.util.UUID;

public record LocationResponse(
        UUID id,
        String name,
        String nameAr,
        String nameEn,
        String description,
        String descriptionAr,
        String descriptionEn,
        LocationType locationType,
        Double areaM2,
        Double latitude,
        Double longitude,
        Instant createdAt,
        Instant updatedAt
) {
    public static LocationResponse from(FarmLocation l) {
        return new LocationResponse(
                l.getId(),
                l.getName(),
                l.getNameAr(),
                l.getNameEn(),
                l.getDescription(),
                l.getDescriptionAr(),
                l.getDescriptionEn(),
                l.getLocationType(),
                l.getAreaM2(),
                l.getLatitude(),
                l.getLongitude(),
                l.getCreatedAt(),
                l.getUpdatedAt()
        );
    }
}
