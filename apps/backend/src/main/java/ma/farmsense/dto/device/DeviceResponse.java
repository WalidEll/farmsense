package ma.farmsense.dto.device;

import lombok.Builder;
import lombok.Data;
import ma.farmsense.entity.Device;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class DeviceResponse {

    private UUID id;
    private String deviceId;
    private String label;
    private UUID plantId;
    private String plantName;
    private boolean online;
    private Instant lastSeenAt;
    private Instant claimedAt;
    private Integer readIntervalMs;
    private Integer soilDryValue;
    private Integer soilWetValue;

    public static DeviceResponse from(Device d, int offlineThresholdMinutes) {
        return DeviceResponse.builder()
                .id(d.getId())
                .deviceId(d.getDeviceId())
                .label(d.getLabel())
                .plantId(d.getPlant() != null ? d.getPlant().getId() : null)
                .plantName(d.getPlant() != null ? d.getPlant().getName() : null)
                .online(d.isOnline(offlineThresholdMinutes))
                .lastSeenAt(d.getLastSeenAt())
                .claimedAt(d.getClaimedAt())
                .readIntervalMs(d.getReadIntervalMs())
                .soilDryValue(d.getSoilDryValue())
                .soilWetValue(d.getSoilWetValue())
                .build();
    }
}
