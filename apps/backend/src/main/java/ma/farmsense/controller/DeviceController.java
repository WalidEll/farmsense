package ma.farmsense.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.farmsense.dto.device.*;
import ma.farmsense.entity.User;
import ma.farmsense.service.DeviceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    /** US-030: Generate one-time setup code */
    @PostMapping("/setup-code")
    public ResponseEntity<SetupCodeResponse> generateCode(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(deviceService.generateSetupCode(user));
    }

    /**
     * US-032: ESP32 claims itself after WiFi provisioning.
     * Called by firmware — authenticated with device_id + claim_token.
     */
    @PostMapping("/claim")
    public ResponseEntity<DeviceResponse> claim(@Valid @RequestBody ClaimRequest req) {
        return ResponseEntity.ok(deviceService.claim(req));
    }

    /** US-033: List user's devices */
    @GetMapping
    public ResponseEntity<List<DeviceResponse>> list(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(deviceService.list(user));
    }

    /** Assign device to a plant */
    @PutMapping("/{id}/assign")
    public ResponseEntity<DeviceResponse> assign(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @RequestBody AssignRequest req) {
        return ResponseEntity.ok(deviceService.assign(user, id, req.getPlantId()));
    }

    /** Rename a device */
    @PutMapping("/{id}/label")
    public ResponseEntity<DeviceResponse> label(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @RequestBody LabelRequest req) {
        return ResponseEntity.ok(deviceService.label(user, id, req.getLabel()));
    }

    /** Update device configuration */
    @PutMapping("/{id}/config")
    public ResponseEntity<DeviceResponse> updateConfig(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @Valid @RequestBody DeviceConfigRequest req) {
        return ResponseEntity.ok(deviceService.updateConfig(user, id, req));
    }

    /** Delete a device */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        deviceService.delete(user, id);
        return ResponseEntity.noContent().build();
    }
}
