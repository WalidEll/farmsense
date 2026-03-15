package ma.farmsense.dto.alert;

import ma.farmsense.entity.AlertPreference;

public record AlertPreferenceResponse(
        boolean soilDryEnabled,
        boolean soilWetEnabled,
        boolean tempHighEnabled,
        boolean tempLowEnabled,
        boolean lightLowEnabled,
        boolean deviceOfflineEnabled,
        Integer quietHoursStart,
        Integer quietHoursEnd,
        boolean channelWhatsapp,
        boolean channelPush
) {
    public static AlertPreferenceResponse from(AlertPreference p) {
        return new AlertPreferenceResponse(
                p.isSoilDryEnabled(),
                p.isSoilWetEnabled(),
                p.isTempHighEnabled(),
                p.isTempLowEnabled(),
                p.isLightLowEnabled(),
                p.isDeviceOfflineEnabled(),
                p.getQuietHoursStart(),
                p.getQuietHoursEnd(),
                p.isChannelWhatsapp(),
                p.isChannelPush()
        );
    }
}
