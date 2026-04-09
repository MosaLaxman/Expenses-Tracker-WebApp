package com.SpringBootMVC.ExpensesTracker.controller;

import com.SpringBootMVC.ExpensesTracker.entity.Client;
import com.SpringBootMVC.ExpensesTracker.entity.Expense;
import com.SpringBootMVC.ExpensesTracker.service.CalendarService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {
    private final CalendarService calendarService;

    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @GetMapping("/expenses-by-date")
    public ResponseEntity<Map<String, List<Expense>>> expensesByDate(@RequestParam(required = false) String monthKey,
                                                                     HttpSession session) {
        Client client = (Client) session.getAttribute("client");
        if (client == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(calendarService.expensesByDate(client.getId(), monthKey));
    }
}
