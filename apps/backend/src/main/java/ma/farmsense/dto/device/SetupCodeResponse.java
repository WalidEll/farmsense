package ma.farmsense.dto.device;

import java.time.Instant;

public record SetupCodeResponse(
        String code,
        Instant expiresAt
) {}
