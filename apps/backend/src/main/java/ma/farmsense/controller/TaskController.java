package ma.farmsense.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.farmsense.dto.cropplan.PlantingTaskResponse;
import ma.farmsense.dto.cropplan.UpdateTaskRequest;
import ma.farmsense.entity.User;
import ma.farmsense.service.PlantingTaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final PlantingTaskService plantingTaskService;

    @GetMapping("/today")
    public ResponseEntity<List<PlantingTaskResponse>> getTodayTasks(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(plantingTaskService.getTodayTasks(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlantingTaskResponse> updateTask(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTaskRequest req) {
        return ResponseEntity.ok(plantingTaskService.updateTask(user, id, req));
    }
}
