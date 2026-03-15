package ma.farmsense.dto.poultry;

import ma.farmsense.entity.Supplier;

import java.time.Instant;
import java.util.UUID;

public record SupplierResponse(
        UUID id,
        String name,
        String phone,
        String email,
        String address,
        String productsSupplied,
        String notes,
        Instant createdAt,
        Instant updatedAt
) {
    public static SupplierResponse from(Supplier s) {
        return new SupplierResponse(
                s.getId(),
                s.getName(),
                s.getPhone(),
                s.getEmail(),
                s.getAddress(),
                s.getProductsSupplied(),
                s.getNotes(),
                s.getCreatedAt(),
                s.getUpdatedAt()
        );
    }
}
