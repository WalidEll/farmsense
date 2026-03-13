package ma.farmsense.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.farmsense.dto.poultry.CreateSupplierRequest;
import ma.farmsense.dto.poultry.SupplierResponse;
import ma.farmsense.dto.poultry.UpdateSupplierRequest;
import ma.farmsense.entity.User;
import ma.farmsense.service.SupplierService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping
    public ResponseEntity<List<SupplierResponse>> getAll(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(supplierService.findAll(user, search));
    }

    @PostMapping
    public ResponseEntity<SupplierResponse> create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateSupplierRequest req) {
        return ResponseEntity.status(201).body(supplierService.create(user, req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierResponse> getById(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        return ResponseEntity.ok(supplierService.findById(user, id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupplierResponse> update(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateSupplierRequest req) {
        return ResponseEntity.ok(supplierService.update(user, id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        supplierService.delete(user, id);
        return ResponseEntity.noContent().build();
    }
}
