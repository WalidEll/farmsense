package ma.farmsense.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "planting_tasks")
@Access(AccessType.FIELD)
public class PlantingTask {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planting_id", nullable = false)
    private Planting planting;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlantingTaskType taskType;

    @Column(nullable = false)
    private String title;

    private String titleAr;
    private String titleEn;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlantingTaskStatus status = PlantingTaskStatus.PENDING;

    private Instant completedAt;
    private String skipReason;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public PlantingTask() {}

    public UUID getId() { return this.id; }
    public void setId(UUID id) { this.id = id; }

    public Planting getPlanting() { return this.planting; }
    public void setPlanting(Planting planting) { this.planting = planting; }

    public PlantingTaskType getTaskType() { return this.taskType; }
    public void setTaskType(PlantingTaskType taskType) { this.taskType = taskType; }

    public String getTitle() { return this.title; }
    public void setTitle(String title) { this.title = title; }

    public String getTitleAr() { return this.titleAr; }
    public void setTitleAr(String titleAr) { this.titleAr = titleAr; }

    public String getTitleEn() { return this.titleEn; }
    public void setTitleEn(String titleEn) { this.titleEn = titleEn; }

    public LocalDate getDueDate() { return this.dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public PlantingTaskStatus getStatus() { return this.status; }
    public void setStatus(PlantingTaskStatus status) { this.status = status; }

    public Instant getCompletedAt() { return this.completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }

    public String getSkipReason() { return this.skipReason; }
    public void setSkipReason(String skipReason) { this.skipReason = skipReason; }

    public Instant getCreatedAt() { return this.createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
