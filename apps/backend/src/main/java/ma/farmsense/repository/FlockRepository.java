package ma.farmsense.repository;

import ma.farmsense.entity.Flock;
import ma.farmsense.entity.FlockPurpose;
import ma.farmsense.entity.FlockStatus;
import ma.farmsense.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FlockRepository extends JpaRepository<Flock, UUID> {

    List<Flock> findByUserOrderByCreatedAtDesc(User user);

    List<Flock> findByUserAndStatusOrderByCreatedAtDesc(User user, FlockStatus status);

    List<Flock> findByUserAndPurposeOrderByCreatedAtDesc(User user, FlockPurpose purpose);

    long countByUserAndStatus(User user, FlockStatus status);
}
