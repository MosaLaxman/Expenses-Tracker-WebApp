package com.SpringBootMVC.ExpensesTracker.service;

import com.SpringBootMVC.ExpensesTracker.DTO.BudgetDTO;
import com.SpringBootMVC.ExpensesTracker.DTO.BudgetStatusDTO;

import java.util.List;

public interface BudgetService {
    BudgetDTO upsertBudget(BudgetDTO dto);
    List<BudgetStatusDTO> monthlyStatus(int clientId, String monthKey);
    void evaluateAndNotify(int clientId, int categoryId, String monthKey);
}
