package ma.farmsense.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "sensor_readings")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
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
    @Builder.Default
    private Source source = Source.SENSOR;

    @Column(nullable = false)
    @Builder.Default
    private Instant recordedAt = Instant.now();

    public enum Source { SENSOR, MANUAL }
}
