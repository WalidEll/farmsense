package ma.farmsense.controller;

import jakarta.validation.Valid;
import ma.farmsense.dto.plant.*;
import ma.farmsense.entity.User;
import ma.farmsense.service.PlantService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/plants")
public class PlantController {

    private final PlantService plantService;

    public PlantController(PlantService plantService) {
        this.plantService = plantService;
    }

    /** US-011: View all plants */
    @GetMapping
    public ResponseEntity<List<PlantResponse>> getAll(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(plantService.findAll(user));
    }

    /** US-010: Add a plant */
    @PostMapping
    public ResponseEntity<PlantResponse> create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreatePlantRequest req) {
        return ResponseEntity.status(201).body(plantService.create(user, req));
    }

    /** US-012: Edit a plant */
    @PutMapping("/{id}")
    public ResponseEntity<PlantResponse> update(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePlantRequest req) {
        return ResponseEntity.ok(plantService.update(user, id, req));
    }

    /** US-013: Delete a plant (soft) */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        plantService.softDelete(user, id);
        return ResponseEntity.noContent().build();
    }
}
