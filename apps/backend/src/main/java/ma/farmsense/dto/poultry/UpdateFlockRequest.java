package ma.farmsense.dto.poultry;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.farmsense.entity.FlockPurpose;
import ma.farmsense.entity.FlockStatus;

import java.time.LocalDate;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UpdateFlockRequest {

    @Size(max = 255)
    private String name;

    @Size(max = 255)
    private String nameAr;

    @Size(max = 255)
    private String nameEn;

    @Size(max = 255)
    private String breed;

    @Min(1)
    private Integer birdCount;

    private FlockPurpose purpose;
    private FlockStatus status;
    private LocalDate startDate;
    private UUID supplierId;
    private String source;
    private String notes;
}
