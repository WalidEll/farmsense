package ma.farmsense.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "plantings")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
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
    @Builder.Default
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
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    @Builder.Default
    private Instant updatedAt = Instant.now();

    @PreUpdate
    void onUpdate() { this.updatedAt = Instant.now(); }
}
