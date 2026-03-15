package ma.farmsense.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "labor_logs")
@Access(AccessType.FIELD)
public class LaborLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Column(nullable = false)
    private String workerName;

    private String workerRole;

    @Column(nullable = false)
    private BigDecimal hourlyRate;

    @Column(nullable = false)
    private Double hoursWorked;

    @Column(nullable = false)
    private LocalDate workDate;

    private String activity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flock_id")
    private Flock flock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_plan_id")
    private CropPlan cropPlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farm_location_id")
    private FarmLocation farmLocation;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    public LaborLog() {}

    @PreUpdate
    void onUpdate() { this.updatedAt = Instant.now(); }

    public UUID getId() { return this.id; }
    public void setId(UUID id) { this.id = id; }

    public User getUser() { return this.user; }
    public void setUser(User user) { this.user = user; }

    public Team getTeam() { return this.team; }
    public void setTeam(Team team) { this.team = team; }

    public String getWorkerName() { return this.workerName; }
    public void setWorkerName(String workerName) { this.workerName = workerName; }

    public String getWorkerRole() { return this.workerRole; }
    public void setWorkerRole(String workerRole) { this.workerRole = workerRole; }

    public BigDecimal getHourlyRate() { return this.hourlyRate; }
    public void setHourlyRate(BigDecimal hourlyRate) { this.hourlyRate = hourlyRate; }

    public Double getHoursWorked() { return this.hoursWorked; }
    public void setHoursWorked(Double hoursWorked) { this.hoursWorked = hoursWorked; }

    public LocalDate getWorkDate() { return this.workDate; }
    public void setWorkDate(LocalDate workDate) { this.workDate = workDate; }

    public String getActivity() { return this.activity; }
    public void setActivity(String activity) { this.activity = activity; }

    public Transaction getTransaction() { return this.transaction; }
    public void setTransaction(Transaction transaction) { this.transaction = transaction; }

    public Flock getFlock() { return this.flock; }
    public void setFlock(Flock flock) { this.flock = flock; }

    public CropPlan getCropPlan() { return this.cropPlan; }
    public void setCropPlan(CropPlan cropPlan) { this.cropPlan = cropPlan; }

    public FarmLocation getFarmLocation() { return this.farmLocation; }
    public void setFarmLocation(FarmLocation farmLocation) { this.farmLocation = farmLocation; }

    public String getNotes() { return this.notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Instant getCreatedAt() { return this.createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return this.updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
