package ma.farmsense.controller;

import jakarta.validation.Valid;
import ma.farmsense.dto.device.DeviceResponse;
import ma.farmsense.dto.sensor.*;
import ma.farmsense.entity.User;
import ma.farmsense.service.ReadingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/readings")
public class ReadingController {

    private final ReadingService readingService;

    public ReadingController(ReadingService readingService) {
        this.readingService = readingService;
    }

    /**
     * US-020: ESP32 posts sensor data.
     * Authenticated with X-Device-Key header (not JWT).
     */
    @PostMapping
    public ResponseEntity<DeviceResponse> ingest(
            @RequestHeader("X-Device-Key") String deviceKey,
            @Valid @RequestBody SensorReadingRequest req) {
        return ResponseEntity.ok(readingService.ingest(deviceKey, req));
    }

    /** US-020: Latest reading for a plant dashboard card */
    @GetMapping("/{plantId}/latest")
    public ResponseEntity<SensorReadingResponse> latest(
            @AuthenticationPrincipal User user,
            @PathVariable UUID plantId) {
        return ResponseEntity.ok(readingService.latest(user, plantId));
    }

    /**
     * US-021: History for charts.
     * ?hours=24 (default) | 168 (7d) | 720 (30d)
     */
    @GetMapping("/{plantId}/history")
    public ResponseEntity<List<SensorReadingResponse>> history(
            @AuthenticationPrincipal User user,
            @PathVariable UUID plantId,
            @RequestParam(defaultValue = "24") int hours) {
        return ResponseEntity.ok(readingService.history(user, plantId, hours));
    }

    /** US-023: Manual reading entry (no sensor needed) */
    @PostMapping("/manual")
    public ResponseEntity<SensorReadingResponse> manual(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ManualReadingRequest req) {
        return ResponseEntity.status(201).body(readingService.addManual(user, req));
    }
}
