package ma.farmsense.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "crop_requirements")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CropRequirement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_id", nullable = false)
    private Crop crop;

    private Integer soilMoistureMin;
    private Integer soilMoistureMax;
    private Double tempMin;
    private Double tempMax;
    private Integer lightMin;
    private Integer lightMax;
    private Double humidityMin;
    private Double humidityMax;
    private String soilType;
    private Double phMin;
    private Double phMax;
    private String waterFrequency;
}
