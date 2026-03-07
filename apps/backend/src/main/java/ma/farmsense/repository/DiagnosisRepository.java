package ma.farmsense.repository;

import ma.farmsense.entity.Diagnosis;
import ma.farmsense.entity.Plant;
import ma.farmsense.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DiagnosisRepository extends JpaRepository<Diagnosis, UUID> {

    List<Diagnosis> findByUserAndPlantOrderByCreatedAtDesc(User user, Plant plant);
}
