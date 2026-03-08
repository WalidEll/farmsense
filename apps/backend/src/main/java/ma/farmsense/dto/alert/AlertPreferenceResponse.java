package ma.farmsense.dto.alert;

import lombok.Builder;
import lombok.Data;
import ma.farmsense.entity.AlertPreference;

@Data
@Builder
public class AlertPreferenceResponse {

    private boolean soilDryEnabled;
    private boolean soilWetEnabled;
    private boolean tempHighEnabled;
    private boolean tempLowEnabled;
    private boolean lightLowEnabled;
    private boolean deviceOfflineEnabled;
    private Integer quietHoursStart;
    private Integer quietHoursEnd;
    private boolean channelWhatsapp;
    private boolean channelPush;

    public static AlertPreferenceResponse from(AlertPreference p) {
        return AlertPreferenceResponse.builder()
                .soilDryEnabled(p.isSoilDryEnabled())
                .soilWetEnabled(p.isSoilWetEnabled())
                .tempHighEnabled(p.isTempHighEnabled())
                .tempLowEnabled(p.isTempLowEnabled())
                .lightLowEnabled(p.isLightLowEnabled())
                .deviceOfflineEnabled(p.isDeviceOfflineEnabled())
                .quietHoursStart(p.getQuietHoursStart())
                .quietHoursEnd(p.getQuietHoursEnd())
                .channelWhatsapp(p.isChannelWhatsapp())
                .channelPush(p.isChannelPush())
                .build();
    }
}
