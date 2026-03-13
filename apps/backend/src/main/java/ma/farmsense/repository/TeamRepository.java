package ma.farmsense.repository;

import ma.farmsense.entity.Team;
import ma.farmsense.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TeamRepository extends JpaRepository<Team, UUID> {
    List<Team> findByOwner(User owner);
    List<Team> findByOwnerOrderByCreatedAtDesc(User owner);
    Optional<Team> findByIdAndOwner(UUID id, User owner);
}
