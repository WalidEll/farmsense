package ma.farmsense.dto.poultry;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import ma.farmsense.entity.FlockPurpose;
import ma.farmsense.entity.FlockStatus;

import java.time.LocalDate;
import java.util.UUID;

public record UpdateFlockRequest(
        @Size(max = 255) String name,
        @Size(max = 255) String nameAr,
        @Size(max = 255) String nameEn,
        UUID breedId,
        @Min(1) Integer birdCount,
        FlockPurpose purpose,
        FlockStatus status,
        LocalDate startDate,
        UUID supplierId,
        String source,
        String notes
) {}
