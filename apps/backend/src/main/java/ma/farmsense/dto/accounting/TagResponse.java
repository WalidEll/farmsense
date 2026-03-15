package ma.farmsense.dto.accounting;

import java.util.UUID;

public record TagResponse(
        UUID id,
        String name,
        String color
) {}
