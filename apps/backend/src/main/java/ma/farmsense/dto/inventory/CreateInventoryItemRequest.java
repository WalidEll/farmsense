package ma.farmsense.dto.inventory;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateInventoryItemRequest(
        @NotBlank(message = "Item name is required")
        @Size(max = 255, message = "Name must be less than 255 characters")
        String name,

        @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "SKU can only contain letters, numbers, hyphens and underscores")
        String sku,

        @NotBlank(message = "Category is required")
        @Size(max = 100, message = "Category must be less than 100 characters")
        String category,

        @DecimalMin(value = "0.0", inclusive = true, message = "Unit cost cannot be negative")
        BigDecimal unitCost,

        @DecimalMin(value = "0.0", inclusive = true, message = "Current quantity cannot be negative")
        BigDecimal currentQuantity,

        @DecimalMin(value = "0.0", inclusive = true, message = "Minimum quantity cannot be negative")
        BigDecimal minQuantity,

        @DecimalMin(value = "0.0", inclusive = true, message = "Maximum quantity cannot be negative")
        BigDecimal maxQuantity,

        @DecimalMin(value = "0.0", inclusive = true, message = "Reorder level cannot be negative")
        BigDecimal reorderLevel,

        @Size(max = 500, message = "Description must be less than or equal to 500 characters")
        String description
) {}
