package ma.farmsense.controller;

import jakarta.validation.Valid;
import ma.farmsense.dto.cropplan.*;
import ma.farmsense.entity.User;
import ma.farmsense.service.CropPlanService;
import ma.farmsense.service.PlantingTaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/crop-plans")
public class CropPlanController {

    private final CropPlanService cropPlanService;
    private final PlantingTaskService plantingTaskService;

    public CropPlanController(CropPlanService cropPlanService, PlantingTaskService plantingTaskService) {
        this.cropPlanService = cropPlanService;
        this.plantingTaskService = plantingTaskService;
    }

    // ── Plans ─────────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<List<CropPlanResponse>> getAll(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(cropPlanService.findAllPlans(user));
    }

    @PostMapping
    public ResponseEntity<CropPlanResponse> create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateCropPlanRequest req) {
        return ResponseEntity.status(201).body(cropPlanService.createPlan(user, req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CropPlanResponse> getById(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        return ResponseEntity.ok(cropPlanService.findPlanById(user, id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CropPlanResponse> update(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCropPlanRequest req) {
        return ResponseEntity.ok(cropPlanService.updatePlan(user, id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        cropPlanService.deletePlan(user, id);
        return ResponseEntity.noContent().build();
    }

    // ── Plantings ─────────────────────────────────────────────────

    @GetMapping("/{planId}/plantings")
    public ResponseEntity<List<PlantingResponse>> getPlantings(
            @AuthenticationPrincipal User user,
            @PathVariable UUID planId) {
        return ResponseEntity.ok(cropPlanService.findPlantings(user, planId));
    }

    @PostMapping("/{planId}/plantings")
    public ResponseEntity<PlantingResponse> createPlanting(
            @AuthenticationPrincipal User user,
            @PathVariable UUID planId,
            @Valid @RequestBody CreatePlantingRequest req) {
        return ResponseEntity.status(201).body(cropPlanService.createPlanting(user, planId, req));
    }

    @PutMapping("/{planId}/plantings/{id}")
    public ResponseEntity<PlantingResponse> updatePlanting(
            @AuthenticationPrincipal User user,
            @PathVariable UUID planId,
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePlantingRequest req) {
        return ResponseEntity.ok(cropPlanService.updatePlanting(user, planId, id, req));
    }

    @DeleteMapping("/{planId}/plantings/{id}")
    public ResponseEntity<Void> deletePlanting(
            @AuthenticationPrincipal User user,
            @PathVariable UUID planId,
            @PathVariable UUID id) {
        cropPlanService.deletePlanting(user, planId, id);
        return ResponseEntity.noContent().build();
    }

    // ── Yield ─────────────────────────────────────────────────────

    @PutMapping("/{planId}/plantings/{id}/yield")
    public ResponseEntity<PlantingResponse> setYield(
            @AuthenticationPrincipal User user,
            @PathVariable UUID planId,
            @PathVariable UUID id,
            @Valid @RequestBody PlantingYieldRequest req) {
        return ResponseEntity.ok(cropPlanService.setYield(user, planId, id, req));
    }

    // ── Notes ─────────────────────────────────────────────────────

    @PostMapping("/{planId}/plantings/{id}/notes")
    public ResponseEntity<PlantingNoteResponse> addNote(
            @AuthenticationPrincipal User user,
            @PathVariable UUID planId,
            @PathVariable UUID id,
            @Valid @RequestBody CreatePlantingNoteRequest req) {
        return ResponseEntity.status(201).body(cropPlanService.addNote(user, planId, id, req));
    }

    @GetMapping("/{planId}/plantings/{id}/notes")
    public ResponseEntity<List<PlantingNoteResponse>> getNotes(
            @AuthenticationPrincipal User user,
            @PathVariable UUID planId,
            @PathVariable UUID id) {
        return ResponseEntity.ok(cropPlanService.getNotes(user, planId, id));
    }

    // ── Tasks ─────────────────────────────────────────────────────

    @GetMapping("/{planId}/tasks")
    public ResponseEntity<List<PlantingTaskResponse>> getTasksForPlan(
            @AuthenticationPrincipal User user,
            @PathVariable UUID planId) {
        // Verify ownership
        cropPlanService.getOwnedPlan(user, planId);
        return ResponseEntity.ok(plantingTaskService.getTasksForPlan(planId));
    }
}
