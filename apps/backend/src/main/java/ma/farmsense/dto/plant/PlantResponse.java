package ma.farmsense.dto.plant;

import ma.farmsense.entity.Plant;

import java.time.Instant;
import java.util.UUID;

public record PlantResponse(
        UUID id,
        String name,
        String species,
        String location,
        String photoUrl,
        int soilMin,
        int soilMax,
        double tempMin,
        double tempMax,
        int lightMin,
        Instant createdAt,
        Instant updatedAt,
        int wateringIntervalDays,
        int fertilisingIntervalDays,
        int repottingIntervalDays,
        Instant lastWateredAt,
        Instant lastFertilisedAt,
        Instant lastRepottedAt
) {
    public static PlantResponse from(Plant p) {
        return new PlantResponse(
                p.getId(),
                p.getName(),
                p.getSpecies(),
                p.getLocation(),
                p.getPhotoUrl(),
                p.getSoilMin(),
                p.getSoilMax(),
                p.getTempMin(),
                p.getTempMax(),
                p.getLightMin(),
                p.getCreatedAt(),
                p.getUpdatedAt(),
                p.getWateringIntervalDays(),
                p.getFertilisingIntervalDays(),
                p.getRepottingIntervalDays(),
                p.getLastWateredAt(),
                p.getLastFertilisedAt(),
                p.getLastRepottedAt()
        );
    }
}
