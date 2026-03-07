package ma.farmsense.dto.device;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClaimRequest {

    @NotBlank
    private String deviceId;   // e.g. FS-00042

    @NotBlank
    private String claimToken; // one-time token from setup code flow
}
