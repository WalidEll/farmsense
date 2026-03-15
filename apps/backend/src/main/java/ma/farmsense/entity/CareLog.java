package ma.farmsense.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "care_log")
@Access(AccessType.FIELD)
public class CareLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_id", nullable = false)
    private Plant plant;

    @Enumerated(EnumType.STRING)
    @Column(name = "task_type", nullable = false)
    private TaskType taskType;

    @Column(name = "done_at", nullable = false)
    private Instant doneAt = Instant.now();

    private String notes;

    public CareLog() {}

    public UUID getId() { return this.id; }
    public void setId(UUID id) { this.id = id; }

    public Plant getPlant() { return this.plant; }
    public void setPlant(Plant plant) { this.plant = plant; }

    public TaskType getTaskType() { return this.taskType; }
    public void setTaskType(TaskType taskType) { this.taskType = taskType; }

    public Instant getDoneAt() { return this.doneAt; }
    public void setDoneAt(Instant doneAt) { this.doneAt = doneAt; }

    public String getNotes() { return this.notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public enum TaskType { WATER, FERTILISE, REPOT }
}
