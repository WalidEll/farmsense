package ma.farmsense.dto.cropplan;

import lombok.Data;
import ma.farmsense.entity.LocationType;

@Data
public class UpdateLocationRequest {

    private String name;
    private String nameAr;
    private String nameEn;
    private String description;
    private String descriptionAr;
    private String descriptionEn;
    private LocationType locationType;
    private Double areaM2;
    private Double latitude;
    private Double longitude;
}
