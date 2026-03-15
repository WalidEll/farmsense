package ma.farmsense.service;

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

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository,
                               SupplierRepository supplierRepository, CustomerRepository customerRepository,
                               ReceiptRepository receiptRepository, FlockRepository flockRepository,
                               CropPlanRepository cropPlanRepository, FarmLocationRepository farmLocationRepository,
                               TagRepository tagRepository, ApprovalLogRepository approvalLogRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.supplierRepository = supplierRepository;
        this.customerRepository = customerRepository;
        this.receiptRepository = receiptRepository;
        this.flockRepository = flockRepository;
        this.cropPlanRepository = cropPlanRepository;
        this.farmLocationRepository = farmLocationRepository;
        this.tagRepository = tagRepository;
        this.approvalLogRepository = approvalLogRepository;
    }

    public List<TransactionResponse> findAll(User user, TransactionFilterRequest filters) {
        List<Transaction> transactions;
        if (filters != null) {
            transactions = transactionRepository.findWithFilters(
                    user,
                    filters.type(),
                    filters.category(),
                    filters.from(),
                    filters.to(),
                    filters.approvalStatus()
            );

            if (filters.tagIds() != null && !filters.tagIds().isEmpty()) {
                Set<UUID> tagIds = new HashSet<>(filters.tagIds());
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
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setType(req.type());
        transaction.setCategory(req.category());
        transaction.setSubcategory(req.subcategory());
        transaction.setAmount(req.amount());
        transaction.setQuantity(req.quantity());
        transaction.setUnitPrice(req.unitPrice());
        transaction.setTransactionDate(req.transactionDate());
        transaction.setDescription(req.description());
        transaction.setPaymentMethod(req.paymentMethod());
        transaction.setReferenceNumber(req.referenceNumber());
        transaction.setNotes(req.notes());
        transaction.setApprovalStatus(ApprovalStatus.APPROVED); // Default to approved for personal transactions

        updateRelations(transaction, req.supplierId(), req.customerId(), req.receiptId(),
                        req.flockId(), req.cropPlanId(), req.farmLocationId(), req.tagIds());

        return mapToResponse(transactionRepository.save(transaction));
    }

    @Transactional
    public TransactionResponse update(User user, UUID id, UpdateTransactionRequest req) {
        Transaction transaction = getOwned(user, id);

        if (req.type() != null) transaction.setType(req.type());
        if (req.category() != null) transaction.setCategory(req.category());
        if (req.subcategory() != null) transaction.setSubcategory(req.subcategory());
        if (req.amount() != null) transaction.setAmount(req.amount());
        if (req.quantity() != null) transaction.setQuantity(req.quantity());
        if (req.unitPrice() != null) transaction.setUnitPrice(req.unitPrice());
        if (req.transactionDate() != null) transaction.setTransactionDate(req.transactionDate());
        if (req.description() != null) transaction.setDescription(req.description());
        if (req.paymentMethod() != null) transaction.setPaymentMethod(req.paymentMethod());
        if (req.referenceNumber() != null) transaction.setReferenceNumber(req.referenceNumber());
        if (req.notes() != null) transaction.setNotes(req.notes());

        updateRelations(transaction, req.supplierId(), req.customerId(), req.receiptId(),
                        req.flockId(), req.cropPlanId(), req.farmLocationId(), req.tagIds());

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

        return new DashboardResponse(
                totalIncome,
                totalExpenses,
                netProfit,
                expensesByCategory,
                incomesByCategory,
                recent,
                pendingApprovals
        );
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
        
        ApprovalStatus status = switch (req.action()) {
            case APPROVED -> ApprovalStatus.APPROVED;
            case REJECTED -> ApprovalStatus.REJECTED;
            default -> ApprovalStatus.PENDING;
        };

        transaction.setApprovalStatus(status);
        if (status == ApprovalStatus.APPROVED) {
            transaction.setApprovedBy(user);
            transaction.setApprovedAt(Instant.now());
        }

        ApprovalLog log = new ApprovalLog();
        log.setTransaction(transaction);
        log.setUser(user);
        log.setAction(req.action());
        log.setComment(req.comment());
        approvalLogRepository.save(log);

        return mapToResponse(transactionRepository.save(transaction));
    }

    public List<ApprovalLogResponse> getApprovalLogs(UUID id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Transaction not found"));
        return approvalLogRepository.findByTransactionOrderByCreatedAtDesc(transaction).stream()
                .map(log -> new ApprovalLogResponse(
                        log.getId(),
                        log.getAction(),
                        log.getUser().getFullName(),
                        log.getComment(),
                        log.getCreatedAt()
                ))
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
        return new TransactionResponse(
                t.getId(),
                t.getType(),
                t.getCategory(),
                t.getSubcategory(),
                t.getAmount(),
                t.getQuantity(),
                t.getUnitPrice(),
                t.getTransactionDate(),
                t.getDescription(),
                t.getPaymentMethod(),
                t.getReferenceNumber(),
                t.getApprovalStatus(),
                t.getSupplier() != null ? t.getSupplier().getId() : null,
                t.getSupplier() != null ? t.getSupplier().getName() : null,
                t.getCustomer() != null ? t.getCustomer().getId() : null,
                t.getCustomer() != null ? t.getCustomer().getName() : null,
                t.getReceipt() != null ? t.getReceipt().getId() : null,
                t.getFlock() != null ? t.getFlock().getId() : null,
                t.getFlock() != null ? t.getFlock().getName() : null,
                t.getCropPlan() != null ? t.getCropPlan().getId() : null,
                t.getCropPlan() != null ? t.getCropPlan().getName() : null,
                t.getFarmLocation() != null ? t.getFarmLocation().getId() : null,
                t.getFarmLocation() != null ? t.getFarmLocation().getName() : null,
                t.getNotes(),
                t.getTags().stream()
                        .map(tag -> new TagResponse(tag.getId(), tag.getName(), tag.getColor()))
                        .toList(),
                t.getApprovedBy() != null ? t.getApprovedBy().getId() : null,
                t.getApprovedAt(),
                t.getCreatedAt(),
                t.getUpdatedAt()
        );
    }
}
