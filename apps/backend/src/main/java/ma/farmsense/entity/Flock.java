package ma.farmsense.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "flocks")
@Access(AccessType.FIELD)
public class Flock {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "housing_location_id")
    private HousingLocation housingLocation;

    @Column(name = "batch_code", nullable = false)
    private String batchCode = "";

    @Column(nullable = false)
    private String name;

    @Column(name = "name_ar")
    private String nameAr;

    @Column(name = "name_en")
    private String nameEn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "breed_id")
    private Breed breed;

    @Column(nullable = false)
    private Integer birdCount;

    @Column(nullable = false)
    private Integer currentBirdCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FlockPurpose purpose;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FlockStatus status = FlockStatus.ACTIVE;

    private LocalDate startDate;
    private String source;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    public Flock() {}

    @PreUpdate
    void onUpdate() { this.updatedAt = Instant.now(); }

    public UUID getId() { return this.id; }
    public void setId(UUID id) { this.id = id; }

    public User getUser() { return this.user; }
    public void setUser(User user) { this.user = user; }

    public Supplier getSupplier() { return this.supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }

    public HousingLocation getHousingLocation() { return this.housingLocation; }
    public void setHousingLocation(HousingLocation housingLocation) { this.housingLocation = housingLocation; }

    public String getBatchCode() { return this.batchCode; }
    public void setBatchCode(String batchCode) { this.batchCode = batchCode != null ? batchCode : ""; }

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    public String getNameAr() { return this.nameAr; }
    public void setNameAr(String nameAr) { this.nameAr = nameAr; }

    public String getNameEn() { return this.nameEn; }
    public void setNameEn(String nameEn) { this.nameEn = nameEn; }

    public Breed getBreed() { return this.breed; }
    public void setBreed(Breed breed) { this.breed = breed; }

    public Integer getBirdCount() { return this.birdCount; }
    public void setBirdCount(Integer birdCount) { this.birdCount = birdCount; }

    public Integer getCurrentBirdCount() { return this.currentBirdCount; }
    public void setCurrentBirdCount(Integer currentBirdCount) { this.currentBirdCount = currentBirdCount; }

    public FlockPurpose getPurpose() { return this.purpose; }
    public void setPurpose(FlockPurpose purpose) { this.purpose = purpose; }

    public FlockStatus getStatus() { return this.status; }
    public void setStatus(FlockStatus status) { this.status = status; }

    public LocalDate getStartDate() { return this.startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public String getSource() { return this.source; }
    public void setSource(String source) { this.source = source; }

    public String getNotes() { return this.notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Instant getCreatedAt() { return this.createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return this.updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
