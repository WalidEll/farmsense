package ma.farmsense.repository;

import ma.farmsense.entity.FarmLocation;
import ma.farmsense.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FarmLocationRepository extends JpaRepository<FarmLocation, UUID> {

    List<FarmLocation> findByUserOrderByNameAsc(User user);
}
