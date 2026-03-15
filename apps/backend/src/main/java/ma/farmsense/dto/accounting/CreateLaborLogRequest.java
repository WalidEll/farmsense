package ma.farmsense.dto.accounting;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateLaborLogRequest(
        String workerName,
        String workerRole,
        BigDecimal hourlyRate,
        Double hoursWorked,
        LocalDate workDate,
        String activity,
        UUID flockId,
        UUID cropPlanId,
        UUID farmLocationId,
        String notes
) {}
