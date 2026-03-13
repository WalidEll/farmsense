package ma.farmsense.dto.accounting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.farmsense.entity.ApprovalStatus;
import ma.farmsense.entity.TransactionType;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionFilterRequest {
    private TransactionType type;
    private String category;
    private LocalDate from;
    private LocalDate to;
    private ApprovalStatus approvalStatus;
    private List<UUID> tagIds;
}
