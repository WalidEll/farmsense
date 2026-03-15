package ma.farmsense.dto.sensor;

import ma.farmsense.entity.SensorReading;

import java.time.Instant;

public record SensorReadingResponse(
        Long id,
        String deviceId,
        Double temperature,
        Double humidity,
        Integer soilMoisture,
        Double lightLux,
        String source,
        Instant recordedAt
) {
    public static SensorReadingResponse from(SensorReading r) {
        return new SensorReadingResponse(
                r.getId(),
                r.getDeviceId(),
                r.getTemperature(),
                r.getHumidity(),
                r.getSoilMoisture(),
                r.getLightLux(),
                r.getSource().name(),
                r.getRecordedAt()
        );
    }
}
