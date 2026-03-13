package ma.farmsense.dto.team;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UpdateTeamRequest {
    @Size(max = 255)
    private String name;
    private String description;
}
