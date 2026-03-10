package ma.farmsense.repository;

import ma.farmsense.entity.CropPlan;
import ma.farmsense.entity.FarmLocation;
import ma.farmsense.entity.Planting;
import ma.farmsense.entity.PlantingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PlantingRepository extends JpaRepository<Planting, UUID> {

    List<Planting> findByCropPlanOrderByPlannedSowDateAsc(CropPlan plan);

    List<Planting> findByCropPlanAndStatusOrderByPlannedSowDateAsc(CropPlan plan, PlantingStatus status);

    List<Planting> findByFarmLocationAndStatusNot(FarmLocation location, PlantingStatus status);

    long countByCropPlan(CropPlan plan);

    long countByFarmLocationAndStatusNotIn(FarmLocation location, List<PlantingStatus> statuses);
}
