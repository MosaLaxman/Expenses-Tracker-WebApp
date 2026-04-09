package com.SpringBootMVC.ExpensesTracker.service;

import com.SpringBootMVC.ExpensesTracker.entity.*;
import com.SpringBootMVC.ExpensesTracker.repository.ExpenseRepository;
import com.SpringBootMVC.ExpensesTracker.repository.RecurringExpenseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecurringExpenseServiceImplTest {

    @Mock
    RecurringExpenseRepository recurringExpenseRepository;
    @Mock
    ExpenseRepository expenseRepository;
    @Mock
    ClientService clientService;
    @Mock
    CategoryService categoryService;
    @Mock
    BudgetService budgetService;
    @Mock
    NotificationService notificationService;

    @InjectMocks
    RecurringExpenseServiceImpl recurringExpenseService;

    @Test
    void processDueRecurringExpensesShouldCreateExpense() {
        Client client = new Client();
        client.setId(1);
        Category category = new Category();
        category.setId(1);
        category.setName("groceries");
        RecurringExpense recurring = new RecurringExpense();
        recurring.setClient(client);
        recurring.setCategory(category);
        recurring.setAmount(200);
        recurring.setDescription("Milk");
        recurring.setFrequency(RecurringFrequency.DAILY);
        recurring.setNextExecutionDate(LocalDate.now());
        recurring.setActive(true);

        when(recurringExpenseRepository.findByActiveTrueAndNextExecutionDateLessThanEqual(any(LocalDate.class)))
                .thenReturn(List.of(recurring));
        when(expenseRepository.save(any(Expense.class))).thenAnswer(i -> i.getArgument(0));
        when(recurringExpenseRepository.save(any(RecurringExpense.class))).thenAnswer(i -> i.getArgument(0));

        int created = recurringExpenseService.processDueRecurringExpenses();

        assertEquals(1, created);
        verify(expenseRepository, times(1)).save(any(Expense.class));
    }
}
