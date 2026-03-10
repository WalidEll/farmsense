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
    private String nameEn;
    private Integer durationDays;
    private String description;
    private String descriptionAr;
    private String descriptionEn;

    public static CropGrowthStageResponse from(CropGrowthStage s) {
        return CropGrowthStageResponse.builder()
                .id(s.getId())
                .stageOrder(s.getStageOrder())
                .name(s.getName())
                .nameAr(s.getNameAr())
                .nameEn(s.getNameEn())
                .durationDays(s.getDurationDays())
                .description(s.getDescription())
                .descriptionAr(s.getDescriptionAr())
                .descriptionEn(s.getDescriptionEn())
                .build();
    }
}
