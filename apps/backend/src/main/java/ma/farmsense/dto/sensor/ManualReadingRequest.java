package ma.farmsense.dto.sensor;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ManualReadingRequest {

    @NotNull
    private UUID plantId;

    private Double temperature;
    private Double humidity;
    private Integer soilMoisture;
    private Double lightLux;
}
