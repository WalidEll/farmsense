package ma.farmsense.dto.poultry;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import ma.farmsense.entity.MortalityCause;
import ma.farmsense.entity.MortalityType;

import java.time.LocalDate;

public record CreateMortalityEventRequest(
        @NotNull MortalityType type,
        @NotNull @Min(1) Integer count,
        @NotNull LocalDate mortalityDate,
        MortalityCause cause,
        String notes
) {}
