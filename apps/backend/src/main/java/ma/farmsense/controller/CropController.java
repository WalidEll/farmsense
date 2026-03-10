package ma.farmsense.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.farmsense.dto.crop.*;
import ma.farmsense.entity.CropCategory;
import ma.farmsense.service.CropService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/crops")
@RequiredArgsConstructor
public class CropController {

    private final CropService cropService;

    @GetMapping
    public ResponseEntity<List<CropResponse>> getAll(
            @RequestParam(required = false) CropCategory category,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(cropService.findAll(category, search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CropDetailResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(cropService.findById(id));
    }

    @PostMapping
    public ResponseEntity<CropResponse> create(@Valid @RequestBody CreateCropRequest req) {
        return ResponseEntity.status(201).body(cropService.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CropResponse> update(@PathVariable UUID id, @Valid @RequestBody UpdateCropRequest req) {
        return ResponseEntity.ok(cropService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        cropService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ── Sub-resources ────────────────────────────────────────────

    @PutMapping("/{id}/requirements")
    public ResponseEntity<CropRequirementResponse> setRequirements(
            @PathVariable UUID id, @Valid @RequestBody CropRequirementRequest req) {
        return ResponseEntity.ok(cropService.setRequirements(id, req));
    }

    @PostMapping("/{id}/stages")
    public ResponseEntity<CropGrowthStageResponse> addStage(
            @PathVariable UUID id, @Valid @RequestBody CropGrowthStageRequest req) {
        return ResponseEntity.status(201).body(cropService.addStage(id, req));
    }

    @PutMapping("/{id}/stages/{stageId}")
    public ResponseEntity<CropGrowthStageResponse> updateStage(
            @PathVariable UUID id, @PathVariable UUID stageId,
            @Valid @RequestBody CropGrowthStageRequest req) {
        return ResponseEntity.ok(cropService.updateStage(id, stageId, req));
    }

    @DeleteMapping("/{id}/stages/{stageId}")
    public ResponseEntity<Void> deleteStage(@PathVariable UUID id, @PathVariable UUID stageId) {
        cropService.deleteStage(id, stageId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/nutrients")
    public ResponseEntity<CropNutrientResponse> setNutrients(
            @PathVariable UUID id, @Valid @RequestBody CropNutrientRequest req) {
        return ResponseEntity.ok(cropService.setNutrients(id, req));
    }

    @PostMapping("/{id}/issues")
    public ResponseEntity<CropIssueResponse> addIssue(
            @PathVariable UUID id, @Valid @RequestBody CropIssueRequest req) {
        return ResponseEntity.status(201).body(cropService.addIssue(id, req));
    }

    @PutMapping("/{id}/issues/{issueId}")
    public ResponseEntity<CropIssueResponse> updateIssue(
            @PathVariable UUID id, @PathVariable UUID issueId,
            @Valid @RequestBody CropIssueRequest req) {
        return ResponseEntity.ok(cropService.updateIssue(id, issueId, req));
    }

    @DeleteMapping("/{id}/issues/{issueId}")
    public ResponseEntity<Void> deleteIssue(@PathVariable UUID id, @PathVariable UUID issueId) {
        cropService.deleteIssue(id, issueId);
        return ResponseEntity.noContent().build();
    }
}
