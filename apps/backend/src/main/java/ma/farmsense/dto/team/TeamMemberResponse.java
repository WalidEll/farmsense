package ma.farmsense.dto.team;

import ma.farmsense.entity.MemberStatus;
import ma.farmsense.entity.TeamRole;

import java.time.Instant;
import java.util.UUID;

public record TeamMemberResponse(
        UUID id,
        UUID userId,
        String userName,
        String userEmail,
        TeamRole role,
        MemberStatus status,
        String invitedByName,
        Instant invitedAt,
        Instant joinedAt
) {}
