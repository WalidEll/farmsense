package ma.farmsense.dto.sensor;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SensorReadingRequest {

    @NotBlank
    private String deviceId;   // e.g. "FS-00042"

    private UUID plantId;      // resolved from device assignment if null

    private Double temperature;
    private Double humidity;
    private Integer soilMoisture;
    private Double lightLux;

    private Instant recordedAt; // optional; server time used if null
}
