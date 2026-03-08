package ma.farmsense.repository;

import ma.farmsense.entity.AlertPreference;
import ma.farmsense.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AlertPreferenceRepository extends JpaRepository<AlertPreference, UUID> {

    Optional<AlertPreference> findByUser(User user);
}
