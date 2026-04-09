package com.SpringBootMVC.ExpensesTracker.controller;

import com.SpringBootMVC.ExpensesTracker.DTO.BudgetDTO;
import com.SpringBootMVC.ExpensesTracker.DTO.ExpenseTemplateDTO;
import com.SpringBootMVC.ExpensesTracker.DTO.RecurringExpenseDTO;
import com.SpringBootMVC.ExpensesTracker.DTO.SavingsGoalDTO;
import com.SpringBootMVC.ExpensesTracker.entity.Client;
import com.SpringBootMVC.ExpensesTracker.entity.Expense;
import com.SpringBootMVC.ExpensesTracker.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@Controller
public class FinanceHubController {
    private final RecurringExpenseService recurringExpenseService;
    private final BudgetService budgetService;
    private final ExpenseTemplateService expenseTemplateService;
    private final SavingsGoalService savingsGoalService;
    private final InsightService insightService;
    private final CalendarService calendarService;

    public FinanceHubController(RecurringExpenseService recurringExpenseService, BudgetService budgetService,
                                ExpenseTemplateService expenseTemplateService, SavingsGoalService savingsGoalService,
                                InsightService insightService, CalendarService calendarService) {
        this.recurringExpenseService = recurringExpenseService;
        this.budgetService = budgetService;
        this.expenseTemplateService = expenseTemplateService;
        this.savingsGoalService = savingsGoalService;
        this.insightService = insightService;
        this.calendarService = calendarService;
    }

    @GetMapping("/finance-hub")
    public String financeHub(HttpSession session, Model model) {
        Client client = (Client) session.getAttribute("client");
        if (client == null) {
            return "redirect:/showLoginPage";
        }
        String monthKey = YearMonth.now().toString();
        model.addAttribute("recurringExpenses", recurringExpenseService.listByClient(client.getId()));
        model.addAttribute("recurringForm", new RecurringExpenseDTO());
        model.addAttribute("budgetForm", new BudgetDTO());
        model.addAttribute("budgetStatus", budgetService.monthlyStatus(client.getId(), monthKey));
        model.addAttribute("templateForm", new ExpenseTemplateDTO());
        model.addAttribute("templates", expenseTemplateService.list(client.getId()));
        model.addAttribute("goalForm", new SavingsGoalDTO());
        model.addAttribute("goals", savingsGoalService.activeGoals(client.getId(), monthKey));
        model.addAttribute("insights", insightService.insights(client.getId()));
        return "finance-hub";
    }

    @GetMapping("/calendar-view")
    public String calendarView(HttpSession session, Model model) {
        Client client = (Client) session.getAttribute("client");
        if (client == null) {
            return "redirect:/showLoginPage";
        }
        String monthKey = YearMonth.now().toString();
        Map<String, List<Expense>> grouped = calendarService.expensesByDate(client.getId(), monthKey);
        model.addAttribute("monthKey", monthKey);
        model.addAttribute("calendarGroupedExpenses", grouped);
        return "calendar-view";
    }
}
