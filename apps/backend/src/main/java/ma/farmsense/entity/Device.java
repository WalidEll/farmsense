package ma.farmsense.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "devices")
@Access(AccessType.FIELD)
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
    private Integer readIntervalMs = 900000; // 15 minutes

    @Column(nullable = false)
    private Integer soilDryValue = 1024;

    @Column(nullable = false)
    private Integer soilWetValue = 300;

    private Instant claimedAt;
    private Instant lastSeenAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public Device() {}

    public UUID getId() { return this.id; }
    public void setId(UUID id) { this.id = id; }

    public String getDeviceId() { return this.deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public User getUser() { return this.user; }
    public void setUser(User user) { this.user = user; }

    public Plant getPlant() { return this.plant; }
    public void setPlant(Plant plant) { this.plant = plant; }

    public String getLabel() { return this.label; }
    public void setLabel(String label) { this.label = label; }

    public Integer getReadIntervalMs() { return this.readIntervalMs; }
    public void setReadIntervalMs(Integer readIntervalMs) { this.readIntervalMs = readIntervalMs; }

    public Integer getSoilDryValue() { return this.soilDryValue; }
    public void setSoilDryValue(Integer soilDryValue) { this.soilDryValue = soilDryValue; }

    public Integer getSoilWetValue() { return this.soilWetValue; }
    public void setSoilWetValue(Integer soilWetValue) { this.soilWetValue = soilWetValue; }

    public Instant getClaimedAt() { return this.claimedAt; }
    public void setClaimedAt(Instant claimedAt) { this.claimedAt = claimedAt; }

    public Instant getLastSeenAt() { return this.lastSeenAt; }
    public void setLastSeenAt(Instant lastSeenAt) { this.lastSeenAt = lastSeenAt; }

    public Instant getCreatedAt() { return this.createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public boolean isOnline(int thresholdMinutes) {
        if (lastSeenAt == null) return false;
        return lastSeenAt.isAfter(Instant.now().minusSeconds(thresholdMinutes * 60L));
    }
}
