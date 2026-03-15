package ma.farmsense.dto.accounting;

import ma.farmsense.entity.ApprovalStatus;
import ma.farmsense.entity.TransactionType;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record TransactionFilterRequest(
        TransactionType type,
        String category,
        LocalDate from,
        LocalDate to,
        ApprovalStatus approvalStatus,
        List<UUID> tagIds
) {}
