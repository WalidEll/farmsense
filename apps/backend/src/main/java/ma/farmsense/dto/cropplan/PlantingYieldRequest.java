package ma.farmsense.dto.cropplan;

import jakarta.validation.constraints.NotNull;
import ma.farmsense.entity.YieldUnit;

public record PlantingYieldRequest(
        @NotNull Double yieldAmount,
        @NotNull YieldUnit yieldUnit,
        Integer qualityRating,
        String harvestNotes
) {}
