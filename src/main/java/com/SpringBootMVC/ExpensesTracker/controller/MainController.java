package com.SpringBootMVC.ExpensesTracker.controller;

import com.SpringBootMVC.ExpensesTracker.DTO.ExpenseDTO;
import com.SpringBootMVC.ExpensesTracker.DTO.ExpenseTemplateDTO;
import com.SpringBootMVC.ExpensesTracker.DTO.FilterDTO;
import com.SpringBootMVC.ExpensesTracker.entity.Client;
import com.SpringBootMVC.ExpensesTracker.entity.Expense;
import com.SpringBootMVC.ExpensesTracker.entity.Category;
import com.SpringBootMVC.ExpensesTracker.service.ExpenseTemplateService;
import com.SpringBootMVC.ExpensesTracker.service.ExpenseService;
import com.SpringBootMVC.ExpensesTracker.service.ReceiptStorageService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MainController {
    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    ExpenseService expenseService;
    ExpenseTemplateService expenseTemplateService;
    ReceiptStorageService receiptStorageService;

    @Autowired
    public MainController(ExpenseService expenseService, ExpenseTemplateService expenseTemplateService,
                          ReceiptStorageService receiptStorageService) {
        this.expenseService = expenseService;
        this.expenseTemplateService = expenseTemplateService;
        this.receiptStorageService = receiptStorageService;
    }

    @GetMapping("/")
    public String landingPage(HttpSession session, Model model) {
        Client client = (Client) session.getAttribute("client");
        model.addAttribute("sessionClient", client);
        return "landing-page";
    }

    @GetMapping("/showAdd")
    public String addExpense(Model model) {
        model.addAttribute("expense", new ExpenseDTO());
        return "add-expense";
    }

    @PostMapping("/submitAdd")
    public String submitAdd(@ModelAttribute("expense") ExpenseDTO expenseDTO, HttpSession session) {
        Client client = getClientFromSession(session);
        if (client == null) {
            log.warn("submitAdd requested without authenticated client in session");
            return "redirect:/showLoginPage";
        }
        expenseDTO.setClientId(client.getId());
        expenseService.save(expenseDTO);
        return "redirect:/list";
    }

    @GetMapping("/list")
    public String list(Model model, HttpSession session) {
        Client client = getClientFromSession(session);
        if (client == null) {
            log.warn("list requested without authenticated client in session");
            return "redirect:/showLoginPage";
        }
        int clientId = client.getId();
        List<Expense> expenseList = expenseService.findAllExpensesByClientId(clientId);
        enrichExpensesForView(expenseList);

        model.addAttribute("expenseList", expenseList);
        FilterDTO filterDTO = new FilterDTO();
        filterDTO.setCategory("all");
        filterDTO.setMonth("all");
        filterDTO.setYear("all");
        filterDTO.setSortBy("dateDesc");
        filterDTO.setDatePreset("all");
        model.addAttribute("filter", filterDTO);
        model.addAttribute("templateForm", new ExpenseTemplateDTO());
        try {
            model.addAttribute("templates", expenseTemplateService.list(client.getId()));
        } catch (Exception ex) {
            log.warn("Template list load failed for client {}. Rendering /list without templates.", client.getId());
            model.addAttribute("templates", List.of());
        }
        addInsights(model, expenseList);
        return "list-page";
    }

    @GetMapping("/showUpdate")
    public String showUpdate(@RequestParam("expId") int id, Model model, HttpSession session) {
        Client client = getClientFromSession(session);
        if (client == null) {
            log.warn("showUpdate requested without authenticated client in session");
            return "redirect:/showLoginPage";
        }
        Expense expense = expenseService.findExpenseById(id);
        if (expense == null || !isOwnedByClient(expense, client.getId())) {
            log.warn("showUpdate denied for expense {} and client {}", id, client.getId());
            return "redirect:/list";
        }

        ExpenseDTO expenseDTO = new ExpenseDTO();
        expenseDTO.setAmount(expense.getAmount());
        expenseDTO.setCategory(
                expense.getCategory() != null ? expense.getCategory().getName() : "Uncategorized"
        );
        expenseDTO.setDescription(expense.getDescription());
        expenseDTO.setDateTime(expense.getDateTime());

        model.addAttribute("expense", expenseDTO);
        model.addAttribute("expenseId", id);
        return "update-page";
    }

    @PostMapping("/submitUpdate")
    public String update(@RequestParam("expId") int id, @ModelAttribute("expense") ExpenseDTO expenseDTO, HttpSession session) {
        Client client = getClientFromSession(session);
        if (client == null) {
            log.warn("submitUpdate requested without authenticated client in session");
            return "redirect:/showLoginPage";
        }
        Expense existingExpense = expenseService.findExpenseById(id);
        if (existingExpense == null || !isOwnedByClient(existingExpense, client.getId())) {
            log.warn("submitUpdate denied for expense {} and client {}", id, client.getId());
            return "redirect:/list";
        }
        expenseDTO.setExpenseId(id);
        expenseDTO.setClientId(client.getId());
        expenseService.update(expenseDTO);
        return "redirect:/list";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("expId") int id, HttpSession session) {
        Client client = getClientFromSession(session);
        if (client == null) {
            log.warn("delete requested without authenticated client in session");
            return "redirect:/showLoginPage";
        }

        Expense expense = expenseService.findExpenseById(id);
        if (expense == null || !isOwnedByClient(expense, client.getId())) {
            log.warn("delete denied for expense {} and client {}", id, client.getId());
            return "redirect:/list";
        }
        expenseService.deleteExpenseById(id);
        return "redirect:/list";
    }

    @GetMapping("/duplicate")
    public String duplicate(@RequestParam("expId") int id, HttpSession session) {
        Client client = getClientFromSession(session);
        if (client == null) {
            return "redirect:/showLoginPage";
        }
        expenseService.duplicateExpense(id, client.getId());
        return "redirect:/list";
    }

    @PostMapping("/quickAdd")
    public String quickAdd(@RequestParam("category") String category, @RequestParam("amount") int amount, HttpSession session) {
        Client client = getClientFromSession(session);
        if (client == null) {
            return "redirect:/showLoginPage";
        }
        expenseService.quickAdd(client.getId(), category, amount);
        return "redirect:/list";
    }

    @PostMapping("/saveTemplate")
    public String saveTemplate(@ModelAttribute("templateForm") ExpenseTemplateDTO templateDTO, HttpSession session) {
        Client client = getClientFromSession(session);
        if (client == null) {
            return "redirect:/showLoginPage";
        }
        templateDTO.setClientId(client.getId());
        expenseTemplateService.saveTemplate(templateDTO);
        return "redirect:/list";
    }

    @GetMapping("/applyTemplate")
    public String applyTemplate(@RequestParam("templateId") int templateId, HttpSession session) {
        Client client = getClientFromSession(session);
        if (client == null) {
            return "redirect:/showLoginPage";
        }
        for (ExpenseTemplateDTO template : expenseTemplateService.list(client.getId())) {
            if (template.getId() == templateId) {
                expenseService.quickAdd(client.getId(), template.getCategory(), template.getDefaultAmount());
                break;
            }
        }
        return "redirect:/list";
    }

    @PostMapping("/uploadReceipt")
    public String uploadReceipt(@RequestParam("expId") int id, @RequestParam("receiptFile") MultipartFile file, HttpSession session) {
        Client client = getClientFromSession(session);
        if (client == null) {
            return "redirect:/showLoginPage";
        }
        Expense expense = expenseService.findExpenseById(id);
        if (expense == null || !isOwnedByClient(expense, client.getId())) {
            return "redirect:/list";
        }
        String path = receiptStorageService.store(file, id);
        if (path != null) {
            expenseService.updateReceiptPath(id, client.getId(), path);
        }
        return "redirect:/list";
    }

    @GetMapping("/receipt")
    public ResponseEntity<byte[]> receipt(@RequestParam("expId") int id, HttpSession session) throws IOException {
        Client client = getClientFromSession(session);
        if (client == null) {
            return ResponseEntity.status(401).build();
        }
        Expense expense = expenseService.findExpenseById(id);
        if (expense == null || !isOwnedByClient(expense, client.getId()) || expense.getReceiptPath() == null) {
            return ResponseEntity.notFound().build();
        }
        Path path = receiptStorageService.resolve(expense.getReceiptPath());
        if (!Files.exists(path)) {
            return ResponseEntity.notFound().build();
        }
        String contentType = Files.probeContentType(path);
        return ResponseEntity.ok()
                .header("Content-Type", contentType != null ? contentType : "application/octet-stream")
                .body(StreamUtils.copyToByteArray(Files.newInputStream(path)));
    }

    @PostMapping("/processFilter")
    public String processFilter(@ModelAttribute("filter") FilterDTO filter, Model model, HttpSession session) {
        Client client = getClientFromSession(session);
        if (client == null) {
            log.warn("processFilter requested without authenticated client in session");
            return "redirect:/showLoginPage";
        }

        log.debug("Applying report/filter for client {} with payload {}", client.getId(), filter);
        List<Expense> expenseList = expenseService.findFilterResult(client.getId(), filter);
        enrichExpensesForView(expenseList);
        model.addAttribute("expenseList", expenseList);
        addInsights(model, expenseList);
        return "filter-result";
    }

    private Client getClientFromSession(HttpSession session) {
        return (Client) session.getAttribute("client");
    }

    private void enrichExpensesForView(List<Expense> expenseList) {
        for (Expense expense : expenseList) {
            Category category = expense.getCategory();
            expense.setCategoryName(category != null ? category.getName() : "Uncategorized");

            try {
                LocalDateTime dateTime = LocalDateTime.parse(expense.getDateTime(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                expense.setDate(dateTime.toLocalDate().toString());
                expense.setTime(dateTime.toLocalTime().toString());
            } catch (Exception e) {
                log.warn("Failed parsing dateTime '{}' for expense {}", expense.getDateTime(), expense.getId());
                expense.setDate("N/A");
                expense.setTime("N/A");
            }
        }
    }

    private boolean isOwnedByClient(Expense expense, int clientId) {
        return expense.getClient() != null && expense.getClient().getId() == clientId;
    }

    private void addInsights(Model model, List<Expense> expenses) {
        int totalAmount = 0;
        int maxAmount = 0;
        int thisMonthAmount = 0;
        Map<String, Integer> categoryTotals = new HashMap<>();
        YearMonth currentMonth = YearMonth.now();

        for (Expense expense : expenses) {
            int amount = expense.getAmount();
            totalAmount += amount;
            if (amount > maxAmount) {
                maxAmount = amount;
            }

            String category = expense.getCategoryName() != null ? expense.getCategoryName() : "Uncategorized";
            categoryTotals.put(category, categoryTotals.getOrDefault(category, 0) + amount);

            try {
                LocalDateTime dt = LocalDateTime.parse(expense.getDateTime(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                if (YearMonth.from(dt).equals(currentMonth)) {
                    thisMonthAmount += amount;
                }
            } catch (Exception ex) {
                log.debug("Skipping month aggregation for expense {} due to invalid date", expense.getId());
            }
        }

        String topCategory = "N/A";
        int topCategoryAmount = 0;
        for (Map.Entry<String, Integer> entry : categoryTotals.entrySet()) {
            if (entry.getValue() > topCategoryAmount) {
                topCategoryAmount = entry.getValue();
                topCategory = entry.getKey();
            }
        }

        int count = expenses.size();
        double average = count > 0 ? (double) totalAmount / count : 0.0;

        model.addAttribute("summaryCount", count);
        model.addAttribute("summaryTotalAmount", totalAmount);
        model.addAttribute("summaryAverageAmount", String.format("%.2f", average));
        model.addAttribute("summaryMaxAmount", maxAmount);
        model.addAttribute("summaryTopCategory", topCategory);
        model.addAttribute("summaryTopCategoryAmount", topCategoryAmount);
        model.addAttribute("summaryThisMonthAmount", thisMonthAmount);
    }
}
