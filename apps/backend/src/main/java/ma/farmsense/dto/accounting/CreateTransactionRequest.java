package ma.farmsense.dto.accounting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.farmsense.entity.PaymentMethod;
import ma.farmsense.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransactionRequest {
    private TransactionType type;
    private String category;
    private String subcategory;
    private BigDecimal amount;
    private Double quantity;
    private BigDecimal unitPrice;
    private LocalDate transactionDate;
    private String description;
    private PaymentMethod paymentMethod;
    private String referenceNumber;
    private UUID supplierId;
    private UUID customerId;
    private UUID receiptId;
    private UUID flockId;
    private UUID cropPlanId;
    private UUID farmLocationId;
    private List<UUID> tagIds;
    private String notes;
}
