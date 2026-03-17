package ma.farmsense.dto.poultry;

import ma.farmsense.entity.HousingLocation;
import ma.farmsense.entity.HousingLocationType;

import java.time.Instant;
import java.util.UUID;

public record HousingLocationResponse(
        UUID id,
        String name,
        String nameAr,
        String nameEn,
        HousingLocationType locationType,
        String notes,
        long currentFlockCount,
        Instant createdAt,
        Instant updatedAt
) {
    public static HousingLocationResponse from(HousingLocation h, long currentFlockCount) {
        return new HousingLocationResponse(
                h.getId(),
                h.getName(),
                h.getNameAr(),
                h.getNameEn(),
                h.getLocationType(),
                h.getNotes(),
                currentFlockCount,
                h.getCreatedAt(),
                h.getUpdatedAt()
        );
    }
}
