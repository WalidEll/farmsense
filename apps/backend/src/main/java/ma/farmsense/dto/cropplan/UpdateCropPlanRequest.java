package ma.farmsense.dto.cropplan;

import lombok.Data;
import ma.farmsense.entity.PlanSeason;
import ma.farmsense.entity.PlanStatus;

@Data
public class UpdateCropPlanRequest {

    private String name;
    private String nameAr;
    private String nameEn;
    private String description;
    private String descriptionAr;
    private String descriptionEn;
    private PlanSeason season;
    private Integer year;
    private PlanStatus status;
}
