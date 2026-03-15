package ma.farmsense.dto.poultry;

import ma.farmsense.entity.Flock;
import ma.farmsense.entity.FlockPurpose;
import ma.farmsense.entity.FlockStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record FlockResponse(
        UUID id,
        String name,
        String nameAr,
        String nameEn,
        String breed,
        Integer birdCount,
        Integer currentBirdCount,
        FlockPurpose purpose,
        FlockStatus status,
        LocalDate startDate,
        UUID supplierId,
        String supplierName,
        String source,
        String notes,
        Instant createdAt,
        Instant updatedAt
) {
    public static FlockResponse from(Flock f) {
        return new FlockResponse(
                f.getId(),
                f.getName(),
                f.getNameAr(),
                f.getNameEn(),
                f.getBreed(),
                f.getBirdCount(),
                f.getCurrentBirdCount(),
                f.getPurpose(),
                f.getStatus(),
                f.getStartDate(),
                f.getSupplier() != null ? f.getSupplier().getId() : null,
                f.getSupplier() != null ? f.getSupplier().getName() : null,
                f.getSource(),
                f.getNotes(),
                f.getCreatedAt(),
                f.getUpdatedAt()
        );
    }
}
