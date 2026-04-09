package com.SpringBootMVC.ExpensesTracker.service;

import com.SpringBootMVC.ExpensesTracker.entity.Expense;

import java.util.List;
import java.util.Map;

public interface CalendarService {
    Map<String, List<Expense>> expensesByDate(int clientId, String monthKey);
}
