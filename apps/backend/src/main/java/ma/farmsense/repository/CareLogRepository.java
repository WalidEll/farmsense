package ma.farmsense.repository;

import ma.farmsense.entity.CareLog;
import ma.farmsense.entity.Plant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CareLogRepository extends JpaRepository<CareLog, UUID> {

    List<CareLog> findByPlantOrderByDoneAtDesc(Plant plant);

    Optional<CareLog> findFirstByPlantAndTaskTypeOrderByDoneAtDesc(Plant plant, CareLog.TaskType taskType);

    List<CareLog> findTop10ByPlantOrderByDoneAtDesc(Plant plant);
}
