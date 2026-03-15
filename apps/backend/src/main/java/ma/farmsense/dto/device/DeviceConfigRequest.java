package ma.farmsense.dto.device;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record DeviceConfigRequest(
        @NotNull @Min(10000) Integer readIntervalMs,
        @NotNull @Min(0) @Max(1024) Integer soilDryValue,
        @NotNull @Min(0) @Max(1024) Integer soilWetValue
) {}
