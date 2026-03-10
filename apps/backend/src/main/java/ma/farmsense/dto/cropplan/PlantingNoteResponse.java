package ma.farmsense.dto.cropplan;

import lombok.Builder;
import lombok.Data;
import ma.farmsense.entity.PlantingNote;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class PlantingNoteResponse {

    private UUID id;
    private UUID plantingId;
    private String content;
    private Instant createdAt;

    public static PlantingNoteResponse from(PlantingNote n) {
        return PlantingNoteResponse.builder()
                .id(n.getId())
                .plantingId(n.getPlanting().getId())
                .content(n.getContent())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
