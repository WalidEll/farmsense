package ma.farmsense.dto.accounting;

import java.math.BigDecimal;

public record CategorySummary(
        String category,
        BigDecimal total
) {}
