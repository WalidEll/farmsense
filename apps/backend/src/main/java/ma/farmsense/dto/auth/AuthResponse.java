package ma.farmsense.dto.auth;

import ma.farmsense.entity.User;

import java.util.UUID;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        UUID userId,
        String name,
        String email,
        User.Language lang,
        String phoneWa,
        Double latitude,
        Double longitude
) {}
