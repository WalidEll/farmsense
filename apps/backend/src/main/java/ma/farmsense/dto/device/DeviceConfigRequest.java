package ma.farmsense.dto.device;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeviceConfigRequest {

    @NotNull
    @Min(10000) // Minimum 10 seconds
    private Integer readIntervalMs;

    @NotNull
    @Min(0)
    @Max(1024)
    private Integer soilDryValue;

    @NotNull
    @Min(0)
    @Max(1024)
    private Integer soilWetValue;
}
