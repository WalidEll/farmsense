package ma.farmsense.repository;

import ma.farmsense.entity.Plant;
import ma.farmsense.entity.SensorReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SensorReadingRepository extends JpaRepository<SensorReading, Long> {

    /** Latest reading for dashboard card */
    @Query("SELECT r FROM SensorReading r WHERE r.plant.id = :plantId ORDER BY r.recordedAt DESC LIMIT 1")
    Optional<SensorReading> findLatestByPlantId(@Param("plantId") UUID plantId);

    /** Last 2 readings — used by AlertScheduler to detect sustained threshold breaches */
    @Query("SELECT r FROM SensorReading r WHERE r.plant.id = :plantId ORDER BY r.recordedAt DESC LIMIT 2")
    List<SensorReading> findLatest2ByPlant(@Param("plantId") UUID plantId);

    /** History for charts */
    @Query("SELECT r FROM SensorReading r WHERE r.plant = :plant AND r.recordedAt >= :since ORDER BY r.recordedAt ASC")
    List<SensorReading> findByPlantAndRecordedAtAfter(@Param("plant") Plant plant, @Param("since") Instant since);
}
