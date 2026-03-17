package ma.farmsense.dto.poultry;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ma.farmsense.entity.FlockPurpose;

import java.time.LocalDate;
import java.util.UUID;

public record CreateFlockRequest(
        @NotBlank @Size(max = 255) String name,
        @NotBlank @Size(max = 100) String batchCode,
        @Size(max = 255) String nameAr,
        @Size(max = 255) String nameEn,
        UUID breedId,
        @NotNull @Min(1) Integer birdCount,
        @NotNull FlockPurpose purpose,
        LocalDate startDate,
        UUID housingLocationId,
        UUID supplierId,
        String source,
        String notes
) {}
