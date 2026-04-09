package com.SpringBootMVC.ExpensesTracker.service;

import com.SpringBootMVC.ExpensesTracker.entity.Expense;
import com.SpringBootMVC.ExpensesTracker.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {
    private final ExpenseRepository expenseRepository;

    public DashboardServiceImpl(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @Override
    public Map<String, Integer> monthlyTrend(int clientId) {
        List<Object[]> rows = expenseRepository.monthlyTrend(clientId);
        Map<String, Integer> trend = new LinkedHashMap<>();
        for (Object[] row : rows) {
            trend.put(String.valueOf(row[0]), ((Number) row[1]).intValue());
        }
        return trend;
    }

    @Override
    public Map<String, Integer> categoryDistribution(int clientId) {
        List<Object[]> rows = expenseRepository.categoryDistribution(clientId);
        Map<String, Integer> distribution = new LinkedHashMap<>();
        for (Object[] row : rows) {
            distribution.put(String.valueOf(row[0]), ((Number) row[1]).intValue());
        }
        return distribution;
    }

    @Override
    public Map<String, Object> monthComparison(int clientId, String monthKey) {
        YearMonth current = monthKey != null && monthKey.length() == 7 ? YearMonth.parse(monthKey) : YearMonth.now();
        YearMonth previous = current.minusMonths(1);
        int currentTotal = expenseRepository.sumByClientMonth(clientId, current.toString());
        int previousTotal = expenseRepository.sumByClientMonth(clientId, previous.toString());
        String currentTopCategory = topCategory(clientId, current.toString());
        String previousTopCategory = topCategory(clientId, previous.toString());
        double changePercent = previousTotal == 0 ? (currentTotal > 0 ? 100.0 : 0.0)
                : ((currentTotal - previousTotal) * 100.0) / previousTotal;
        Map<String, Object> comparison = new LinkedHashMap<>();
        comparison.put("month", current.toString());
        comparison.put("previousMonth", previous.toString());
        comparison.put("currentTotal", currentTotal);
        comparison.put("previousTotal", previousTotal);
        comparison.put("changePercent", Math.round(changePercent * 100.0) / 100.0);
        comparison.put("direction", currentTotal >= previousTotal ? "INCREASE" : "DECREASE");
        comparison.put("currentTopCategory", currentTopCategory);
        comparison.put("previousTopCategory", previousTopCategory);
        comparison.put("topCategoryShift", !currentTopCategory.equals(previousTopCategory));
        return comparison;
    }

    @Override
    public List<Expense> recentExpenses(int clientId) {
        return expenseRepository.findTop10ByClientIdOrderByDateTimeDesc(clientId);
    }

    private String topCategory(int clientId, String monthKey) {
        List<Object[]> rows = expenseRepository.categoryDistributionByMonth(clientId, monthKey);
        String top = "N/A";
        int max = -1;
        for (Object[] row : rows) {
            int amount = ((Number) row[1]).intValue();
            if (amount > max) {
                max = amount;
                top = String.valueOf(row[0]);
            }
        }
        return top;
    }
}
