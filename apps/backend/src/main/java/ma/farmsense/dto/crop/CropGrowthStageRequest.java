package ma.farmsense.dto.crop;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CropGrowthStageRequest {

    @NotNull
    private Integer stageOrder;

    @NotBlank
    private String name;

    private String nameAr;
    private String nameEn;
    private Integer durationDays;
    private String description;
    private String descriptionAr;
    private String descriptionEn;
}
