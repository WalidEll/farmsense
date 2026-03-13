package ma.farmsense.dto.poultry;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UpdateCustomerRequest {

    @Size(max = 255)
    private String name;

    @Size(max = 50)
    private String phone;

    @Email
    @Size(max = 255)
    private String email;

    private String address;
    private String notes;
}
