package ma.farmsense.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "crop_growth_stages")
@Access(AccessType.FIELD)
public class CropGrowthStage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_id", nullable = false)
    private Crop crop;

    @Column(nullable = false)
    private Integer stageOrder;

    @Column(nullable = false)
    private String name;

    private String nameAr;
    private String nameEn;
    private Integer durationDays;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String descriptionAr;

    @Column(columnDefinition = "TEXT")
    private String descriptionEn;

    public CropGrowthStage() {}

    public UUID getId() { return this.id; }
    public void setId(UUID id) { this.id = id; }

    public Crop getCrop() { return this.crop; }
    public void setCrop(Crop crop) { this.crop = crop; }

    public Integer getStageOrder() { return this.stageOrder; }
    public void setStageOrder(Integer stageOrder) { this.stageOrder = stageOrder; }

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    public String getNameAr() { return this.nameAr; }
    public void setNameAr(String nameAr) { this.nameAr = nameAr; }

    public String getNameEn() { return this.nameEn; }
    public void setNameEn(String nameEn) { this.nameEn = nameEn; }

    public Integer getDurationDays() { return this.durationDays; }
    public void setDurationDays(Integer durationDays) { this.durationDays = durationDays; }

    public String getDescription() { return this.description; }
    public void setDescription(String description) { this.description = description; }

    public String getDescriptionAr() { return this.descriptionAr; }
    public void setDescriptionAr(String descriptionAr) { this.descriptionAr = descriptionAr; }

    public String getDescriptionEn() { return this.descriptionEn; }
    public void setDescriptionEn(String descriptionEn) { this.descriptionEn = descriptionEn; }
}
