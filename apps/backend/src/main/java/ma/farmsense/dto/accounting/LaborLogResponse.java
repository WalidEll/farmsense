package ma.farmsense.dto.accounting;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.UUID;

public record LaborLogResponse(
        UUID id,
        String workerName,
        String workerRole,
        BigDecimal hourlyRate,
        Double hoursWorked,
        LocalDate workDate,
        String activity,
        UUID transactionId,
        UUID flockId,
        String flockName,
        UUID cropPlanId,
        String cropPlanName,
        UUID farmLocationId,
        String farmLocationName,
        String notes,
        Instant createdAt,
        Instant updatedAt
) {}
