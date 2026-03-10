package ma.farmsense.dto.cropplan;

import lombok.Data;
import ma.farmsense.entity.PlantingStatus;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class UpdatePlantingRequest {

    private UUID farmLocationId;
    private PlantingStatus status;
    private Integer quantity;
    private Double areaM2;
    private String notes;
    private LocalDate plannedSowDate;
    private LocalDate plannedTransplantDate;
    private LocalDate plannedHarvestDate;
    private String failureReason;
}
