package ma.farmsense.dto.accounting;

import ma.farmsense.entity.PaymentMethod;
import ma.farmsense.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ReceiptConfirmRequest(
        TransactionType type,
        String category,
        String subcategory,
        BigDecimal amount,
        LocalDate transactionDate,
        String description,
        String vendor,
        PaymentMethod paymentMethod,
        UUID supplierId,
        UUID customerId,
        UUID flockId,
        UUID cropPlanId,
        UUID farmLocationId,
        List<UUID> tagIds
) {}
