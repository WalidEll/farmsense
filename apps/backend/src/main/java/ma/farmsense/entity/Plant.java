package ma.farmsense.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "plants")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Plant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    private String species;
    private String location;
    private String photoUrl;

    // Sensor thresholds — pre-filled by species, overridable
    @Builder.Default private int soilMin = 30;
    @Builder.Default private int soilMax = 80;
    @Builder.Default private double tempMin = 15.0;
    @Builder.Default private double tempMax = 30.0;
    @Builder.Default private int lightMin = 500;

    // Care schedule — intervals (days) + last-done timestamps
    @Builder.Default private int wateringIntervalDays = 7;
    @Builder.Default private int fertilisingIntervalDays = 30;
    @Builder.Default private int repottingIntervalDays = 180;
    private Instant lastWateredAt;
    private Instant lastFertilisedAt;
    private Instant lastRepottedAt;

    private Instant deletedAt;  // soft delete

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    @Builder.Default
    private Instant updatedAt = Instant.now();

    @PreUpdate
    void onUpdate() { this.updatedAt = Instant.now(); }

    public boolean isDeleted() { return deletedAt != null; }
}
