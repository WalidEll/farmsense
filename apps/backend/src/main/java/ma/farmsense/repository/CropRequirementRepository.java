package ma.farmsense.repository;

import ma.farmsense.entity.Crop;
import ma.farmsense.entity.CropRequirement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CropRequirementRepository extends JpaRepository<CropRequirement, UUID> {
    Optional<CropRequirement> findByCrop(Crop crop);
    void deleteByCrop(Crop crop);
}
