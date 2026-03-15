package ma.farmsense.dto.cropplan;

import ma.farmsense.entity.PlantingTask;
import ma.farmsense.entity.PlantingTaskStatus;
import ma.farmsense.entity.PlantingTaskType;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record PlantingTaskResponse(
        UUID id,
        UUID plantingId,
        PlantingTaskType taskType,
        String title,
        String titleAr,
        String titleEn,
        LocalDate dueDate,
        PlantingTaskStatus status,
        Instant completedAt,
        String skipReason,
        Instant createdAt
) {
    public static PlantingTaskResponse from(PlantingTask t) {
        return new PlantingTaskResponse(
                t.getId(),
                t.getPlanting().getId(),
                t.getTaskType(),
                t.getTitle(),
                t.getTitleAr(),
                t.getTitleEn(),
                t.getDueDate(),
                t.getStatus(),
                t.getCompletedAt(),
                t.getSkipReason(),
                t.getCreatedAt()
        );
    }
}
