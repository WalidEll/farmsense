package ma.farmsense.dto.alert;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class AlertPreferenceRequest {

    private boolean soilDryEnabled = true;
    private boolean soilWetEnabled = true;
    private boolean tempHighEnabled = true;
    private boolean tempLowEnabled = true;
    private boolean lightLowEnabled = true;
    private boolean deviceOfflineEnabled = true;

    @Min(0) @Max(23)
    private Integer quietHoursStart;

    @Min(0) @Max(23)
    private Integer quietHoursEnd;

    private boolean channelWhatsapp = true;
    private boolean channelPush = true;
}
