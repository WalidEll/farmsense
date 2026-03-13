package ma.farmsense.dto.accounting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LaborLogResponse {
    private UUID id;
    private String workerName;
    private String workerRole;
    private BigDecimal hourlyRate;
    private Double hoursWorked;
    private LocalDate workDate;
    private String activity;
    private UUID transactionId;
    private UUID flockId;
    private String flockName;
    private UUID cropPlanId;
    private String cropPlanName;
    private UUID farmLocationId;
    private String farmLocationName;
    private String notes;
    private Instant createdAt;
    private Instant updatedAt;
}
