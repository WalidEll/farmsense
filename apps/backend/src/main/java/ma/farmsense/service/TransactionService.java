package ma.farmsense.service;

import lombok.RequiredArgsConstructor;
import ma.farmsense.dto.accounting.*;
import ma.farmsense.dto.team.ApprovalLogResponse;
import ma.farmsense.dto.team.ApprovalRequest;
import ma.farmsense.entity.*;
import ma.farmsense.exception.AppException;
import ma.farmsense.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final SupplierRepository supplierRepository;
    private final CustomerRepository customerRepository;
    private final ReceiptRepository receiptRepository;
    private final FlockRepository flockRepository;
    private final CropPlanRepository cropPlanRepository;
    private final FarmLocationRepository farmLocationRepository;
    private final TagRepository tagRepository;
    private final ApprovalLogRepository approvalLogRepository;

    public List<TransactionResponse> findAll(User user, TransactionFilterRequest filters) {
        List<Transaction> transactions;
        if (filters != null) {
            transactions = transactionRepository.findWithFilters(
                    user,
                    filters.getType(),
                    filters.getCategory(),
                    filters.getFrom(),
                    filters.getTo(),
                    filters.getApprovalStatus()
            );
            
            if (filters.getTagIds() != null && !filters.getTagIds().isEmpty()) {
                Set<UUID> tagIds = new HashSet<>(filters.getTagIds());
                transactions = transactions.stream()
                        .filter(t -> t.getTags().stream().anyMatch(tag -> tagIds.contains(tag.getId())))
                        .collect(Collectors.toList());
            }
        } else {
            transactions = transactionRepository.findByUserOrderByTransactionDateDesc(user);
        }
        return transactions.stream().map(this::mapToResponse).toList();
    }

    public TransactionResponse findById(User user, UUID id) {
        Transaction transaction = getOwned(user, id);
        return mapToResponse(transaction);
    }

    @Transactional
    public TransactionResponse create(User user, CreateTransactionRequest req) {
        Transaction transaction = Transaction.builder()
                .user(user)
                .type(req.getType())
                .category(req.getCategory())
                .subcategory(req.getSubcategory())
                .amount(req.getAmount())
                .quantity(req.getQuantity())
                .unitPrice(req.getUnitPrice())
                .transactionDate(req.getTransactionDate())
                .description(req.getDescription())
                .paymentMethod(req.getPaymentMethod())
                .referenceNumber(req.getReferenceNumber())
                .notes(req.getNotes())
                .approvalStatus(ApprovalStatus.APPROVED) // Default to approved for personal transactions
                .build();

        updateRelations(transaction, req.getSupplierId(), req.getCustomerId(), req.getReceiptId(), 
                        req.getFlockId(), req.getCropPlanId(), req.getFarmLocationId(), req.getTagIds());

        return mapToResponse(transactionRepository.save(transaction));
    }

    @Transactional
    public TransactionResponse update(User user, UUID id, UpdateTransactionRequest req) {
        Transaction transaction = getOwned(user, id);

        if (req.getType() != null) transaction.setType(req.getType());
        if (req.getCategory() != null) transaction.setCategory(req.getCategory());
        if (req.getSubcategory() != null) transaction.setSubcategory(req.getSubcategory());
        if (req.getAmount() != null) transaction.setAmount(req.getAmount());
        if (req.getQuantity() != null) transaction.setQuantity(req.getQuantity());
        if (req.getUnitPrice() != null) transaction.setUnitPrice(req.getUnitPrice());
        if (req.getTransactionDate() != null) transaction.setTransactionDate(req.getTransactionDate());
        if (req.getDescription() != null) transaction.setDescription(req.getDescription());
        if (req.getPaymentMethod() != null) transaction.setPaymentMethod(req.getPaymentMethod());
        if (req.getReferenceNumber() != null) transaction.setReferenceNumber(req.getReferenceNumber());
        if (req.getNotes() != null) transaction.setNotes(req.getNotes());

        updateRelations(transaction, req.getSupplierId(), req.getCustomerId(), req.getReceiptId(), 
                        req.getFlockId(), req.getCropPlanId(), req.getFarmLocationId(), req.getTagIds());

        return mapToResponse(transactionRepository.save(transaction));
    }

    @Transactional
    public void delete(User user, UUID id) {
        Transaction transaction = getOwned(user, id);
        transactionRepository.delete(transaction);
    }

    public DashboardResponse getDashboard(User user, String fromStr, String toStr) {
        LocalDate from = fromStr != null ? LocalDate.parse(fromStr) : LocalDate.now().withDayOfMonth(1);
        LocalDate to = toStr != null ? LocalDate.parse(toStr) : LocalDate.now();

        BigDecimal totalIncome = transactionRepository.sumAmountByUserAndTypeAndDateRange(user, TransactionType.INCOME, from, to);
        BigDecimal totalExpenses = transactionRepository.sumAmountByUserAndTypeAndDateRange(user, TransactionType.EXPENSE, from, to);
        BigDecimal netProfit = totalIncome.subtract(totalExpenses);

        List<CategorySummary> expensesByCategory = transactionRepository.findCategorySummary(user, TransactionType.EXPENSE, from, to);
        List<CategorySummary> incomesByCategory = transactionRepository.findCategorySummary(user, TransactionType.INCOME, from, to);

        long pendingApprovals = transactionRepository.countByUserAndApprovalStatus(user, ApprovalStatus.PENDING);

        // Recent 10 transactions
        List<TransactionResponse> recent = transactionRepository.findByUserOrderByTransactionDateDesc(user).stream()
                .limit(10)
                .map(this::mapToResponse)
                .toList();

        return DashboardResponse.builder()
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netProfit(netProfit)
                .expensesByCategory(expensesByCategory)
                .incomesByCategory(incomesByCategory)
                .recentTransactions(recent)
                .pendingApprovals(pendingApprovals)
                .build();
    }

    public List<String> getCategories(User user) {
        return transactionRepository.findDistinctCategoriesByUser(user);
    }

    @Transactional
    public TransactionResponse approve(User user, UUID id, ApprovalRequest req) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Transaction not found"));
        
        // Ownership/Team check: for now just basic
        // In a real team scenario, we'd check if user is admin of the team
        
        ApprovalStatus status = switch (req.getAction()) {
            case APPROVED -> ApprovalStatus.APPROVED;
            case REJECTED -> ApprovalStatus.REJECTED;
            default -> ApprovalStatus.PENDING;
        };
        
        transaction.setApprovalStatus(status);
        if (status == ApprovalStatus.APPROVED) {
            transaction.setApprovedBy(user);
            transaction.setApprovedAt(Instant.now());
        }

        ApprovalLog log = ApprovalLog.builder()
                .transaction(transaction)
                .user(user)
                .action(req.getAction())
                .comment(req.getComment())
                .build();
        approvalLogRepository.save(log);

        return mapToResponse(transactionRepository.save(transaction));
    }

    public List<ApprovalLogResponse> getApprovalLogs(UUID id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Transaction not found"));
        return approvalLogRepository.findByTransactionOrderByCreatedAtDesc(transaction).stream()
                .map(log -> ApprovalLogResponse.builder()
                        .id(log.getId())
                        .action(log.getAction())
                        .userName(log.getUser().getFullName())
                        .comment(log.getComment())
                        .createdAt(log.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    // ── Helpers ───────────────────────────────────────────────────

    private Transaction getOwned(User user, UUID id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Transaction not found"));
        if (!transaction.getUser().getId().equals(user.getId())) {
            // Check team? For now simple ownership
            throw AppException.notFound("Transaction not found");
        }
        return transaction;
    }

    private void updateRelations(Transaction transaction, UUID supplierId, UUID customerId, UUID receiptId,
                                UUID flockId, UUID cropPlanId, UUID farmLocationId, List<UUID> tagIds) {
        
        if (supplierId != null) {
            transaction.setSupplier(supplierRepository.findById(supplierId).orElse(null));
        } else {
            transaction.setSupplier(null);
        }

        if (customerId != null) {
            transaction.setCustomer(customerRepository.findById(customerId).orElse(null));
        } else {
            transaction.setCustomer(null);
        }

        if (receiptId != null) {
            transaction.setReceipt(receiptRepository.findById(receiptId).orElse(null));
        } else {
            transaction.setReceipt(null);
        }

        if (flockId != null) {
            transaction.setFlock(flockRepository.findById(flockId).orElse(null));
        } else {
            transaction.setFlock(null);
        }

        if (cropPlanId != null) {
            transaction.setCropPlan(cropPlanRepository.findById(cropPlanId).orElse(null));
        } else {
            transaction.setCropPlan(null);
        }

        if (farmLocationId != null) {
            transaction.setFarmLocation(farmLocationRepository.findById(farmLocationId).orElse(null));
        } else {
            transaction.setFarmLocation(null);
        }

        if (tagIds != null) {
            Set<Tag> tags = new HashSet<>(tagRepository.findAllById(tagIds));
            transaction.setTags(tags);
        }
    }

    private TransactionResponse mapToResponse(Transaction t) {
        return TransactionResponse.builder()
                .id(t.getId())
                .type(t.getType())
                .category(t.getCategory())
                .subcategory(t.getSubcategory())
                .amount(t.getAmount())
                .quantity(t.getQuantity())
                .unitPrice(t.getUnitPrice())
                .transactionDate(t.getTransactionDate())
                .description(t.getDescription())
                .paymentMethod(t.getPaymentMethod())
                .referenceNumber(t.getReferenceNumber())
                .approvalStatus(t.getApprovalStatus())
                .supplierId(t.getSupplier() != null ? t.getSupplier().getId() : null)
                .supplierName(t.getSupplier() != null ? t.getSupplier().getName() : null)
                .customerId(t.getCustomer() != null ? t.getCustomer().getId() : null)
                .customerName(t.getCustomer() != null ? t.getCustomer().getName() : null)
                .receiptId(t.getReceipt() != null ? t.getReceipt().getId() : null)
                .flockId(t.getFlock() != null ? t.getFlock().getId() : null)
                .flockName(t.getFlock() != null ? t.getFlock().getName() : null)
                .cropPlanId(t.getCropPlan() != null ? t.getCropPlan().getId() : null)
                .cropPlanName(t.getCropPlan() != null ? t.getCropPlan().getName() : null)
                .farmLocationId(t.getFarmLocation() != null ? t.getFarmLocation().getId() : null)
                .farmLocationName(t.getFarmLocation() != null ? t.getFarmLocation().getName() : null)
                .notes(t.getNotes())
                .tags(t.getTags().stream()
                        .map(tag -> TagResponse.builder()
                                .id(tag.getId())
                                .name(tag.getName())
                                .color(tag.getColor())
                                .build())
                        .toList())
                .approvedBy(t.getApprovedBy() != null ? t.getApprovedBy().getId() : null)
                .approvedAt(t.getApprovedAt())
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .build();
    }
}
