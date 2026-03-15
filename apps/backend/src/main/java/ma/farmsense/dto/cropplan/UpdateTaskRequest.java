package ma.farmsense.dto.cropplan;

import jakarta.validation.constraints.NotNull;
import ma.farmsense.entity.PlantingTaskStatus;

public record UpdateTaskRequest(
        @NotNull PlantingTaskStatus status,
        String skipReason
) {}
