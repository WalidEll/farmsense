package ma.farmsense.dto.poultry;

import ma.farmsense.entity.HousingLocation;
import ma.farmsense.entity.HousingLocationStatus;
import ma.farmsense.entity.HousingLocationType;

import java.time.Instant;
import java.util.UUID;

public record HousingLocationResponse(
        UUID id,
        String name,
        String nameAr,
        String nameEn,
        HousingLocationType locationType,
        HousingLocationStatus status,
        Integer capacity,
        String notes,
        long currentFlockCount,
        String currentFlockName,
        Instant createdAt,
        Instant updatedAt
) {
    public static HousingLocationResponse from(HousingLocation h, long currentFlockCount, String currentFlockName) {
        return new HousingLocationResponse(
                h.getId(),
                h.getName(),
                h.getNameAr(),
                h.getNameEn(),
                h.getLocationType(),
                h.getStatus(),
                h.getCapacity(),
                h.getNotes(),
                currentFlockCount,
                currentFlockName,
                h.getCreatedAt(),
                h.getUpdatedAt()
        );
    }
}
