package ma.farmsense.repository;

import ma.farmsense.entity.Crop;
import ma.farmsense.entity.CropGrowthStage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CropGrowthStageRepository extends JpaRepository<CropGrowthStage, UUID> {
    List<CropGrowthStage> findByCropOrderByStageOrder(Crop crop);
    void deleteByCrop(Crop crop);
}
