package ma.farmsense.dto.plant;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreatePlantRequest {

    @NotBlank
    private String name;

    private String species;
    private String location;
    private String photoUrl;

    // Thresholds — optional, entity defaults apply if null
    private Integer soilMin;
    private Integer soilMax;
    private Double tempMin;
    private Double tempMax;
    private Integer lightMin;
}
