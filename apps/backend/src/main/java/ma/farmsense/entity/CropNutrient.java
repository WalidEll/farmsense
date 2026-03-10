package ma.farmsense.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "crop_nutrients")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CropNutrient {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_id", nullable = false)
    private Crop crop;

    @Enumerated(EnumType.STRING)
    private NutrientLevel nitrogenNeed;

    @Enumerated(EnumType.STRING)
    private NutrientLevel phosphorusNeed;

    @Enumerated(EnumType.STRING)
    private NutrientLevel potassiumNeed;

    private String fertilizerType;
    private String fertilizerTypeAr;
    private String fertilizerTypeEn;
    private String applicationFrequency;
}
