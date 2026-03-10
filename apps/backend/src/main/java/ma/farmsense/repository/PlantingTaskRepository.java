package ma.farmsense.repository;

import ma.farmsense.entity.Planting;
import ma.farmsense.entity.PlantingTask;
import ma.farmsense.entity.PlantingTaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface PlantingTaskRepository extends JpaRepository<PlantingTask, UUID> {

    List<PlantingTask> findByPlantingOrderByDueDateAsc(Planting planting);

    void deleteByPlanting(Planting planting);

    @Query("SELECT t FROM PlantingTask t WHERE t.planting.cropPlan.id = :planId ORDER BY t.dueDate ASC")
    List<PlantingTask> findByPlanId(UUID planId);

    @Query("SELECT t FROM PlantingTask t WHERE t.planting.cropPlan.user.id = :userId AND t.dueDate = :date AND t.status = :status ORDER BY t.dueDate ASC")
    List<PlantingTask> findByUserAndDateAndStatus(UUID userId, LocalDate date, PlantingTaskStatus status);
}
