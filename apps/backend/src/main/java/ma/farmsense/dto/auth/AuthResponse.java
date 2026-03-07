package ma.farmsense.dto.auth;

import lombok.Builder;
import lombok.Data;
import ma.farmsense.entity.User;

import java.util.UUID;

@Data
@Builder
public class AuthResponse {

    private String accessToken;
    private String refreshToken;

    // Minimal user profile embedded in auth response
    private UUID userId;
    private String name;
    private String email;
    private User.Language lang;
    private String phoneWa;
    private Double latitude;
    private Double longitude;
}
