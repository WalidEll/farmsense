package ma.farmsense.repository;

import ma.farmsense.entity.Crop;
import ma.farmsense.entity.CropNutrient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CropNutrientRepository extends JpaRepository<CropNutrient, UUID> {
    Optional<CropNutrient> findByCrop(Crop crop);
    void deleteByCrop(Crop crop);
}
