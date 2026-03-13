package ma.farmsense.dto.accounting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal netProfit;
    private List<CategorySummary> expensesByCategory;
    private List<CategorySummary> incomesByCategory;
    private List<TransactionResponse> recentTransactions;
    private long pendingApprovals;
}
