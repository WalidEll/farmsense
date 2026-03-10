package ma.farmsense.dto.cropplan;

import lombok.Builder;
import lombok.Data;
import ma.farmsense.entity.CropPlan;
import ma.farmsense.entity.PlanSeason;
import ma.farmsense.entity.PlanStatus;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class CropPlanResponse {

    private UUID id;
    private String name;
    private String nameAr;
    private String nameEn;
    private String description;
    private String descriptionAr;
    private String descriptionEn;
    private PlanSeason season;
    private Integer year;
    private PlanStatus status;
    private long plantingCount;
    private Instant createdAt;
    private Instant updatedAt;

    public static CropPlanResponse from(CropPlan p, long plantingCount) {
        return CropPlanResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .nameAr(p.getNameAr())
                .nameEn(p.getNameEn())
                .description(p.getDescription())
                .descriptionAr(p.getDescriptionAr())
                .descriptionEn(p.getDescriptionEn())
                .season(p.getSeason())
                .year(p.getYear())
                .status(p.getStatus())
                .plantingCount(plantingCount)
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
