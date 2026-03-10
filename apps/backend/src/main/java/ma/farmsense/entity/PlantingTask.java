package ma.farmsense.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "planting_tasks")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
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
    @Builder.Default
    private PlantingTaskStatus status = PlantingTaskStatus.PENDING;

    private Instant completedAt;
    private String skipReason;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();
}
