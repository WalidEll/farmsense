package ma.farmsense.dto.cropplan;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ma.farmsense.entity.LocationType;

@Data
public class CreateLocationRequest {

    @NotBlank
    private String name;

    private String nameAr;
    private String nameEn;
    private String description;
    private String descriptionAr;
    private String descriptionEn;

    @NotNull
    private LocationType locationType;

    private Double areaM2;
    private Double latitude;
    private Double longitude;
}
