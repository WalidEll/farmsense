package ma.farmsense.service;

import lombok.RequiredArgsConstructor;
import ma.farmsense.dto.inventory.*;
import ma.farmsense.entity.InventoryItem;
import ma.farmsense.repository.InventoryItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryItemService {

    private final InventoryItemRepository inventoryItemRepository;

    @Transactional(readOnly = true)
    public List<InventoryItemResponse> findAll(String category, String search) {
        return inventoryItemRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public InventoryItemResponse findById(String id) {
        UUID uuid = UUID.fromString(id);
        return inventoryItemRepository.findById(uuid).map(this::toResponse).orElse(null);
    }

    @Transactional
    public InventoryItemResponse create(CreateInventoryItemRequest request) {
        InventoryItem item = new InventoryItem();

        // Generate SKU if not provided
        String sku = (request.sku() == null || request.sku().isBlank())
                ? generateUniqueSku(request.name())
                : request.sku();

        item.setName(request.name());
        item.setSku(sku);
        item.setCategory(request.category());
        item.setUnitCost(request.unitCost());
        item.setCurrentQuantity(request.currentQuantity() != null ? request.currentQuantity() : BigDecimal.ZERO);
        item.setMinQuantity(request.minQuantity() != null ? request.minQuantity() : BigDecimal.ZERO);
        item.setMaxQuantity(request.maxQuantity());
        item.setReorderLevel(request.reorderLevel());
        item.setDescription(request.description());

        InventoryItem saved = inventoryItemRepository.save(item);
        return toResponse(saved);
    }

    @Transactional
    public InventoryItemResponse update(String id, UpdateInventoryItemRequest request) {
        UUID uuid = UUID.fromString(id);
        InventoryItem existing = inventoryItemRepository.findById(uuid)
                .orElseThrow(() -> new RuntimeException("Inventory item not found"));

        if (request.name() != null && !request.name().isBlank()) {
            existing.setName(request.name());
        }
        if (request.sku() != null && !request.sku().isBlank()) {
            existing.setSku(request.sku());
        }
        if (request.category() != null && !request.category().isBlank()) {
            existing.setCategory(request.category());
        }
        if (request.unitCost() != null) {
            existing.setUnitCost(request.unitCost());
        }
        if (request.currentQuantity() != null) {
            existing.setCurrentQuantity(request.currentQuantity());
        }
        if (request.minQuantity() != null) {
            existing.setMinQuantity(request.minQuantity());
        }
        if (request.maxQuantity() != null) {
            existing.setMaxQuantity(request.maxQuantity());
        }
        if (request.reorderLevel() != null) {
            existing.setReorderLevel(request.reorderLevel());
        }
        if (request.description() != null && !request.description().isBlank()) {
            existing.setDescription(request.description());
        }

        InventoryItem saved = inventoryItemRepository.save(existing);
        return toResponse(saved);
    }

    @Transactional
    public void delete(String id) {
        UUID uuid = UUID.fromString(id);
        if (!inventoryItemRepository.existsById(uuid)) {
            throw new RuntimeException("Inventory item not found");
        }
        inventoryItemRepository.deleteById(uuid);
    }

    private InventoryItemResponse toResponse(InventoryItem item) {
        return new InventoryItemResponse(
                item.getId() != null ? item.getId().toString() : null,
                item.getName(),
                item.getSku(),
                item.getCategory(),
                item.getUnitCost(),
                item.getCurrentQuantity(),
                item.getMinQuantity(),
                item.getMaxQuantity(),
                item.getReorderLevel(),
                item.getDescription()
        );
    }

    private String generateUniqueSku(String name) {
        String base = name.replaceAll("[^a-zA-Z0-9]", "-").toUpperCase();
        if (base.length() > 10) {
            base = base.substring(0, 10);
        }
        return base + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
