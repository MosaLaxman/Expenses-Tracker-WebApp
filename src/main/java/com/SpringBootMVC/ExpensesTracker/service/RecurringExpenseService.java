package com.SpringBootMVC.ExpensesTracker.service;

import com.SpringBootMVC.ExpensesTracker.DTO.RecurringExpenseDTO;

import java.util.List;

public interface RecurringExpenseService {
    RecurringExpenseDTO create(RecurringExpenseDTO dto);
    List<RecurringExpenseDTO> listByClient(int clientId);
    void toggle(int recurringId, int clientId, boolean active);
    int processDueRecurringExpenses();
}
