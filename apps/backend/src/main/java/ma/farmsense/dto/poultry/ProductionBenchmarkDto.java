package ma.farmsense.dto.poultry;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ma.farmsense.entity.ProductionBenchmark;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductionBenchmarkDto(
        UUID id,
        @NotBlank String metricType,
        @NotNull @DecimalMin("0.0001") BigDecimal expectedValue,
        @NotBlank String unit,
        Integer ageStartDays,
        Integer ageEndDays,
        String notes,
        Integer sortOrder
) {
    public static ProductionBenchmarkDto from(ProductionBenchmark benchmark) {
        return new ProductionBenchmarkDto(
                benchmark.getId(),
                benchmark.getMetricType(),
                benchmark.getExpectedValue(),
                benchmark.getUnit(),
                benchmark.getAgeStartDays(),
                benchmark.getAgeEndDays(),
                benchmark.getNotes(),
                benchmark.getSortOrder()
        );
    }
}
