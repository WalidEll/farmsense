package ma.farmsense.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.farmsense.dto.alert.AlertPreferenceRequest;
import ma.farmsense.dto.alert.AlertPreferenceResponse;
import ma.farmsense.dto.alert.AlertResponse;
import ma.farmsense.entity.User;
import ma.farmsense.service.AlertService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @GetMapping
    public ResponseEntity<List<AlertResponse>> list(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(alertService.findAll(user));
    }

    @PatchMapping("/{id}/ack")
    public ResponseEntity<AlertResponse> acknowledge(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        return ResponseEntity.ok(alertService.acknowledge(user, id));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> unreadCount(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(alertService.getUnreadCount(user));
    }

    @GetMapping("/preferences")
    public ResponseEntity<AlertPreferenceResponse> getPreferences(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(alertService.getPreferences(user));
    }

    @PutMapping("/preferences")
    public ResponseEntity<AlertPreferenceResponse> updatePreferences(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody AlertPreferenceRequest req) {
        return ResponseEntity.ok(alertService.updatePreferences(user, req));
    }
}
