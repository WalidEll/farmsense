package ma.farmsense.dto.care;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class CareScheduleResponse {

    private CareTask watering;
    private CareTask fertilising;
    private CareTask repotting;
    private List<CareLogEntry> recentLog;

    @Data
    @Builder
    public static class CareTask {
        private int intervalDays;
        private Instant lastDoneAt;
        private Instant nextDueAt;
        private int daysRemaining;   // negative = overdue
        private boolean overdue;
    }

    @Data
    @Builder
    public static class CareLogEntry {
        private UUID id;
        private String taskType;
        private Instant doneAt;
        private String notes;
    }
}
