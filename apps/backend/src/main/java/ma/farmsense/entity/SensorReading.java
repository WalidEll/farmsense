package ma.farmsense.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "sensor_readings")
@Access(AccessType.FIELD)
public class SensorReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String deviceId;   // e.g. "FS-00042"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_id")
    private Plant plant;

    private Double temperature;   // °C
    private Double humidity;      // %
    private Integer soilMoisture; // 0-100
    private Double lightLux;      // lux

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Source source = Source.SENSOR;

    @Column(nullable = false)
    private Instant recordedAt = Instant.now();

    public SensorReading() {}

    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }

    public String getDeviceId() { return this.deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public Plant getPlant() { return this.plant; }
    public void setPlant(Plant plant) { this.plant = plant; }

    public Double getTemperature() { return this.temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }

    public Double getHumidity() { return this.humidity; }
    public void setHumidity(Double humidity) { this.humidity = humidity; }

    public Integer getSoilMoisture() { return this.soilMoisture; }
    public void setSoilMoisture(Integer soilMoisture) { this.soilMoisture = soilMoisture; }

    public Double getLightLux() { return this.lightLux; }
    public void setLightLux(Double lightLux) { this.lightLux = lightLux; }

    public Source getSource() { return this.source; }
    public void setSource(Source source) { this.source = source; }

    public Instant getRecordedAt() { return this.recordedAt; }
    public void setRecordedAt(Instant recordedAt) { this.recordedAt = recordedAt; }

    public enum Source { SENSOR, MANUAL }
}
