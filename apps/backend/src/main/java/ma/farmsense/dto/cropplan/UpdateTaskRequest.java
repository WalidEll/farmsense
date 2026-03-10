package ma.farmsense.dto.cropplan;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ma.farmsense.entity.PlantingTaskStatus;

@Data
public class UpdateTaskRequest {

    @NotNull
    private PlantingTaskStatus status;

    private String skipReason;
}
