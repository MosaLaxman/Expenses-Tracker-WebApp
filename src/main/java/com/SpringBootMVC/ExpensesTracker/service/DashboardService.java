package com.SpringBootMVC.ExpensesTracker.service;

import com.SpringBootMVC.ExpensesTracker.entity.Expense;

import java.util.List;
import java.util.Map;

public interface DashboardService {
    Map<String, Integer> monthlyTrend(int clientId);
    Map<String, Integer> categoryDistribution(int clientId);
    Map<String, Object> monthComparison(int clientId, String monthKey);
    List<Expense> recentExpenses(int clientId);
}
