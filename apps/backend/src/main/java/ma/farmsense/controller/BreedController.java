package ma.farmsense.controller;

import jakarta.validation.Valid;
import ma.farmsense.dto.poultry.BreedDetailResponse;
import ma.farmsense.dto.poultry.BreedSummaryResponse;
import ma.farmsense.dto.poultry.CreateBreedRequest;
import ma.farmsense.dto.poultry.UpdateBreedRequest;
import ma.farmsense.entity.BreedCategory;
import ma.farmsense.entity.BreedPurpose;
import ma.farmsense.entity.User;
import ma.farmsense.service.BreedService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/breeds")
public class BreedController {

    private final BreedService breedService;

    public BreedController(BreedService breedService) {
        this.breedService = breedService;
    }

    @GetMapping
    public ResponseEntity<Page<BreedSummaryResponse>> getAll(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) BreedCategory category,
            @RequestParam(required = false) BreedPurpose purpose,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(breedService.findAll(user, search, category, purpose, page, size));
    }

    @PostMapping
    public ResponseEntity<BreedDetailResponse> create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateBreedRequest req) {
        return ResponseEntity.status(201).body(breedService.create(user, req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BreedDetailResponse> getById(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        return ResponseEntity.ok(breedService.findById(user, id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BreedDetailResponse> update(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateBreedRequest req) {
        return ResponseEntity.ok(breedService.update(user, id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @RequestParam(defaultValue = "false") boolean confirm) {
        breedService.delete(user, id, confirm);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/templates")
    public ResponseEntity<BreedDetailResponse> getTemplates(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        return ResponseEntity.ok(breedService.getTemplates(user, id));
    }
}
