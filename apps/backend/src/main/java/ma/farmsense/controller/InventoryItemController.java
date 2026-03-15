package ma.farmsense.controller;

import jakarta.validation.Valid;
import ma.farmsense.dto.inventory.*;
import ma.farmsense.service.InventoryItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventory-items")
public class InventoryItemController {

    private final InventoryItemService inventoryItemService;

    public InventoryItemController(InventoryItemService inventoryItemService) {
        this.inventoryItemService = inventoryItemService;
    }

    @GetMapping
    public ResponseEntity<List<InventoryItemResponse>> getAll(
            @RequestParam(required = false) String categoryType,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(inventoryItemService.findAll(categoryType, search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryItemResponse> getById(@PathVariable String id) {
        InventoryItemResponse item = inventoryItemService.findById(id);
        if (item == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(item);
    }

    @PostMapping
    public ResponseEntity<InventoryItemResponse> create(@Valid @RequestBody CreateInventoryItemRequest req) {
        InventoryItemResponse item = inventoryItemService.create(req);
        return ResponseEntity.status(201).body(item);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryItemResponse> update(
            @PathVariable String id,
            @Valid @RequestBody UpdateInventoryItemRequest req) {
        InventoryItemResponse item = inventoryItemService.update(id, req);
        return ResponseEntity.ok(item);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        inventoryItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
