package ma.farmsense.dto.sensor;

import lombok.Builder;
import lombok.Data;
import ma.farmsense.entity.SensorReading;

import java.time.Instant;

@Data
@Builder
public class SensorReadingResponse {

    private Long id;
    private String deviceId;
    private Double temperature;
    private Double humidity;
    private Integer soilMoisture;
    private Double lightLux;
    private String source;
    private Instant recordedAt;

    public static SensorReadingResponse from(SensorReading r) {
        return SensorReadingResponse.builder()
                .id(r.getId())
                .deviceId(r.getDeviceId())
                .temperature(r.getTemperature())
                .humidity(r.getHumidity())
                .soilMoisture(r.getSoilMoisture())
                .lightLux(r.getLightLux())
                .source(r.getSource().name())
                .recordedAt(r.getRecordedAt())
                .build();
    }
}
