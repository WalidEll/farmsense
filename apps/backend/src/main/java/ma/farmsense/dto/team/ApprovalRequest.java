package ma.farmsense.dto.team;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.farmsense.entity.ApprovalAction;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ApprovalRequest {
    @NotNull
    private ApprovalAction action;
    private String comment;
}
