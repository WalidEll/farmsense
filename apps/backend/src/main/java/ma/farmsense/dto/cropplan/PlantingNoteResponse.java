package ma.farmsense.dto.cropplan;

import ma.farmsense.entity.PlantingNote;

import java.time.Instant;
import java.util.UUID;

public record PlantingNoteResponse(
        UUID id,
        UUID plantingId,
        String content,
        Instant createdAt
) {
    public static PlantingNoteResponse from(PlantingNote n) {
        return new PlantingNoteResponse(
                n.getId(),
                n.getPlanting().getId(),
                n.getContent(),
                n.getCreatedAt()
        );
    }
}
