package ma.farmsense.dto.device;

import ma.farmsense.entity.Device;

import java.time.Instant;
import java.util.UUID;

public record DeviceResponse(
        UUID id,
        String deviceId,
        String label,
        UUID plantId,
        String plantName,
        boolean online,
        Instant lastSeenAt,
        Instant claimedAt,
        Integer readIntervalMs,
        Integer soilDryValue,
        Integer soilWetValue
) {
    public static DeviceResponse from(Device d, int offlineThresholdMinutes) {
        return new DeviceResponse(
                d.getId(),
                d.getDeviceId(),
                d.getLabel(),
                d.getPlant() != null ? d.getPlant().getId() : null,
                d.getPlant() != null ? d.getPlant().getName() : null,
                d.isOnline(offlineThresholdMinutes),
                d.getLastSeenAt(),
                d.getClaimedAt(),
                d.getReadIntervalMs(),
                d.getSoilDryValue(),
                d.getSoilWetValue()
        );
    }
}
