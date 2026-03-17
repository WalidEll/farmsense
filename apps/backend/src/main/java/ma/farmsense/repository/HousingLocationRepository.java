package ma.farmsense.repository;

import ma.farmsense.entity.HousingLocation;
import ma.farmsense.entity.HousingLocationType;
import ma.farmsense.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HousingLocationRepository extends JpaRepository<HousingLocation, UUID> {

    List<HousingLocation> findByUserOrderByCreatedAtDesc(User user);

    List<HousingLocation> findByUserAndLocationTypeOrderByCreatedAtDesc(User user, HousingLocationType locationType);

    Optional<HousingLocation> findByIdAndUser(UUID id, User user);
}
