package ma.farmsense.dto.accounting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.farmsense.entity.ApprovalStatus;
import ma.farmsense.entity.PaymentMethod;
import ma.farmsense.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private UUID id;
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
    private ApprovalStatus approvalStatus;
    private UUID supplierId;
    private String supplierName;
    private UUID customerId;
    private String customerName;
    private UUID receiptId;
    private UUID flockId;
    private String flockName;
    private UUID cropPlanId;
    private String cropPlanName;
    private UUID farmLocationId;
    private String farmLocationName;
    private String notes;
    private List<TagResponse> tags;
    private UUID approvedBy;
    private Instant approvedAt;
    private Instant createdAt;
    private Instant updatedAt;
}
