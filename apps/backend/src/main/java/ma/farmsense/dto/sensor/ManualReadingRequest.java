package ma.farmsense.dto.sensor;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ManualReadingRequest(
        @NotNull UUID plantId,
        Double temperature,
        Double humidity,
        Integer soilMoisture,
        Double lightLux
) {}
