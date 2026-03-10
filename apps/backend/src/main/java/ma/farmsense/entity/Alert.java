package ma.farmsense.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "alerts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_id")
    private Plant plant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Severity severity = Severity.MEDIUM;

    private Double sensorValue;

    private String msgEn;
    private String msgFr;
    private String msgAr;

    @Builder.Default private boolean waSent = false;
    @Builder.Default private boolean pushSent = false;

    @Column(nullable = false)
    @Builder.Default
    private Instant triggeredAt = Instant.now();

    private Instant ackAt;

    public enum AlertType {
        SOIL_DRY, SOIL_WET, TEMP_HIGH, TEMP_LOW, LIGHT_LOW, DEVICE_OFFLINE
    }

    public enum Severity { LOW, MEDIUM, HIGH }
}
