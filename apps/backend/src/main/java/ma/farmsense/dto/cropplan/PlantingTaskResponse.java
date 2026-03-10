package ma.farmsense.dto.cropplan;

import lombok.Builder;
import lombok.Data;
import ma.farmsense.entity.PlantingTask;
import ma.farmsense.entity.PlantingTaskStatus;
import ma.farmsense.entity.PlantingTaskType;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class PlantingTaskResponse {

    private UUID id;
    private UUID plantingId;
    private PlantingTaskType taskType;
    private String title;
    private String titleAr;
    private String titleEn;
    private LocalDate dueDate;
    private PlantingTaskStatus status;
    private Instant completedAt;
    private String skipReason;
    private Instant createdAt;

    public static PlantingTaskResponse from(PlantingTask t) {
        return PlantingTaskResponse.builder()
                .id(t.getId())
                .plantingId(t.getPlanting().getId())
                .taskType(t.getTaskType())
                .title(t.getTitle())
                .titleAr(t.getTitleAr())
                .titleEn(t.getTitleEn())
                .dueDate(t.getDueDate())
                .status(t.getStatus())
                .completedAt(t.getCompletedAt())
                .skipReason(t.getSkipReason())
                .createdAt(t.getCreatedAt())
                .build();
    }
}
