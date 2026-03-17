package ma.farmsense.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "mortality_records")
@Access(AccessType.FIELD)
public class MortalityRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flock_id", nullable = false)
    private Flock flock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MortalityType type;

    @Column(nullable = false)
    private Integer count;

    @Column(name = "mortality_date", nullable = false)
    private LocalDate mortalityDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private MortalityCause cause;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    public MortalityRecord() {}

    @PreUpdate
    void onUpdate() { this.updatedAt = Instant.now(); }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Flock getFlock() { return flock; }
    public void setFlock(Flock flock) { this.flock = flock; }

    public MortalityType getType() { return type; }
    public void setType(MortalityType type) { this.type = type; }

    public Integer getCount() { return count; }
    public void setCount(Integer count) { this.count = count; }

    public LocalDate getMortalityDate() { return mortalityDate; }
    public void setMortalityDate(LocalDate mortalityDate) { this.mortalityDate = mortalityDate; }

    public MortalityCause getCause() { return cause; }
    public void setCause(MortalityCause cause) { this.cause = cause; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
