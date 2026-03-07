package ma.farmsense.dto.plant;

import lombok.Data;

@Data
public class UpdatePlantRequest {

    private String name;
    private String species;
    private String location;
    private String photoUrl;

    private Integer soilMin;
    private Integer soilMax;
    private Double tempMin;
    private Double tempMax;
    private Integer lightMin;
}
