package ma.farmsense.dto.crop;

import lombok.Builder;
import lombok.Data;
import ma.farmsense.entity.Crop;
import ma.farmsense.entity.CropCategory;
import ma.farmsense.entity.CropDifficulty;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class CropResponse {

    private UUID id;
    private String name;
    private String nameAr;
    private String nameDarija;
    private String scientificName;
    private CropCategory category;
    private String description;
    private String descriptionAr;
    private String descriptionDarija;
    private String imageUrl;
    private String growingSeason;
    private Integer daysToHarvest;
    private CropDifficulty difficulty;
    private Instant createdAt;
    private Instant updatedAt;

    public static CropResponse from(Crop c) {
        return CropResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .nameAr(c.getNameAr())
                .nameDarija(c.getNameDarija())
                .scientificName(c.getScientificName())
                .category(c.getCategory())
                .description(c.getDescription())
                .descriptionAr(c.getDescriptionAr())
                .descriptionDarija(c.getDescriptionDarija())
                .imageUrl(c.getImageUrl())
                .growingSeason(c.getGrowingSeason())
                .daysToHarvest(c.getDaysToHarvest())
                .difficulty(c.getDifficulty())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}
