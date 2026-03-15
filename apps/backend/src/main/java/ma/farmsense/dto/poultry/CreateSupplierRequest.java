package ma.farmsense.dto.poultry;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateSupplierRequest(
        @NotBlank @Size(max = 255) String name,
        @Size(max = 50) String phone,
        @Email @Size(max = 255) String email,
        String address,
        String productsSupplied,
        String notes
) {}
