package ma.farmsense.dto.poultry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.farmsense.entity.Supplier;

import java.time.Instant;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SupplierResponse {

    private UUID id;
    private String name;
    private String phone;
    private String email;
    private String address;
    private String productsSupplied;
    private String notes;
    private Instant createdAt;
    private Instant updatedAt;

    public static SupplierResponse from(Supplier s) {
        return SupplierResponse.builder()
                .id(s.getId())
                .name(s.getName())
                .phone(s.getPhone())
                .email(s.getEmail())
                .address(s.getAddress())
                .productsSupplied(s.getProductsSupplied())
                .notes(s.getNotes())
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt())
                .build();
    }
}
