package ma.farmsense.repository;

import ma.farmsense.entity.Alert;
import ma.farmsense.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AlertRepository extends JpaRepository<Alert, UUID> {

    /** Used by AlertScheduler suppress logic */
    @Query("""
            SELECT a FROM Alert a
            WHERE a.plant.id = :plantId
              AND a.type = :type
              AND a.triggeredAt >= :since
            ORDER BY a.triggeredAt DESC
            LIMIT 1
            """)
    Optional<Alert> findMostRecentByPlantAndType(
            @Param("plantId") UUID plantId,
            @Param("type") Alert.AlertType type,
            @Param("since") Instant since);

    /** Alert history for user feed */
    List<Alert> findByUserOrderByTriggeredAtDesc(User user);

    /** Unacknowledged alerts */
    List<Alert> findByUserAndAckAtIsNullOrderByTriggeredAtDesc(User user);

    /** Count unacknowledged alerts for badge */
    long countByUserAndAckAtIsNull(User user);
}
