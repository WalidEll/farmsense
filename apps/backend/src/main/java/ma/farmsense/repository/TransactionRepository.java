package ma.farmsense.repository;

import ma.farmsense.dto.accounting.CategorySummary;
import ma.farmsense.entity.ApprovalStatus;
import ma.farmsense.entity.Transaction;
import ma.farmsense.entity.TransactionType;
import ma.farmsense.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findByUserOrderByTransactionDateDesc(User user);

    @Query("SELECT t FROM Transaction t WHERE t.user = :user " +
           "AND (:type IS NULL OR t.type = :type) " +
           "AND (:category IS NULL OR t.category = :category) " +
           "AND (:from IS NULL OR t.transactionDate >= :from) " +
           "AND (:to IS NULL OR t.transactionDate <= :to) " +
           "AND (:status IS NULL OR t.approvalStatus = :status) " +
           "ORDER BY t.transactionDate DESC")
    List<Transaction> findWithFilters(
            @Param("user") User user,
            @Param("type") TransactionType type,
            @Param("category") String category,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("status") ApprovalStatus status
    );

    @Query("SELECT DISTINCT t.category FROM Transaction t WHERE t.user = :user ORDER BY t.category")
    List<String> findDistinctCategoriesByUser(@Param("user") User user);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user = :user AND t.type = :type AND t.transactionDate BETWEEN :from AND :to AND t.approvalStatus = 'APPROVED'")
    java.math.BigDecimal sumAmountByUserAndTypeAndDateRange(
            @Param("user") User user,
            @Param("type") TransactionType type,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    @Query("SELECT new ma.farmsense.dto.accounting.CategorySummary(t.category, SUM(t.amount)) " +
           "FROM Transaction t WHERE t.user = :user AND t.type = :type AND t.transactionDate BETWEEN :from AND :to AND t.approvalStatus = 'APPROVED' " +
           "GROUP BY t.category")
    List<CategorySummary> findCategorySummary(
            @Param("user") User user,
            @Param("type") TransactionType type,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    long countByUserAndApprovalStatus(User user, ApprovalStatus status);
}
