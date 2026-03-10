package ma.farmsense.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "diagnoses")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Diagnosis {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_id")
    private Plant plant;

    @Column(nullable = false)
    @Builder.Default
    private String photoUrl = "INLINE"; // set when photo is persisted to storage

    private String problemName;
    private String severity; // LOW / MEDIUM / HIGH

    @Column(columnDefinition = "TEXT")
    private String treatmentFr;

    @Column(columnDefinition = "TEXT")
    private String treatmentAr;

    @Column(columnDefinition = "TEXT")
    private String treatmentEn;

    @Column(columnDefinition = "TEXT")
    private String prevention;

    @Column(columnDefinition = "TEXT")
    private String rawResponse;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();
}
