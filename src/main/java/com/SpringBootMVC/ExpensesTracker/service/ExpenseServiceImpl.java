package com.SpringBootMVC.ExpensesTracker.service;

import com.SpringBootMVC.ExpensesTracker.DTO.ExpenseDTO;
import com.SpringBootMVC.ExpensesTracker.DTO.FilterDTO;
import com.SpringBootMVC.ExpensesTracker.entity.Category;
import com.SpringBootMVC.ExpensesTracker.entity.Client;
import com.SpringBootMVC.ExpensesTracker.entity.Expense;
import com.SpringBootMVC.ExpensesTracker.repository.ExpenseRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExpenseServiceImpl implements ExpenseService {
    private static final Logger log = LoggerFactory.getLogger(ExpenseServiceImpl.class);

    ExpenseRepository expenseRepository;
    ClientService clientService;
    CategoryService categoryService;
    BudgetService budgetService;
    EntityManager entityManager;

    @Autowired
    public ExpenseServiceImpl(ExpenseRepository expenseRepository, ClientService clientService,
                              CategoryService categoryService, BudgetService budgetService, EntityManager entityManager) {
        this.expenseRepository = expenseRepository;
        this.clientService = clientService;
        this.categoryService = categoryService;
        this.budgetService = budgetService;
        this.entityManager = entityManager;
    }


    @Override
    public Expense findExpenseById(int id) {
        return expenseRepository.findById(id).orElse(null);
    }

    @Transactional
    @Override
    public void save(ExpenseDTO expenseDTO) {
        log.debug("Saving expense payload: {}", expenseDTO);
        Expense expense = new Expense();
        expense.setAmount(expenseDTO.getAmount());
        expense.setDateTime(expenseDTO.getDateTime());
        expense.setDescription(expenseDTO.getDescription());
        expense.setClient(clientService.findClientById(expenseDTO.getClientId()));
        Category category = categoryService.findCategoryByName(expenseDTO.getCategory());
        expense.setCategory(category);
        expenseRepository.save(expense);
        if (category != null) {
            budgetService.evaluateAndNotify(expenseDTO.getClientId(), category.getId(), monthKey(expense.getDateTime()));
        }
    }

    @Override
    public void update(ExpenseDTO expenseDTO) {
        Expense existingExpense = expenseRepository.findById(expenseDTO.getExpenseId()).orElse(null);
        if (existingExpense == null) {
            log.warn("Expense update skipped because expense id {} was not found", expenseDTO.getExpenseId());
            return;
        }
        existingExpense.setAmount(expenseDTO.getAmount());
        existingExpense.setDateTime(expenseDTO.getDateTime());
        existingExpense.setDescription(expenseDTO.getDescription());
        Category category = categoryService.findCategoryByName(expenseDTO.getCategory());
        existingExpense.setCategory(category);
        expenseRepository.save(existingExpense);
        if (existingExpense.getClient() != null && category != null) {
            budgetService.evaluateAndNotify(existingExpense.getClient().getId(), category.getId(), monthKey(existingExpense.getDateTime()));
        }
    }

    @Override
    public List<Expense> findAllExpenses() {
        return expenseRepository.findAll();
    }

    @Override
    public List<Expense> findAllExpensesByClientId(int id) {
        return expenseRepository.findByClientId(id);
    }

    @Override
    public void deleteExpenseById(int id) {
        expenseRepository.deleteById(id);
    }

    @Override
    public List<Expense> findFilterResult(int clientId, FilterDTO filter) {
        StringBuilder queryBuilder = new StringBuilder("select e from Expense e where e.client.id = :clientId");
        List<String> debugParts = new ArrayList<>();
        Integer categoryId = null;

        String categoryName = filter.getCategory();
        if (categoryName != null && !"all".equalsIgnoreCase(categoryName)) {
            Category category = categoryService.findCategoryByName(categoryName);
            if (category == null) {
                log.info("No category found for '{}', returning empty report result", categoryName);
                return List.of();
            }
            queryBuilder.append(" and e.category.id = :categoryId");
            categoryId = category.getId();
            debugParts.add("categoryId=" + categoryId);
        }

        int from = Math.max(filter.getFrom(), 0);
        int to = filter.getTo() > 0 ? filter.getTo() : Integer.MAX_VALUE;
        if (to < from) {
            int temp = from;
            from = to;
            to = temp;
        }
        queryBuilder.append(" and e.amount between :from and :to");
        debugParts.add("amountRange=" + from + "-" + (to == Integer.MAX_VALUE ? "MAX" : to));

        if (filter.getYear() != null && !"all".equalsIgnoreCase(filter.getYear())) {
            queryBuilder.append(" and SUBSTRING(e.dateTime, 1, 4) = :year");
            debugParts.add("year=" + filter.getYear());
        }
        if (filter.getMonth() != null && !"all".equalsIgnoreCase(filter.getMonth())) {
            queryBuilder.append(" and SUBSTRING(e.dateTime, 6, 2) = :month");
            debugParts.add("month=" + filter.getMonth());
        }
        if (filter.getKeyword() != null && !filter.getKeyword().trim().isEmpty()) {
            queryBuilder.append(" and lower(e.description) like :keyword");
            debugParts.add("keyword=" + filter.getKeyword().trim());
        }
        if (filter.getDatePreset() != null && !"all".equalsIgnoreCase(filter.getDatePreset())) {
            LocalDate today = LocalDate.now();
            switch (filter.getDatePreset()) {
                case "today":
                    queryBuilder.append(" and SUBSTRING(e.dateTime, 1, 10) = :day");
                    break;
                case "thisWeek":
                    LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
                    LocalDate weekEnd = weekStart.plusDays(6);
                    queryBuilder.append(" and SUBSTRING(e.dateTime, 1, 10) between :weekStart and :weekEnd");
                    break;
                case "thisMonth":
                    queryBuilder.append(" and SUBSTRING(e.dateTime, 1, 7) = :thisMonth");
                    break;
                default:
                    break;
            }
            debugParts.add("datePreset=" + filter.getDatePreset());
        }

        String sortBy = filter.getSortBy() != null ? filter.getSortBy().trim() : "";
        switch (sortBy) {
            case "amountAsc":
                queryBuilder.append(" order by e.amount asc");
                break;
            case "amountDesc":
                queryBuilder.append(" order by e.amount desc");
                break;
            case "dateAsc":
                queryBuilder.append(" order by e.dateTime asc");
                break;
            default:
                queryBuilder.append(" order by e.dateTime desc");
                sortBy = "dateDesc";
                break;
        }
        debugParts.add("sortBy=" + sortBy);

        TypedQuery<Expense> expenseTypedQuery = entityManager.createQuery(queryBuilder.toString(), Expense.class);
        expenseTypedQuery.setParameter("clientId", clientId);
        expenseTypedQuery.setParameter("from", from);
        expenseTypedQuery.setParameter("to", to);

        if (categoryId != null) {
            expenseTypedQuery.setParameter("categoryId", categoryId);
        }
        if (queryBuilder.indexOf(":year") > 0) {
            expenseTypedQuery.setParameter("year", filter.getYear());
        }
        if (queryBuilder.indexOf(":month") > 0) {
            expenseTypedQuery.setParameter("month", filter.getMonth());
        }
        if (queryBuilder.indexOf(":keyword") > 0) {
            expenseTypedQuery.setParameter("keyword", "%" + filter.getKeyword().trim().toLowerCase() + "%");
        }
        if (queryBuilder.indexOf(":day") > 0) {
            expenseTypedQuery.setParameter("day", LocalDate.now().toString());
        }
        if (queryBuilder.indexOf(":weekStart") > 0) {
            LocalDate today = LocalDate.now();
            LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
            expenseTypedQuery.setParameter("weekStart", weekStart.toString());
            expenseTypedQuery.setParameter("weekEnd", weekStart.plusDays(6).toString());
        }
        if (queryBuilder.indexOf(":thisMonth") > 0) {
            expenseTypedQuery.setParameter("thisMonth", LocalDate.now().toString().substring(0, 7));
        }

        List<Expense> expenseList = expenseTypedQuery.getResultList();
        log.debug("Report/filter query for client {} with [{}] returned {} rows",
                clientId, String.join(", ", debugParts), expenseList.size());
        return expenseList;
    }

    @Override
    @Transactional
    public Expense duplicateExpense(int sourceExpenseId, int clientId) {
        Expense source = expenseRepository.findById(sourceExpenseId).orElse(null);
        if (source == null || source.getClient() == null || source.getClient().getId() != clientId) {
            return null;
        }
        Expense copy = new Expense();
        copy.setClient(source.getClient());
        copy.setCategory(source.getCategory());
        copy.setAmount(source.getAmount());
        copy.setDescription(source.getDescription());
        copy.setDateTime(LocalDateTime.now().toString());
        expenseRepository.save(copy);
        return copy;
    }

    @Override
    @Transactional
    public Expense quickAdd(int clientId, String category, int amount) {
        Client client = clientService.findClientById(clientId);
        Category found = categoryService.findCategoryByName(category);
        if (client == null || found == null || amount <= 0) {
            return null;
        }
        Expense expense = new Expense();
        expense.setClient(client);
        expense.setCategory(found);
        expense.setAmount(amount);
        expense.setDescription("Quick add");
        expense.setDateTime(LocalDateTime.now().toString());
        expenseRepository.save(expense);
        budgetService.evaluateAndNotify(clientId, found.getId(), monthKey(expense.getDateTime()));
        return expense;
    }

    @Override
    @Transactional
    public void updateReceiptPath(int expenseId, int clientId, String receiptPath) {
        Expense expense = expenseRepository.findById(expenseId).orElse(null);
        if (expense == null || expense.getClient() == null || expense.getClient().getId() != clientId) {
            return;
        }
        expense.setReceiptPath(receiptPath);
        expenseRepository.save(expense);
    }

    private String monthKey(String dateTime) {
        if (dateTime != null && dateTime.length() >= 7) {
            return dateTime.substring(0, 7);
        }
        return LocalDate.now().toString().substring(0, 7);
    }
}
