package ma.farmsense.dto.cropplan;

import lombok.Builder;
import lombok.Data;
import ma.farmsense.entity.FarmLocation;
import ma.farmsense.entity.LocationType;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class LocationResponse {

    private UUID id;
    private String name;
    private String nameAr;
    private String nameEn;
    private String description;
    private String descriptionAr;
    private String descriptionEn;
    private LocationType locationType;
    private Double areaM2;
    private Double latitude;
    private Double longitude;
    private Instant createdAt;
    private Instant updatedAt;

    public static LocationResponse from(FarmLocation l) {
        return LocationResponse.builder()
                .id(l.getId())
                .name(l.getName())
                .nameAr(l.getNameAr())
                .nameEn(l.getNameEn())
                .description(l.getDescription())
                .descriptionAr(l.getDescriptionAr())
                .descriptionEn(l.getDescriptionEn())
                .locationType(l.getLocationType())
                .areaM2(l.getAreaM2())
                .latitude(l.getLatitude())
                .longitude(l.getLongitude())
                .createdAt(l.getCreatedAt())
                .updatedAt(l.getUpdatedAt())
                .build();
    }
}
