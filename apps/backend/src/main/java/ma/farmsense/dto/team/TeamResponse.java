package ma.farmsense.dto.team;

import java.time.Instant;
import java.util.UUID;

public record TeamResponse(
        UUID id,
        UUID ownerId,
        String ownerName,
        String name,
        String description,
        Instant createdAt,
        Instant updatedAt
) {}
