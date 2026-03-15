package ma.farmsense.dto.poultry;

import ma.farmsense.entity.Customer;

import java.time.Instant;
import java.util.UUID;

public record CustomerResponse(
        UUID id,
        String name,
        String phone,
        String email,
        String address,
        String notes,
        Instant createdAt,
        Instant updatedAt
) {
    public static CustomerResponse from(Customer c) {
        return new CustomerResponse(
                c.getId(),
                c.getName(),
                c.getPhone(),
                c.getEmail(),
                c.getAddress(),
                c.getNotes(),
                c.getCreatedAt(),
                c.getUpdatedAt()
        );
    }
}
