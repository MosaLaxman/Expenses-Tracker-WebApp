package com.SpringBootMVC.ExpensesTracker.service;

import com.SpringBootMVC.ExpensesTracker.DTO.BudgetDTO;
import com.SpringBootMVC.ExpensesTracker.DTO.BudgetStatusDTO;
import com.SpringBootMVC.ExpensesTracker.entity.Budget;
import com.SpringBootMVC.ExpensesTracker.entity.Category;
import com.SpringBootMVC.ExpensesTracker.entity.Client;
import com.SpringBootMVC.ExpensesTracker.entity.NotificationType;
import com.SpringBootMVC.ExpensesTracker.repository.BudgetRepository;
import com.SpringBootMVC.ExpensesTracker.repository.ExpenseRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BudgetServiceImpl implements BudgetService {
    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;
    private final ClientService clientService;
    private final CategoryService categoryService;
    private final NotificationService notificationService;

    public BudgetServiceImpl(BudgetRepository budgetRepository, ExpenseRepository expenseRepository, ClientService clientService,
                             CategoryService categoryService, NotificationService notificationService) {
        this.budgetRepository = budgetRepository;
        this.expenseRepository = expenseRepository;
        this.clientService = clientService;
        this.categoryService = categoryService;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public BudgetDTO upsertBudget(BudgetDTO dto) {
        Client client = clientService.findClientById(dto.getClientId());
        Category category = categoryService.findCategoryByName(dto.getCategory());
        if (client == null || category == null || dto.getLimitAmount() <= 0) {
            return null;
        }
        String monthKey = dto.getMonthKey() != null && dto.getMonthKey().length() == 7 ? dto.getMonthKey() : YearMonth.now().toString();
        Optional<Budget> existing = budgetRepository.findByClientIdAndCategoryIdAndMonthKey(client.getId(), category.getId(), monthKey);
        Budget budget = existing.orElseGet(Budget::new);
        budget.setClient(client);
        budget.setCategory(category);
        budget.setMonthKey(monthKey);
        budget.setLimitAmount(dto.getLimitAmount());
        budgetRepository.save(budget);
        BudgetDTO result = new BudgetDTO();
        result.setId(budget.getId());
        result.setClientId(client.getId());
        result.setCategory(category.getName());
        result.setMonthKey(monthKey);
        result.setLimitAmount(budget.getLimitAmount());
        return result;
    }

    @Override
    public List<BudgetStatusDTO> monthlyStatus(int clientId, String monthKey) {
        String key = monthKey != null && monthKey.length() == 7 ? monthKey : YearMonth.now().toString();
        List<Budget> budgets = budgetRepository.findByClientIdAndMonthKey(clientId, key);
        List<BudgetStatusDTO> statuses = new ArrayList<>();
        for (Budget budget : budgets) {
            int spent = expenseRepository.sumByClientCategoryMonth(clientId, budget.getCategory().getId(), key);
            BudgetStatusDTO status = new BudgetStatusDTO();
            status.setCategory(budget.getCategory().getName());
            status.setMonthKey(key);
            status.setLimitAmount(budget.getLimitAmount());
            status.setSpentAmount(spent);
            status.setRemainingAmount(budget.getLimitAmount() - spent);
            status.setExceeded(spent > budget.getLimitAmount());
            status.setProgressPercent(budget.getLimitAmount() > 0 ? Math.min(100, (spent * 100) / budget.getLimitAmount()) : 0);
            statuses.add(status);
        }
        return statuses;
    }

    @Override
    public void evaluateAndNotify(int clientId, int categoryId, String monthKey) {
        budgetRepository.findByClientIdAndCategoryIdAndMonthKey(clientId, categoryId, monthKey).ifPresent(budget -> {
            int spent = expenseRepository.sumByClientCategoryMonth(clientId, categoryId, monthKey);
            if (spent > budget.getLimitAmount()) {
                notificationService.createNotification(
                        clientId,
                        "Budget exceeded for " + budget.getCategory().getName() + " in " + monthKey
                                + ". Limit: " + budget.getLimitAmount() + ", Spent: " + spent,
                        NotificationType.BUDGET_EXCEEDED
                );
            }
        });
    }
}
