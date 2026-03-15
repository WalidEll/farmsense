package ma.farmsense.service;

import lombok.RequiredArgsConstructor;
import ma.farmsense.dto.cropplan.PlantingTaskResponse;
import ma.farmsense.dto.cropplan.UpdateTaskRequest;
import ma.farmsense.entity.PlantingTask;
import ma.farmsense.entity.PlantingTaskStatus;
import ma.farmsense.entity.User;
import ma.farmsense.exception.AppException;
import ma.farmsense.repository.PlantingTaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlantingTaskService {

    private final PlantingTaskRepository plantingTaskRepository;

    public List<PlantingTaskResponse> getTasksForPlan(UUID planId) {
        return plantingTaskRepository.findByPlanId(planId).stream()
                .map(PlantingTaskResponse::from)
                .toList();
    }

    public List<PlantingTaskResponse> getTodayTasks(User user) {
        return plantingTaskRepository.findByUserAndDateAndStatus(
                        user.getId(), LocalDate.now(), PlantingTaskStatus.PENDING).stream()
                .map(PlantingTaskResponse::from)
                .toList();
    }

    @Transactional
    public PlantingTaskResponse updateTask(User user, UUID taskId, UpdateTaskRequest req) {
        PlantingTask task = plantingTaskRepository.findById(taskId)
                .orElseThrow(() -> AppException.notFound("Task not found"));

        // Verify the task belongs to the user
        if (!task.getPlanting().getCropPlan().getUser().getId().equals(user.getId())) {
            throw AppException.notFound("Task not found");
        }

        task.setStatus(req.status());

        if (req.status() == PlantingTaskStatus.DONE) {
            task.setCompletedAt(Instant.now());
        }
        if (req.status() == PlantingTaskStatus.SKIPPED && req.skipReason() != null) {
            task.setSkipReason(req.skipReason());
        }

        return PlantingTaskResponse.from(plantingTaskRepository.save(task));
    }
}
