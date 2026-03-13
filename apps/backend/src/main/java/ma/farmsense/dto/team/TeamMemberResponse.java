package ma.farmsense.dto.team;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.farmsense.entity.MemberStatus;
import ma.farmsense.entity.TeamRole;

import java.time.Instant;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TeamMemberResponse {
    private UUID id;
    private UUID userId;
    private String userName;
    private String userEmail;
    private TeamRole role;
    private MemberStatus status;
    private String invitedByName;
    private Instant invitedAt;
    private Instant joinedAt;
}
