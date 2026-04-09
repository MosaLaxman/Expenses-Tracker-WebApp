package com.SpringBootMVC.ExpensesTracker.controller;

import com.SpringBootMVC.ExpensesTracker.DTO.ExpenseTemplateDTO;
import com.SpringBootMVC.ExpensesTracker.entity.Client;
import com.SpringBootMVC.ExpensesTracker.service.ExpenseTemplateService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
public class ExpenseTemplateController {
    private final ExpenseTemplateService expenseTemplateService;

    public ExpenseTemplateController(ExpenseTemplateService expenseTemplateService) {
        this.expenseTemplateService = expenseTemplateService;
    }

    @PostMapping
    public ResponseEntity<ExpenseTemplateDTO> create(@RequestBody ExpenseTemplateDTO dto, HttpSession session) {
        Client client = (Client) session.getAttribute("client");
        if (client == null) {
            return ResponseEntity.status(401).build();
        }
        dto.setClientId(client.getId());
        ExpenseTemplateDTO created = expenseTemplateService.saveTemplate(dto);
        return created == null ? ResponseEntity.badRequest().build() : ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<ExpenseTemplateDTO>> list(HttpSession session) {
        Client client = (Client) session.getAttribute("client");
        if (client == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(expenseTemplateService.list(client.getId()));
    }
}
