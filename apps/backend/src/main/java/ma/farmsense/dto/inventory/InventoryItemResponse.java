package ma.farmsense.dto.inventory;

import java.math.BigDecimal;

public record InventoryItemResponse(
        String id,
        String name,
        String sku,
        String category,
        BigDecimal unitCost,
        BigDecimal currentQuantity,
        BigDecimal minQuantity,
        BigDecimal maxQuantity,
        BigDecimal reorderLevel,
        String description
) {}
