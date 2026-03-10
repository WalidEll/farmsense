package ma.farmsense.dto.cropplan;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ma.farmsense.entity.YieldUnit;

@Data
public class PlantingYieldRequest {

    @NotNull
    private Double yieldAmount;

    @NotNull
    private YieldUnit yieldUnit;

    private Integer qualityRating;
    private String harvestNotes;
}
