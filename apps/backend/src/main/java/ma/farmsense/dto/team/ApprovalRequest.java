package ma.farmsense.dto.team;

import jakarta.validation.constraints.NotNull;
import ma.farmsense.entity.ApprovalAction;

public record ApprovalRequest(
        @NotNull ApprovalAction action,
        String comment
) {}
