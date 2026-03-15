package ma.farmsense.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;
import jakarta.validation.constraints.AssertTrue;

@Entity
@Table(name = "feeding_program_templates")
@Access(AccessType.FIELD)
public class FeedingProgramTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "breed_id", nullable = false)
    private Breed breed;

    @Enumerated(EnumType.STRING)
    @Column(name = "growth_stage", nullable = false)
    private GrowthStage growthStage;

    @Column(name = "age_start_days", nullable = false)
    private Integer ageStartDays;

    @Column(name = "age_end_days", nullable = false)
    private Integer ageEndDays;

    @AssertTrue(message = "ageEndDays must be greater than or equal to ageStartDays")
    private boolean isAgeRangeValid() {
        return ageEndDays == null || ageStartDays == null || ageEndDays >= ageStartDays;
    }

    @Column(name = "feed_type", nullable = false)
    private String feedType;

    @Column(name = "daily_quantity_grams", nullable = false, precision = 7, scale = 2)
    private BigDecimal dailyQuantityGrams;

    @Column(name = "feeding_frequency", nullable = false)
    private Integer feedingFrequency = 2;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    public FeedingProgramTemplate() {}

    public UUID getId() { return this.id; }
    public void setId(UUID id) { this.id = id; }

    public Breed getBreed() { return this.breed; }
    public void setBreed(Breed breed) { this.breed = breed; }

    public GrowthStage getGrowthStage() { return this.growthStage; }
    public void setGrowthStage(GrowthStage growthStage) { this.growthStage = growthStage; }

    public Integer getAgeStartDays() { return this.ageStartDays; }
    public void setAgeStartDays(Integer ageStartDays) { this.ageStartDays = ageStartDays; }

    public Integer getAgeEndDays() { return this.ageEndDays; }
    public void setAgeEndDays(Integer ageEndDays) { this.ageEndDays = ageEndDays; }

    public String getFeedType() { return this.feedType; }
    public void setFeedType(String feedType) { this.feedType = feedType; }

    public BigDecimal getDailyQuantityGrams() { return this.dailyQuantityGrams; }
    public void setDailyQuantityGrams(BigDecimal dailyQuantityGrams) { this.dailyQuantityGrams = dailyQuantityGrams; }

    public Integer getFeedingFrequency() { return this.feedingFrequency; }
    public void setFeedingFrequency(Integer feedingFrequency) { this.feedingFrequency = feedingFrequency; }

    public String getNotes() { return this.notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Integer getSortOrder() { return this.sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
}
