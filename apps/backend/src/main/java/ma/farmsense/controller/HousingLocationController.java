package ma.farmsense.controller;

import jakarta.validation.Valid;
import ma.farmsense.dto.poultry.CreateHousingLocationRequest;
import ma.farmsense.dto.poultry.HousingLocationResponse;
import ma.farmsense.dto.poultry.UpdateHousingLocationRequest;
import ma.farmsense.entity.HousingLocationType;
import ma.farmsense.entity.User;
import ma.farmsense.service.HousingLocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/housing-locations")
public class HousingLocationController {

    private final HousingLocationService housingLocationService;

    public HousingLocationController(HousingLocationService housingLocationService) {
        this.housingLocationService = housingLocationService;
    }

    @GetMapping
    public ResponseEntity<List<HousingLocationResponse>> getAll(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) HousingLocationType type) {
        return ResponseEntity.ok(housingLocationService.findAll(user, type));
    }

    @PostMapping
    public ResponseEntity<HousingLocationResponse> create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateHousingLocationRequest req) {
        return ResponseEntity.status(201).body(housingLocationService.create(user, req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HousingLocationResponse> getById(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        return ResponseEntity.ok(housingLocationService.findById(user, id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HousingLocationResponse> update(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateHousingLocationRequest req) {
        return ResponseEntity.ok(housingLocationService.update(user, id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        housingLocationService.delete(user, id);
        return ResponseEntity.noContent().build();
    }
}
