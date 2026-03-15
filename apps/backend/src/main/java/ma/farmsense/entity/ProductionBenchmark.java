package ma.farmsense.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "production_benchmarks")
@Access(AccessType.FIELD)
public class ProductionBenchmark {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "breed_id", nullable = false)
    private Breed breed;

    @Column(name = "metric_type", nullable = false)
    private String metricType;

    @Column(name = "expected_value", nullable = false, precision = 10, scale = 4)
    private BigDecimal expectedValue;

    @Column(nullable = false)
    private String unit;

    @Column(name = "age_start_days")
    private Integer ageStartDays;

    @Column(name = "age_end_days")
    private Integer ageEndDays;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    public ProductionBenchmark() {}

    public UUID getId() { return this.id; }
    public void setId(UUID id) { this.id = id; }

    public Breed getBreed() { return this.breed; }
    public void setBreed(Breed breed) { this.breed = breed; }

    public String getMetricType() { return this.metricType; }
    public void setMetricType(String metricType) { this.metricType = metricType; }

    public BigDecimal getExpectedValue() { return this.expectedValue; }
    public void setExpectedValue(BigDecimal expectedValue) { this.expectedValue = expectedValue; }

    public String getUnit() { return this.unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public Integer getAgeStartDays() { return this.ageStartDays; }
    public void setAgeStartDays(Integer ageStartDays) { this.ageStartDays = ageStartDays; }

    public Integer getAgeEndDays() { return this.ageEndDays; }
    public void setAgeEndDays(Integer ageEndDays) { this.ageEndDays = ageEndDays; }

    public String getNotes() { return this.notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Integer getSortOrder() { return this.sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
}
