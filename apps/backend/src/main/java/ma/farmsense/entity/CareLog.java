package ma.farmsense.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "care_log")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
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
    @Builder.Default
    private Instant doneAt = Instant.now();

    private String notes;

    public enum TaskType { WATER, FERTILISE, REPOT }
}
