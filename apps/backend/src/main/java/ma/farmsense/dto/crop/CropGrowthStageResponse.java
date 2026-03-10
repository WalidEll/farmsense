package ma.farmsense.dto.crop;

import lombok.Builder;
import lombok.Data;
import ma.farmsense.entity.CropGrowthStage;

import java.util.UUID;

@Data
@Builder
public class CropGrowthStageResponse {

    private UUID id;
    private Integer stageOrder;
    private String name;
    private String nameAr;
    private String nameDarija;
    private Integer durationDays;
    private String description;
    private String descriptionAr;
    private String descriptionDarija;

    public static CropGrowthStageResponse from(CropGrowthStage s) {
        return CropGrowthStageResponse.builder()
                .id(s.getId())
                .stageOrder(s.getStageOrder())
                .name(s.getName())
                .nameAr(s.getNameAr())
                .nameDarija(s.getNameDarija())
                .durationDays(s.getDurationDays())
                .description(s.getDescription())
                .descriptionAr(s.getDescriptionAr())
                .descriptionDarija(s.getDescriptionDarija())
                .build();
    }
}
