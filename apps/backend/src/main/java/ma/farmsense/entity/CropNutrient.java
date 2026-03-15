package ma.farmsense.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "crop_nutrients")
@Access(AccessType.FIELD)
public class CropNutrient {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_id", nullable = false)
    private Crop crop;

    @Enumerated(EnumType.STRING)
    private NutrientLevel nitrogenNeed;

    @Enumerated(EnumType.STRING)
    private NutrientLevel phosphorusNeed;

    @Enumerated(EnumType.STRING)
    private NutrientLevel potassiumNeed;

    private String fertilizerType;
    private String fertilizerTypeAr;
    private String fertilizerTypeEn;
    private String applicationFrequency;

    public CropNutrient() {}

    public UUID getId() { return this.id; }
    public void setId(UUID id) { this.id = id; }

    public Crop getCrop() { return this.crop; }
    public void setCrop(Crop crop) { this.crop = crop; }

    public NutrientLevel getNitrogenNeed() { return this.nitrogenNeed; }
    public void setNitrogenNeed(NutrientLevel nitrogenNeed) { this.nitrogenNeed = nitrogenNeed; }

    public NutrientLevel getPhosphorusNeed() { return this.phosphorusNeed; }
    public void setPhosphorusNeed(NutrientLevel phosphorusNeed) { this.phosphorusNeed = phosphorusNeed; }

    public NutrientLevel getPotassiumNeed() { return this.potassiumNeed; }
    public void setPotassiumNeed(NutrientLevel potassiumNeed) { this.potassiumNeed = potassiumNeed; }

    public String getFertilizerType() { return this.fertilizerType; }
    public void setFertilizerType(String fertilizerType) { this.fertilizerType = fertilizerType; }

    public String getFertilizerTypeAr() { return this.fertilizerTypeAr; }
    public void setFertilizerTypeAr(String fertilizerTypeAr) { this.fertilizerTypeAr = fertilizerTypeAr; }

    public String getFertilizerTypeEn() { return this.fertilizerTypeEn; }
    public void setFertilizerTypeEn(String fertilizerTypeEn) { this.fertilizerTypeEn = fertilizerTypeEn; }

    public String getApplicationFrequency() { return this.applicationFrequency; }
    public void setApplicationFrequency(String applicationFrequency) { this.applicationFrequency = applicationFrequency; }
}
