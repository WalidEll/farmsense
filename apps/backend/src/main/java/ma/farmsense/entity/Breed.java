package ma.farmsense.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "breeds")
@Access(AccessType.FIELD)
public class Breed {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(name = "name_ar")
    private String nameAr;

    @Column(name = "name_en")
    private String nameEn;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BreedCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BreedPurpose purpose;

    private String origin;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "description_ar", columnDefinition = "TEXT")
    private String descriptionAr;

    @Column(name = "description_en", columnDefinition = "TEXT")
    private String descriptionEn;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "climate_suitability")
    private String climateSuitability;

    @Column(name = "avg_weight_male_kg", precision = 5, scale = 2)
    private BigDecimal avgWeightMaleKg;

    @Column(name = "avg_weight_female_kg", precision = 5, scale = 2)
    private BigDecimal avgWeightFemaleKg;

    @Column(nullable = false)
    private Boolean isSystem = true;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    @OneToMany(mappedBy = "breed", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeedingProgramTemplate> feedingPrograms = new ArrayList<>();

    @OneToMany(mappedBy = "breed", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VaccinationTemplate> vaccinationSchedule = new ArrayList<>();

    @OneToMany(mappedBy = "breed", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductionBenchmark> productionBenchmarks = new ArrayList<>();

    @OneToMany(mappedBy = "breed", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HousingGuideline> housingGuidelines = new ArrayList<>();

    public Breed() {}

    @PreUpdate
    void onUpdate() { this.updatedAt = Instant.now(); }

    public UUID getId() { return this.id; }
    public void setId(UUID id) { this.id = id; }

    public User getUser() { return this.user; }
    public void setUser(User user) { this.user = user; }

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    public String getNameAr() { return this.nameAr; }
    public void setNameAr(String nameAr) { this.nameAr = nameAr; }

    public String getNameEn() { return this.nameEn; }
    public void setNameEn(String nameEn) { this.nameEn = nameEn; }

    public BreedCategory getCategory() { return this.category; }
    public void setCategory(BreedCategory category) { this.category = category; }

    public BreedPurpose getPurpose() { return this.purpose; }
    public void setPurpose(BreedPurpose purpose) { this.purpose = purpose; }

    public String getOrigin() { return this.origin; }
    public void setOrigin(String origin) { this.origin = origin; }

    public String getDescription() { return this.description; }
    public void setDescription(String description) { this.description = description; }

    public String getDescriptionAr() { return this.descriptionAr; }
    public void setDescriptionAr(String descriptionAr) { this.descriptionAr = descriptionAr; }

    public String getDescriptionEn() { return this.descriptionEn; }
    public void setDescriptionEn(String descriptionEn) { this.descriptionEn = descriptionEn; }

    public String getImageUrl() { return this.imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getClimateSuitability() { return this.climateSuitability; }
    public void setClimateSuitability(String climateSuitability) { this.climateSuitability = climateSuitability; }

    public BigDecimal getAvgWeightMaleKg() { return this.avgWeightMaleKg; }
    public void setAvgWeightMaleKg(BigDecimal avgWeightMaleKg) { this.avgWeightMaleKg = avgWeightMaleKg; }

    public BigDecimal getAvgWeightFemaleKg() { return this.avgWeightFemaleKg; }
    public void setAvgWeightFemaleKg(BigDecimal avgWeightFemaleKg) { this.avgWeightFemaleKg = avgWeightFemaleKg; }

    public Boolean getIsSystem() { return this.isSystem; }
    public void setIsSystem(Boolean isSystem) { this.isSystem = isSystem; }

    public Instant getCreatedAt() { return this.createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return this.updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public List<FeedingProgramTemplate> getFeedingPrograms() { return this.feedingPrograms; }
    public void setFeedingPrograms(List<FeedingProgramTemplate> feedingPrograms) { this.feedingPrograms = feedingPrograms; }

    public List<VaccinationTemplate> getVaccinationSchedule() { return this.vaccinationSchedule; }
    public void setVaccinationSchedule(List<VaccinationTemplate> vaccinationSchedule) { this.vaccinationSchedule = vaccinationSchedule; }

    public List<ProductionBenchmark> getProductionBenchmarks() { return this.productionBenchmarks; }
    public void setProductionBenchmarks(List<ProductionBenchmark> productionBenchmarks) { this.productionBenchmarks = productionBenchmarks; }

    public List<HousingGuideline> getHousingGuidelines() { return this.housingGuidelines; }
    public void setHousingGuidelines(List<HousingGuideline> housingGuidelines) { this.housingGuidelines = housingGuidelines; }
}
