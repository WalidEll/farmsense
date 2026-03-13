package ma.farmsense.repository;

import ma.farmsense.entity.LaborLog;
import ma.farmsense.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface LaborLogRepository extends JpaRepository<LaborLog, UUID> {
    List<LaborLog> findByUserOrderByWorkDateDesc(User user);
    List<LaborLog> findByUserAndWorkDateBetween(User user, LocalDate start, LocalDate end);
}
