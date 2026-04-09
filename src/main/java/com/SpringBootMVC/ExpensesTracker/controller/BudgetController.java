package com.SpringBootMVC.ExpensesTracker.controller;

import com.SpringBootMVC.ExpensesTracker.DTO.BudgetDTO;
import com.SpringBootMVC.ExpensesTracker.DTO.BudgetStatusDTO;
import com.SpringBootMVC.ExpensesTracker.entity.Client;
import com.SpringBootMVC.ExpensesTracker.service.BudgetService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {
    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @PostMapping
    public ResponseEntity<BudgetDTO> upsert(@RequestBody BudgetDTO dto, HttpSession session) {
        Client client = (Client) session.getAttribute("client");
        if (client == null) {
            return ResponseEntity.status(401).build();
        }
        dto.setClientId(client.getId());
        BudgetDTO saved = budgetService.upsertBudget(dto);
        if (saved == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/status")
    public ResponseEntity<List<BudgetStatusDTO>> status(@RequestParam(required = false) String monthKey, HttpSession session) {
        Client client = (Client) session.getAttribute("client");
        if (client == null) {
            return ResponseEntity.status(401).build();
        }
        String key = monthKey != null ? monthKey : YearMonth.now().toString();
        return ResponseEntity.ok(budgetService.monthlyStatus(client.getId(), key));
    }
}
