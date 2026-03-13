package ma.farmsense.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.farmsense.dto.poultry.CreateFlockRequest;
import ma.farmsense.dto.poultry.FlockResponse;
import ma.farmsense.dto.poultry.UpdateFlockRequest;
import ma.farmsense.entity.FlockPurpose;
import ma.farmsense.entity.FlockStatus;
import ma.farmsense.entity.User;
import ma.farmsense.service.FlockService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/flocks")
@RequiredArgsConstructor
public class FlockController {

    private final FlockService flockService;

    @GetMapping
    public ResponseEntity<List<FlockResponse>> getAll(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) FlockStatus status,
            @RequestParam(required = false) FlockPurpose purpose) {
        return ResponseEntity.ok(flockService.findAll(user, status, purpose));
    }

    @PostMapping
    public ResponseEntity<FlockResponse> create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateFlockRequest req) {
        return ResponseEntity.status(201).body(flockService.create(user, req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlockResponse> getById(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        return ResponseEntity.ok(flockService.findById(user, id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FlockResponse> update(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateFlockRequest req) {
        return ResponseEntity.ok(flockService.update(user, id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        flockService.delete(user, id);
        return ResponseEntity.noContent().build();
    }
}
