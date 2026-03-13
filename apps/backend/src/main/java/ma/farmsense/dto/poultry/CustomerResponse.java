package ma.farmsense.dto.poultry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.farmsense.entity.Customer;

import java.time.Instant;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CustomerResponse {

    private UUID id;
    private String name;
    private String phone;
    private String email;
    private String address;
    private String notes;
    private Instant createdAt;
    private Instant updatedAt;

    public static CustomerResponse from(Customer c) {
        return CustomerResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .phone(c.getPhone())
                .email(c.getEmail())
                .address(c.getAddress())
                .notes(c.getNotes())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}
