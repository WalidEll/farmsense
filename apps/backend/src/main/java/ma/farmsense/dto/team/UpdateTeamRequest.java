package ma.farmsense.dto.team;

import jakarta.validation.constraints.Size;

public record UpdateTeamRequest(
        @Size(max = 255) String name,
        String description
) {}
