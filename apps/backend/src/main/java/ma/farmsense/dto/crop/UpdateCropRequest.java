package ma.farmsense.dto.crop;

import lombok.Data;
import ma.farmsense.entity.CropCategory;
import ma.farmsense.entity.CropDifficulty;

@Data
public class UpdateCropRequest {

    private String name;
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
