package ma.farmsense.dto.cropplan;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CreatePlantingRequest {

    @NotNull
    private UUID cropId;

    private UUID farmLocationId;
    private Integer quantity;
    private Double areaM2;
    private String notes;
    private LocalDate plannedSowDate;
    private LocalDate plannedTransplantDate;
    private LocalDate plannedHarvestDate;
}
