package ma.farmsense.dto.sensor;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.UUID;

public record SensorReadingRequest(
        @NotBlank String deviceId,
        UUID plantId,
        Double temperature,
        Double humidity,
        Integer soilMoisture,
        Double lightLux,
        Instant recordedAt
) {}
