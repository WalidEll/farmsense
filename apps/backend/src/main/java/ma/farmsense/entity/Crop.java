package ma.farmsense.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "crops")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Crop {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String nameAr;
    private String nameEn;
    private String scientificName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CropCategory category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String descriptionAr;

    @Column(columnDefinition = "TEXT")
    private String descriptionEn;

    private String imageUrl;
    private String growingSeason;
    private Integer daysToHarvest;

    @Enumerated(EnumType.STRING)
    private CropDifficulty difficulty;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    @Builder.Default
    private Instant updatedAt = Instant.now();

    @PreUpdate
    void onUpdate() { this.updatedAt = Instant.now(); }
}
