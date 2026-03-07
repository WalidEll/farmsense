package ma.farmsense.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "devices")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String deviceId;   // e.g. FS-00042 — set at manufacturing

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_id")
    private Plant plant;

    private String label;        // user-defined: "Living Room Lily"
    
    // Configuration
    @Column(nullable = false)
    @Builder.Default
    private Integer readIntervalMs = 900000; // 15 minutes
    
    @Column(nullable = false)
    @Builder.Default
    private Integer soilDryValue = 1024;
    
    @Column(nullable = false)
    @Builder.Default
    private Integer soilWetValue = 300;

    private Instant claimedAt;
    private Instant lastSeenAt;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    public boolean isOnline(int thresholdMinutes) {
        if (lastSeenAt == null) return false;
        return lastSeenAt.isAfter(Instant.now().minusSeconds(thresholdMinutes * 60L));
    }
}
