package ma.farmsense.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "plantings")
@Access(AccessType.FIELD)
public class Planting {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_plan_id", nullable = false)
    private CropPlan cropPlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_id", nullable = false)
    private Crop crop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farm_location_id")
    private FarmLocation farmLocation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlantingStatus status = PlantingStatus.PLANNED;

    private Integer quantity;
    @Column(name = "area_m2")
    private Double areaM2;

    @Column(columnDefinition = "TEXT")
    private String notes;

    // Planned dates
    private LocalDate plannedSowDate;
    private LocalDate plannedTransplantDate;
    private LocalDate plannedHarvestDate;

    // Actual dates
    private LocalDate actualSowDate;
    private LocalDate actualTransplantDate;
    private LocalDate actualHarvestDate;

    // Yield
    private Double yieldAmount;

    @Enumerated(EnumType.STRING)
    private YieldUnit yieldUnit;

    private Integer qualityRating;

    @Column(columnDefinition = "TEXT")
    private String harvestNotes;

    @Column(columnDefinition = "TEXT")
    private String failureReason;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    public Planting() {}

    @PreUpdate
    void onUpdate() { this.updatedAt = Instant.now(); }

    public UUID getId() { return this.id; }
    public void setId(UUID id) { this.id = id; }

    public CropPlan getCropPlan() { return this.cropPlan; }
    public void setCropPlan(CropPlan cropPlan) { this.cropPlan = cropPlan; }

    public Crop getCrop() { return this.crop; }
    public void setCrop(Crop crop) { this.crop = crop; }

    public FarmLocation getFarmLocation() { return this.farmLocation; }
    public void setFarmLocation(FarmLocation farmLocation) { this.farmLocation = farmLocation; }

    public PlantingStatus getStatus() { return this.status; }
    public void setStatus(PlantingStatus status) { this.status = status; }

    public Integer getQuantity() { return this.quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Double getAreaM2() { return this.areaM2; }
    public void setAreaM2(Double areaM2) { this.areaM2 = areaM2; }

    public String getNotes() { return this.notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDate getPlannedSowDate() { return this.plannedSowDate; }
    public void setPlannedSowDate(LocalDate plannedSowDate) { this.plannedSowDate = plannedSowDate; }

    public LocalDate getPlannedTransplantDate() { return this.plannedTransplantDate; }
    public void setPlannedTransplantDate(LocalDate plannedTransplantDate) { this.plannedTransplantDate = plannedTransplantDate; }

    public LocalDate getPlannedHarvestDate() { return this.plannedHarvestDate; }
    public void setPlannedHarvestDate(LocalDate plannedHarvestDate) { this.plannedHarvestDate = plannedHarvestDate; }

    public LocalDate getActualSowDate() { return this.actualSowDate; }
    public void setActualSowDate(LocalDate actualSowDate) { this.actualSowDate = actualSowDate; }

    public LocalDate getActualTransplantDate() { return this.actualTransplantDate; }
    public void setActualTransplantDate(LocalDate actualTransplantDate) { this.actualTransplantDate = actualTransplantDate; }

    public LocalDate getActualHarvestDate() { return this.actualHarvestDate; }
    public void setActualHarvestDate(LocalDate actualHarvestDate) { this.actualHarvestDate = actualHarvestDate; }

    public Double getYieldAmount() { return this.yieldAmount; }
    public void setYieldAmount(Double yieldAmount) { this.yieldAmount = yieldAmount; }

    public YieldUnit getYieldUnit() { return this.yieldUnit; }
    public void setYieldUnit(YieldUnit yieldUnit) { this.yieldUnit = yieldUnit; }

    public Integer getQualityRating() { return this.qualityRating; }
    public void setQualityRating(Integer qualityRating) { this.qualityRating = qualityRating; }

    public String getHarvestNotes() { return this.harvestNotes; }
    public void setHarvestNotes(String harvestNotes) { this.harvestNotes = harvestNotes; }

    public String getFailureReason() { return this.failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }

    public Instant getCreatedAt() { return this.createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return this.updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
