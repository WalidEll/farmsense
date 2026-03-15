package ma.farmsense.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "housing_guidelines")
@Access(AccessType.FIELD)
public class HousingGuideline {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "breed_id", nullable = false)
    private Breed breed;

    @Column(name = "parameter_name", nullable = false)
    private String parameterName;

    @Column(name = "recommended_value", nullable = false)
    private String recommendedValue;

    @Column(length = 50)
    private String unit;

    @Enumerated(EnumType.STRING)
    @Column(name = "growth_stage")
    private GrowthStage growthStage;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    public HousingGuideline() {}

    public UUID getId() { return this.id; }
    public void setId(UUID id) { this.id = id; }

    public Breed getBreed() { return this.breed; }
    public void setBreed(Breed breed) { this.breed = breed; }

    public String getParameterName() { return this.parameterName; }
    public void setParameterName(String parameterName) { this.parameterName = parameterName; }

    public String getRecommendedValue() { return this.recommendedValue; }
    public void setRecommendedValue(String recommendedValue) { this.recommendedValue = recommendedValue; }

    public String getUnit() { return this.unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public GrowthStage getGrowthStage() { return this.growthStage; }
    public void setGrowthStage(GrowthStage growthStage) { this.growthStage = growthStage; }

    public String getNotes() { return this.notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Integer getSortOrder() { return this.sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
}
