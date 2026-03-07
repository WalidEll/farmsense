package ma.farmsense.dto.device;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class SetupCodeResponse {

    private String code;
    private Instant expiresAt;
}
