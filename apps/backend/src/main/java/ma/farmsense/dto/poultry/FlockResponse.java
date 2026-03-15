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
        UUID breedId,
        String breedName,
        String breedImageUrl,
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
        var breed = f.getBreed();
        var supplier = f.getSupplier();
        
        UUID breedId = breed != null ? breed.getId() : null;
        String breedName = breed != null ? breed.getName() : null;
        String breedImageUrl = breed != null ? breed.getImageUrl() : null;
        
        UUID supplierId = supplier != null ? supplier.getId() : null;
        String supplierName = supplier != null ? supplier.getName() : null;
        
        return new FlockResponse(
                f.getId(),
                f.getName(),
                f.getNameAr(),
                f.getNameEn(),
                breedId,
                breedName,
                breedImageUrl,
                f.getBirdCount(),
                f.getCurrentBirdCount(),
                f.getPurpose(),
                f.getStatus(),
                f.getStartDate(),
                supplierId,
                supplierName,
                f.getSource(),
                f.getNotes(),
                f.getCreatedAt(),
                f.getUpdatedAt()
        );
    }
}
