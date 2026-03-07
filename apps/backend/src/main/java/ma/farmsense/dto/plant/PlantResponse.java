package ma.farmsense.dto.plant;

import lombok.Builder;
import lombok.Data;
import ma.farmsense.entity.Plant;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class PlantResponse {

    private UUID id;
    private String name;
    private String species;
    private String location;
    private String photoUrl;

    private int soilMin;
    private int soilMax;
    private double tempMin;
    private double tempMax;
    private int lightMin;

    private Instant createdAt;
    private Instant updatedAt;

    // Care schedule fields for mini badges
    private int wateringIntervalDays;
    private int fertilisingIntervalDays;
    private int repottingIntervalDays;
    private Instant lastWateredAt;
    private Instant lastFertilisedAt;
    private Instant lastRepottedAt;

    public static PlantResponse from(Plant p) {
        return PlantResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .species(p.getSpecies())
                .location(p.getLocation())
                .photoUrl(p.getPhotoUrl())
                .soilMin(p.getSoilMin())
                .soilMax(p.getSoilMax())
                .tempMin(p.getTempMin())
                .tempMax(p.getTempMax())
                .lightMin(p.getLightMin())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .wateringIntervalDays(p.getWateringIntervalDays())
                .fertilisingIntervalDays(p.getFertilisingIntervalDays())
                .repottingIntervalDays(p.getRepottingIntervalDays())
                .lastWateredAt(p.getLastWateredAt())
                .lastFertilisedAt(p.getLastFertilisedAt())
                .lastRepottedAt(p.getLastRepottedAt())
                .build();
    }
}
