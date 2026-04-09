package com.SpringBootMVC.ExpensesTracker.service;

import com.SpringBootMVC.ExpensesTracker.DTO.BudgetDTO;
import com.SpringBootMVC.ExpensesTracker.entity.Budget;
import com.SpringBootMVC.ExpensesTracker.entity.Category;
import com.SpringBootMVC.ExpensesTracker.entity.Client;
import com.SpringBootMVC.ExpensesTracker.repository.BudgetRepository;
import com.SpringBootMVC.ExpensesTracker.repository.ExpenseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetServiceImplTest {

    @Mock
    BudgetRepository budgetRepository;
    @Mock
    ExpenseRepository expenseRepository;
    @Mock
    ClientService clientService;
    @Mock
    CategoryService categoryService;
    @Mock
    NotificationService notificationService;

    @InjectMocks
    BudgetServiceImpl budgetService;

    @Test
    void upsertBudgetShouldSave() {
        BudgetDTO dto = new BudgetDTO();
        dto.setClientId(1);
        dto.setCategory("groceries");
        dto.setMonthKey("2026-04");
        dto.setLimitAmount(1000);

        Client client = new Client();
        client.setId(1);
        Category category = new Category();
        category.setId(1);
        category.setName("groceries");

        when(clientService.findClientById(1)).thenReturn(client);
        when(categoryService.findCategoryByName("groceries")).thenReturn(category);
        when(budgetRepository.findByClientIdAndCategoryIdAndMonthKey(1, 1, "2026-04"))
                .thenReturn(Optional.empty());
        when(budgetRepository.save(any(Budget.class))).thenAnswer(i -> i.getArgument(0));

        BudgetDTO result = budgetService.upsertBudget(dto);

        assertNotNull(result);
        verify(budgetRepository, times(1)).save(any(Budget.class));
    }
}
