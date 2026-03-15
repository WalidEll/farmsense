package ma.farmsense.dto.accounting;

import ma.farmsense.entity.ApprovalStatus;
import ma.farmsense.entity.PaymentMethod;
import ma.farmsense.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        TransactionType type,
        String category,
        String subcategory,
        BigDecimal amount,
        Double quantity,
        BigDecimal unitPrice,
        LocalDate transactionDate,
        String description,
        PaymentMethod paymentMethod,
        String referenceNumber,
        ApprovalStatus approvalStatus,
        UUID supplierId,
        String supplierName,
        UUID customerId,
        String customerName,
        UUID receiptId,
        UUID flockId,
        String flockName,
        UUID cropPlanId,
        String cropPlanName,
        UUID farmLocationId,
        String farmLocationName,
        String notes,
        List<TagResponse> tags,
        UUID approvedBy,
        Instant approvedAt,
        Instant createdAt,
        Instant updatedAt
) {}
