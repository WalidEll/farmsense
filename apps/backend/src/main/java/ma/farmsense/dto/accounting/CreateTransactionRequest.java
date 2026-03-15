package ma.farmsense.dto.accounting;

import ma.farmsense.entity.PaymentMethod;
import ma.farmsense.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CreateTransactionRequest(
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
        UUID supplierId,
        UUID customerId,
        UUID receiptId,
        UUID flockId,
        UUID cropPlanId,
        UUID farmLocationId,
        List<UUID> tagIds,
        String notes
) {}
