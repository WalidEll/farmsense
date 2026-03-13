package ma.farmsense.dto.team;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.farmsense.entity.ApprovalAction;

import java.time.Instant;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ApprovalLogResponse {
    private UUID id;
    private ApprovalAction action;
    private String userName;
    private String comment;
    private Instant createdAt;
}
