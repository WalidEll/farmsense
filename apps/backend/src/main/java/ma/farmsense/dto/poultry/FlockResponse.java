package ma.farmsense.dto.poultry;

import ma.farmsense.entity.Flock;
import ma.farmsense.entity.FlockPurpose;
import ma.farmsense.entity.FlockStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public record FlockResponse(
        UUID id,
        String name,
        String batchCode,
        String nameAr,
        String nameEn,
        UUID breedId,
        String breedName,
        String breedImageUrl,
        Integer birdCount,
        Integer currentBirdCount,
        Integer ageWeeks,
        FlockPurpose purpose,
        FlockStatus status,
        LocalDate startDate,
        UUID housingLocationId,
        String housingLocationName,
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
        var housing = f.getHousingLocation();

        UUID breedId = breed != null ? breed.getId() : null;
        String breedName = breed != null ? breed.getName() : null;
        String breedImageUrl = breed != null ? breed.getImageUrl() : null;

        UUID supplierId = supplier != null ? supplier.getId() : null;
        String supplierName = supplier != null ? supplier.getName() : null;

        UUID housingLocationId = housing != null ? housing.getId() : null;
        String housingLocationName = housing != null ? housing.getName() : null;

        Integer ageWeeks = null;
        if (f.getStartDate() != null) {
            long weeks = ChronoUnit.WEEKS.between(f.getStartDate(), LocalDate.now());
            ageWeeks = (int) Math.max(0, weeks);
        }

        return new FlockResponse(
                f.getId(),
                f.getName(),
                f.getBatchCode(),
                f.getNameAr(),
                f.getNameEn(),
                breedId,
                breedName,
                breedImageUrl,
                f.getBirdCount(),
                f.getCurrentBirdCount(),
                ageWeeks,
                f.getPurpose(),
                f.getStatus(),
                f.getStartDate(),
                housingLocationId,
                housingLocationName,
                supplierId,
                supplierName,
                f.getSource(),
                f.getNotes(),
                f.getCreatedAt(),
                f.getUpdatedAt()
        );
    }
}
