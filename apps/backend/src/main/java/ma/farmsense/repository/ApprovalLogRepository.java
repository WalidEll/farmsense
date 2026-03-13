package ma.farmsense.repository;

import ma.farmsense.entity.ApprovalLog;
import ma.farmsense.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ApprovalLogRepository extends JpaRepository<ApprovalLog, UUID> {
    List<ApprovalLog> findByTransactionOrderByCreatedAtDesc(Transaction transaction);
}
