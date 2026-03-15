package ma.farmsense.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "plants")
@Access(AccessType.FIELD)
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
    private int soilMin = 30;
    private int soilMax = 80;
    private double tempMin = 15.0;
    private double tempMax = 30.0;
    private int lightMin = 500;

    // Care schedule — intervals (days) + last-done timestamps
    private int wateringIntervalDays = 7;
    private int fertilisingIntervalDays = 30;
    private int repottingIntervalDays = 180;
    private Instant lastWateredAt;
    private Instant lastFertilisedAt;
    private Instant lastRepottedAt;

    private Instant deletedAt;  // soft delete

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    public Plant() {}

    @PreUpdate
    void onUpdate() { this.updatedAt = Instant.now(); }

    public UUID getId() { return this.id; }
    public void setId(UUID id) { this.id = id; }

    public User getUser() { return this.user; }
    public void setUser(User user) { this.user = user; }

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    public String getSpecies() { return this.species; }
    public void setSpecies(String species) { this.species = species; }

    public String getLocation() { return this.location; }
    public void setLocation(String location) { this.location = location; }

    public String getPhotoUrl() { return this.photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public int getSoilMin() { return this.soilMin; }
    public void setSoilMin(int soilMin) { this.soilMin = soilMin; }

    public int getSoilMax() { return this.soilMax; }
    public void setSoilMax(int soilMax) { this.soilMax = soilMax; }

    public double getTempMin() { return this.tempMin; }
    public void setTempMin(double tempMin) { this.tempMin = tempMin; }

    public double getTempMax() { return this.tempMax; }
    public void setTempMax(double tempMax) { this.tempMax = tempMax; }

    public int getLightMin() { return this.lightMin; }
    public void setLightMin(int lightMin) { this.lightMin = lightMin; }

    public int getWateringIntervalDays() { return this.wateringIntervalDays; }
    public void setWateringIntervalDays(int wateringIntervalDays) { this.wateringIntervalDays = wateringIntervalDays; }

    public int getFertilisingIntervalDays() { return this.fertilisingIntervalDays; }
    public void setFertilisingIntervalDays(int fertilisingIntervalDays) { this.fertilisingIntervalDays = fertilisingIntervalDays; }

    public int getRepottingIntervalDays() { return this.repottingIntervalDays; }
    public void setRepottingIntervalDays(int repottingIntervalDays) { this.repottingIntervalDays = repottingIntervalDays; }

    public Instant getLastWateredAt() { return this.lastWateredAt; }
    public void setLastWateredAt(Instant lastWateredAt) { this.lastWateredAt = lastWateredAt; }

    public Instant getLastFertilisedAt() { return this.lastFertilisedAt; }
    public void setLastFertilisedAt(Instant lastFertilisedAt) { this.lastFertilisedAt = lastFertilisedAt; }

    public Instant getLastRepottedAt() { return this.lastRepottedAt; }
    public void setLastRepottedAt(Instant lastRepottedAt) { this.lastRepottedAt = lastRepottedAt; }

    public Instant getDeletedAt() { return this.deletedAt; }
    public void setDeletedAt(Instant deletedAt) { this.deletedAt = deletedAt; }

    public Instant getCreatedAt() { return this.createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return this.updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public boolean isDeleted() { return deletedAt != null; }
}
