package ma.farmsense.dto.team;

import ma.farmsense.entity.ApprovalAction;

import java.time.Instant;
import java.util.UUID;

public record ApprovalLogResponse(
        UUID id,
        ApprovalAction action,
        String userName,
        String comment,
        Instant createdAt
) {}
