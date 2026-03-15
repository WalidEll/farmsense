package ma.farmsense.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "crops")
@Access(AccessType.FIELD)
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
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    public Crop() {}

    @PreUpdate
    void onUpdate() { this.updatedAt = Instant.now(); }

    public UUID getId() { return this.id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    public String getNameAr() { return this.nameAr; }
    public void setNameAr(String nameAr) { this.nameAr = nameAr; }

    public String getNameEn() { return this.nameEn; }
    public void setNameEn(String nameEn) { this.nameEn = nameEn; }

    public String getScientificName() { return this.scientificName; }
    public void setScientificName(String scientificName) { this.scientificName = scientificName; }

    public CropCategory getCategory() { return this.category; }
    public void setCategory(CropCategory category) { this.category = category; }

    public String getDescription() { return this.description; }
    public void setDescription(String description) { this.description = description; }

    public String getDescriptionAr() { return this.descriptionAr; }
    public void setDescriptionAr(String descriptionAr) { this.descriptionAr = descriptionAr; }

    public String getDescriptionEn() { return this.descriptionEn; }
    public void setDescriptionEn(String descriptionEn) { this.descriptionEn = descriptionEn; }

    public String getImageUrl() { return this.imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getGrowingSeason() { return this.growingSeason; }
    public void setGrowingSeason(String growingSeason) { this.growingSeason = growingSeason; }

    public Integer getDaysToHarvest() { return this.daysToHarvest; }
    public void setDaysToHarvest(Integer daysToHarvest) { this.daysToHarvest = daysToHarvest; }

    public CropDifficulty getDifficulty() { return this.difficulty; }
    public void setDifficulty(CropDifficulty difficulty) { this.difficulty = difficulty; }

    public Instant getCreatedAt() { return this.createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return this.updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
