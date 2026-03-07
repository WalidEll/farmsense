package ma.farmsense.repository;

import ma.farmsense.entity.Plant;
import ma.farmsense.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlantRepository extends JpaRepository<Plant, UUID> {

    List<Plant> findByUserAndDeletedAtIsNull(User user);

    Optional<Plant> findByIdAndUserAndDeletedAtIsNull(UUID id, User user);

    /** All plants with active sensor data (not soft-deleted) — used by AlertScheduler */
    @Query("SELECT p FROM Plant p WHERE p.deletedAt IS NULL")
    List<Plant> findAllActive();
}
