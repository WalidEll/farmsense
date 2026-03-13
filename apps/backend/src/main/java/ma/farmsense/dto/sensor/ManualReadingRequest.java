package ma.farmsense.dto.sensor;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManualReadingRequest {

    @NotNull
    private UUID plantId;

    private Double temperature;
    private Double humidity;
    private Integer soilMoisture;
    private Double lightLux;
}
