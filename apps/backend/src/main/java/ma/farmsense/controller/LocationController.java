package ma.farmsense.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.farmsense.dto.cropplan.*;
import ma.farmsense.entity.User;
import ma.farmsense.service.CropPlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
public class LocationController {

    private final CropPlanService cropPlanService;

    @GetMapping
    public ResponseEntity<List<LocationResponse>> getAll(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(cropPlanService.findAllLocations(user));
    }

    @PostMapping
    public ResponseEntity<LocationResponse> create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateLocationRequest req) {
        return ResponseEntity.status(201).body(cropPlanService.createLocation(user, req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocationResponse> update(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateLocationRequest req) {
        return ResponseEntity.ok(cropPlanService.updateLocation(user, id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        cropPlanService.deleteLocation(user, id);
        return ResponseEntity.noContent().build();
    }
}
