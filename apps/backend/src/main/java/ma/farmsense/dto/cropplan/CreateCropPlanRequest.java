package ma.farmsense.dto.cropplan;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ma.farmsense.entity.PlanSeason;

@Data
public class CreateCropPlanRequest {

    @NotBlank
    private String name;

    private String nameAr;
    private String nameEn;
    private String description;
    private String descriptionAr;
    private String descriptionEn;

    @NotNull
    private PlanSeason season;

    @NotNull
    private Integer year;
}
