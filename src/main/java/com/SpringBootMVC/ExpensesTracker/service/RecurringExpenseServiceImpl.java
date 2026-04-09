package com.SpringBootMVC.ExpensesTracker.service;

import com.SpringBootMVC.ExpensesTracker.DTO.RecurringExpenseDTO;
import com.SpringBootMVC.ExpensesTracker.entity.*;
import com.SpringBootMVC.ExpensesTracker.repository.ExpenseRepository;
import com.SpringBootMVC.ExpensesTracker.repository.RecurringExpenseRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RecurringExpenseServiceImpl implements RecurringExpenseService {
    private static final Logger log = LoggerFactory.getLogger(RecurringExpenseServiceImpl.class);

    private final RecurringExpenseRepository recurringExpenseRepository;
    private final ExpenseRepository expenseRepository;
    private final ClientService clientService;
    private final CategoryService categoryService;
    private final BudgetService budgetService;
    private final NotificationService notificationService;

    public RecurringExpenseServiceImpl(RecurringExpenseRepository recurringExpenseRepository,
                                       ExpenseRepository expenseRepository,
                                       ClientService clientService,
                                       CategoryService categoryService,
                                       BudgetService budgetService,
                                       NotificationService notificationService) {
        this.recurringExpenseRepository = recurringExpenseRepository;
        this.expenseRepository = expenseRepository;
        this.clientService = clientService;
        this.categoryService = categoryService;
        this.budgetService = budgetService;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public RecurringExpenseDTO create(RecurringExpenseDTO dto) {
        Client client = clientService.findClientById(dto.getClientId());
        Category category = categoryService.findCategoryByName(dto.getCategory());
        if (client == null || category == null || dto.getAmount() <= 0 || dto.getFrequency() == null
                || dto.getNextExecutionDate() == null || dto.getNextExecutionDate().isBlank()) {
            return null;
        }

        RecurringFrequency frequency;
        LocalDate nextDate;
        try {
            frequency = RecurringFrequency.valueOf(dto.getFrequency().toUpperCase());
            nextDate = LocalDate.parse(dto.getNextExecutionDate());
        } catch (Exception ex) {
            return null;
        }

        RecurringExpense recurringExpense = new RecurringExpense();
        recurringExpense.setClient(client);
        recurringExpense.setCategory(category);
        recurringExpense.setAmount(dto.getAmount());
        recurringExpense.setDescription(dto.getDescription());
        recurringExpense.setFrequency(frequency);
        recurringExpense.setNextExecutionDate(nextDate);
        recurringExpense.setActive(dto.isActive());
        recurringExpenseRepository.save(recurringExpense);
        return map(recurringExpense);
    }

    @Override
    public List<RecurringExpenseDTO> listByClient(int clientId) {
        List<RecurringExpense> recurringExpenses = recurringExpenseRepository.findByClientId(clientId);
        List<RecurringExpenseDTO> result = new ArrayList<>();
        for (RecurringExpense recurringExpense : recurringExpenses) {
            result.add(map(recurringExpense));
        }
        return result;
    }

    @Override
    @Transactional
    public void toggle(int recurringId, int clientId, boolean active) {
        recurringExpenseRepository.findById(recurringId).ifPresent(re -> {
            if (re.getClient() != null && re.getClient().getId() == clientId) {
                re.setActive(active);
                recurringExpenseRepository.save(re);
            }
        });
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 */15 * * * *")
    public int processDueRecurringExpenses() {
        LocalDate today = LocalDate.now();
        List<RecurringExpense> dueList = recurringExpenseRepository.findByActiveTrueAndNextExecutionDateLessThanEqual(today);
        int created = 0;
        for (RecurringExpense recurringExpense : dueList) {
            Expense expense = new Expense();
            expense.setClient(recurringExpense.getClient());
            expense.setCategory(recurringExpense.getCategory());
            expense.setAmount(recurringExpense.getAmount());
            expense.setDescription(recurringExpense.getDescription());
            expense.setDateTime(LocalDateTime.now().toString());
            expense.setRecurring(true);
            expense.setFrequency(recurringExpense.getFrequency());
            expense.setRecurringSourceId(recurringExpense.getId());
            expenseRepository.save(expense);
            created++;

            recurringExpense.setLastExecutionAt(LocalDateTime.now());
            recurringExpense.setNextExecutionDate(nextDate(recurringExpense.getNextExecutionDate(), recurringExpense.getFrequency()));
            recurringExpenseRepository.save(recurringExpense);

            String monthKey = LocalDate.now().toString().substring(0, 7);
            budgetService.evaluateAndNotify(recurringExpense.getClient().getId(), recurringExpense.getCategory().getId(), monthKey);
            notificationService.createNotification(
                    recurringExpense.getClient().getId(),
                    "Recurring expense created: " + recurringExpense.getDescription() + " (" + recurringExpense.getAmount() + ")",
                    NotificationType.RECURRING_CREATED
            );
        }
        if (created > 0) {
            log.info("Recurring scheduler created {} expenses", created);
        }
        return created;
    }

    private LocalDate nextDate(LocalDate fromDate, RecurringFrequency frequency) {
        if (frequency == RecurringFrequency.DAILY) {
            return fromDate.plusDays(1);
        }
        if (frequency == RecurringFrequency.WEEKLY) {
            return fromDate.plusWeeks(1);
        }
        return fromDate.plusMonths(1);
    }

    private RecurringExpenseDTO map(RecurringExpense recurringExpense) {
        RecurringExpenseDTO dto = new RecurringExpenseDTO();
        dto.setId(recurringExpense.getId());
        dto.setClientId(recurringExpense.getClient() != null ? recurringExpense.getClient().getId() : 0);
        dto.setCategory(recurringExpense.getCategory() != null ? recurringExpense.getCategory().getName() : "");
        dto.setAmount(recurringExpense.getAmount());
        dto.setDescription(recurringExpense.getDescription());
        dto.setFrequency(recurringExpense.getFrequency() != null ? recurringExpense.getFrequency().name() : "");
        dto.setNextExecutionDate(recurringExpense.getNextExecutionDate() != null ? recurringExpense.getNextExecutionDate().toString() : "");
        dto.setActive(recurringExpense.isActive());
        return dto;
    }
}
