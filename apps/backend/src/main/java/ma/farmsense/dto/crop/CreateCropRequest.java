package ma.farmsense.dto.crop;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ma.farmsense.entity.CropCategory;
import ma.farmsense.entity.CropDifficulty;

@Data
public class CreateCropRequest {

    @NotBlank
    private String name;

    @NotNull
    private CropCategory category;

    private String nameAr;
    private String nameEn;
    private String scientificName;
    private String description;
    private String descriptionAr;
    private String descriptionEn;
    private String imageUrl;
    private String growingSeason;
    private Integer daysToHarvest;
    private CropDifficulty difficulty;
}
