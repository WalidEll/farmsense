package ma.farmsense.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "crop_growth_stages")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CropGrowthStage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_id", nullable = false)
    private Crop crop;

    @Column(nullable = false)
    private Integer stageOrder;

    @Column(nullable = false)
    private String name;

    private String nameAr;
    private String nameEn;
    private Integer durationDays;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String descriptionAr;

    @Column(columnDefinition = "TEXT")
    private String descriptionEn;
}
