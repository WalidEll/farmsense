package ma.farmsense.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "crop_issues")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CropIssue {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_id", nullable = false)
    private Crop crop;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IssueType issueType;

    @Column(nullable = false)
    private String name;

    private String nameAr;
    private String nameEn;

    @Column(columnDefinition = "TEXT")
    private String symptoms;

    @Column(columnDefinition = "TEXT")
    private String symptomsAr;

    @Column(columnDefinition = "TEXT")
    private String symptomsEn;

    @Column(columnDefinition = "TEXT")
    private String treatment;

    @Column(columnDefinition = "TEXT")
    private String treatmentAr;

    @Column(columnDefinition = "TEXT")
    private String treatmentEn;

    @Column(columnDefinition = "TEXT")
    private String prevention;
}
