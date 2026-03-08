package ma.farmsense.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "alert_preferences")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AlertPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Builder.Default
    private boolean soilDryEnabled = true;

    @Builder.Default
    private boolean soilWetEnabled = true;

    @Builder.Default
    private boolean tempHighEnabled = true;

    @Builder.Default
    private boolean tempLowEnabled = true;

    @Builder.Default
    private boolean lightLowEnabled = true;

    @Builder.Default
    private boolean deviceOfflineEnabled = true;

    /** Quiet hours start (0-23), nullable means no quiet hours */
    private Integer quietHoursStart;

    /** Quiet hours end (0-23), nullable means no quiet hours */
    private Integer quietHoursEnd;

    @Builder.Default
    private boolean channelWhatsapp = true;

    @Builder.Default
    private boolean channelPush = true;
}
