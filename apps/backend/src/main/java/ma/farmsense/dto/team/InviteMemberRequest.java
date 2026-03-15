package ma.farmsense.dto.team;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ma.farmsense.entity.TeamRole;

public record InviteMemberRequest(
        @NotBlank @Email String email,
        @NotNull TeamRole role
) {}
