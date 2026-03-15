package ma.farmsense.dto.cropplan;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record CreatePlantingRequest(
        @NotNull UUID cropId,
        UUID farmLocationId,
        Integer quantity,
        Double areaM2,
        String notes,
        LocalDate plannedSowDate,
        LocalDate plannedTransplantDate,
        LocalDate plannedHarvestDate
) {}
