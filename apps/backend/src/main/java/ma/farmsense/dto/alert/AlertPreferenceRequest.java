package ma.farmsense.dto.alert;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record AlertPreferenceRequest(
        @JsonProperty(defaultValue = "true") boolean soilDryEnabled,
        @JsonProperty(defaultValue = "true") boolean soilWetEnabled,
        @JsonProperty(defaultValue = "true") boolean tempHighEnabled,
        @JsonProperty(defaultValue = "true") boolean tempLowEnabled,
        @JsonProperty(defaultValue = "true") boolean lightLowEnabled,
        @JsonProperty(defaultValue = "true") boolean deviceOfflineEnabled,
        @Min(0) @Max(23) Integer quietHoursStart,
        @Min(0) @Max(23) Integer quietHoursEnd,
        @JsonProperty(defaultValue = "true") boolean channelWhatsapp,
        @JsonProperty(defaultValue = "true") boolean channelPush
) {}
