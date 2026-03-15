package ma.farmsense.dto.accounting;

import java.math.BigDecimal;
import java.util.List;

public record DashboardResponse(
        BigDecimal totalIncome,
        BigDecimal totalExpenses,
        BigDecimal netProfit,
        List<CategorySummary> expensesByCategory,
        List<CategorySummary> incomesByCategory,
        List<TransactionResponse> recentTransactions,
        long pendingApprovals
) {}
