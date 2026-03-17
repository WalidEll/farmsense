package ma.farmsense.controller;

import jakarta.validation.Valid;
import ma.farmsense.dto.poultry.CreateMortalityEventRequest;
import ma.farmsense.dto.poultry.MortalityEventCreateResponse;
import ma.farmsense.dto.poultry.MortalityEventResponse;
import ma.farmsense.entity.User;
import ma.farmsense.service.MortalityService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/flocks/{flockId}/mortality-events")
public class MortalityController {

    private final MortalityService mortalityService;

    public MortalityController(MortalityService mortalityService) {
        this.mortalityService = mortalityService;
    }

    @GetMapping
    public ResponseEntity<List<MortalityEventResponse>> getAll(
            @AuthenticationPrincipal User user,
            @PathVariable UUID flockId) {
        return ResponseEntity.ok(mortalityService.findByFlock(user, flockId));
    }

    @PostMapping
    public ResponseEntity<MortalityEventCreateResponse> create(
            @AuthenticationPrincipal User user,
            @PathVariable UUID flockId,
            @Valid @RequestBody CreateMortalityEventRequest req) {
        return ResponseEntity.status(201).body(mortalityService.create(user, flockId, req));
    }
}
