package ma.farmsense.dto.care;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CareScheduleResponse(
        CareTask watering,
        CareTask fertilising,
        CareTask repotting,
        List<CareLogEntry> recentLog
) {
    public record CareTask(
            int intervalDays,
            Instant lastDoneAt,
            Instant nextDueAt,
            int daysRemaining,
            boolean overdue
    ) {}

    public record CareLogEntry(
            UUID id,
            String taskType,
            Instant doneAt,
            String notes
    ) {}
}
