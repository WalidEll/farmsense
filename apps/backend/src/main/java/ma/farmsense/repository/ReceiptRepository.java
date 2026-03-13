package ma.farmsense.repository;

import ma.farmsense.entity.OcrStatus;
import ma.farmsense.entity.Receipt;
import ma.farmsense.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReceiptRepository extends JpaRepository<Receipt, UUID> {
    List<Receipt> findByUserOrderByCreatedAtDesc(User user);
    List<Receipt> findByUserAndOcrStatus(User user, OcrStatus status);
}
