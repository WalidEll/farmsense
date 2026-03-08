package ma.farmsense.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.farmsense.dto.alert.AlertPreferenceRequest;
import ma.farmsense.dto.alert.AlertPreferenceResponse;
import ma.farmsense.dto.alert.AlertResponse;
import ma.farmsense.entity.Alert;
import ma.farmsense.entity.AlertPreference;
import ma.farmsense.entity.User;
import ma.farmsense.exception.AppException;
import ma.farmsense.repository.AlertPreferenceRepository;
import ma.farmsense.repository.AlertRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertRepository alertRepository;
    private final AlertPreferenceRepository alertPreferenceRepository;

    /** US-040: Alert history feed */
    @GetMapping
    public ResponseEntity<List<AlertResponse>> list(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(
                alertRepository.findByUserOrderByTriggeredAtDesc(user)
                        .stream().map(AlertResponse::from).toList()
        );
    }

    /** US-041: Acknowledge an alert */
    @PatchMapping("/{id}/ack")
    public ResponseEntity<AlertResponse> acknowledge(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        Alert alert = alertRepository.findById(id)
                .filter(a -> a.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> AppException.notFound("Alert not found"));

        alert.setAckAt(Instant.now());
        return ResponseEntity.ok(AlertResponse.from(alertRepository.save(alert)));
    }

    /** Unacknowledged count — used by frontend badge */
    @GetMapping("/unread-count")
    public ResponseEntity<Long> unreadCount(@AuthenticationPrincipal User user) {
        long count = alertRepository.findByUserAndAckAtIsNullOrderByTriggeredAtDesc(user).size();
        return ResponseEntity.ok(count);
    }

    /** US-044: Get alert preferences (creates defaults if none exist) */
    @GetMapping("/preferences")
    public ResponseEntity<AlertPreferenceResponse> getPreferences(@AuthenticationPrincipal User user) {
        AlertPreference pref = alertPreferenceRepository.findByUser(user)
                .orElseGet(() -> alertPreferenceRepository.save(
                        AlertPreference.builder().user(user).build()
                ));
        return ResponseEntity.ok(AlertPreferenceResponse.from(pref));
    }

    /** US-044: Update alert preferences */
    @PutMapping("/preferences")
    public ResponseEntity<AlertPreferenceResponse> updatePreferences(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody AlertPreferenceRequest req) {
        AlertPreference pref = alertPreferenceRepository.findByUser(user)
                .orElseGet(() -> AlertPreference.builder().user(user).build());

        pref.setSoilDryEnabled(req.isSoilDryEnabled());
        pref.setSoilWetEnabled(req.isSoilWetEnabled());
        pref.setTempHighEnabled(req.isTempHighEnabled());
        pref.setTempLowEnabled(req.isTempLowEnabled());
        pref.setLightLowEnabled(req.isLightLowEnabled());
        pref.setDeviceOfflineEnabled(req.isDeviceOfflineEnabled());
        pref.setQuietHoursStart(req.getQuietHoursStart());
        pref.setQuietHoursEnd(req.getQuietHoursEnd());
        pref.setChannelWhatsapp(req.isChannelWhatsapp());
        pref.setChannelPush(req.isChannelPush());

        return ResponseEntity.ok(AlertPreferenceResponse.from(alertPreferenceRepository.save(pref)));
    }
}
