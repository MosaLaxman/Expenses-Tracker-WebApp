package com.SpringBootMVC.ExpensesTracker.service;

import com.SpringBootMVC.ExpensesTracker.entity.Expense;
import com.SpringBootMVC.ExpensesTracker.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CalendarServiceImpl implements CalendarService {
    private final ExpenseRepository expenseRepository;

    public CalendarServiceImpl(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @Override
    public Map<String, List<Expense>> expensesByDate(int clientId, String monthKey) {
        String key = monthKey != null && monthKey.length() == 7 ? monthKey : YearMonth.now().toString();
        List<Expense> expenses = expenseRepository.findByClientIdAndDateTimeStartingWith(clientId, key);
        Map<String, List<Expense>> grouped = new LinkedHashMap<>();
        for (Expense expense : expenses) {
            String date = expense.getDateTime() != null && expense.getDateTime().length() >= 10
                    ? expense.getDateTime().substring(0, 10) : "unknown";
            grouped.computeIfAbsent(date, x -> new ArrayList<>()).add(expense);
        }
        return grouped;
    }
}
