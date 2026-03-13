package ma.farmsense.dto.team;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.farmsense.entity.TeamRole;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class InviteMemberRequest {
    @NotBlank @Email
    private String email;
    @NotNull
    private TeamRole role;
}
