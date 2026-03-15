package ma.farmsense.controller;

import ma.farmsense.dto.care.CareScheduleResponse;
import ma.farmsense.entity.CareLog;
import ma.farmsense.entity.User;
import ma.farmsense.service.CareService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/plants/{plantId}/care")
public class CareController {

    private final CareService careService;

    public CareController(CareService careService) {
        this.careService = careService;
    }

    @GetMapping
    public ResponseEntity<CareScheduleResponse> getSchedule(
            @AuthenticationPrincipal User user,
            @PathVariable UUID plantId) {
        return ResponseEntity.ok(careService.getSchedule(user, plantId));
    }

    @PostMapping("/{taskType}/done")
    public ResponseEntity<CareScheduleResponse> markDone(
            @AuthenticationPrincipal User user,
            @PathVariable UUID plantId,
            @PathVariable CareLog.TaskType taskType) {
        return ResponseEntity.ok(careService.markDone(user, plantId, taskType));
    }
}
