package ma.farmsense.repository;

import ma.farmsense.entity.CropPlan;
import ma.farmsense.entity.PlanSeason;
import ma.farmsense.entity.PlanStatus;
import ma.farmsense.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CropPlanRepository extends JpaRepository<CropPlan, UUID> {

    List<CropPlan> findByUserOrderByCreatedAtDesc(User user);

    List<CropPlan> findByUserAndStatusOrderByCreatedAtDesc(User user, PlanStatus status);

    List<CropPlan> findByUserAndSeasonAndYear(User user, PlanSeason season, Integer year);
}
