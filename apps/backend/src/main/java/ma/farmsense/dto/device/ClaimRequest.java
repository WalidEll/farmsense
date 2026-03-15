package ma.farmsense.dto.device;

import jakarta.validation.constraints.NotBlank;

public record ClaimRequest(
        @NotBlank String deviceId,
        @NotBlank String claimToken
) {}
