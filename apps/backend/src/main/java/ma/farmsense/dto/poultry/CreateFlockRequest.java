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
        @Size(max = 255) String nameAr,
        @Size(max = 255) String nameEn,
        @Size(max = 255) String breed,
        @NotNull @Min(1) Integer birdCount,
        @NotNull FlockPurpose purpose,
        LocalDate startDate,
        UUID supplierId,
        String source,
        String notes
) {}
