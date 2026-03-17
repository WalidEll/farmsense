package ma.farmsense.repository;

import jakarta.persistence.LockModeType;
import ma.farmsense.entity.Flock;
import ma.farmsense.entity.FlockPurpose;
import ma.farmsense.entity.FlockStatus;
import ma.farmsense.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FlockRepository extends JpaRepository<Flock, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT f FROM Flock f WHERE f.id = :id")
    Optional<Flock> findByIdForUpdate(UUID id);

    List<Flock> findByUserOrderByCreatedAtDesc(User user);

    List<Flock> findByUserAndStatusOrderByCreatedAtDesc(User user, FlockStatus status);

    List<Flock> findByUserAndPurposeOrderByCreatedAtDesc(User user, FlockPurpose purpose);

    long countByUserAndStatus(User user, FlockStatus status);

    long countByBreedIdAndStatus(UUID breedId, FlockStatus status);

    boolean existsByUserAndBatchCode(User user, String batchCode);

    long countByHousingLocationIdAndStatus(UUID housingLocationId, FlockStatus status);
}
