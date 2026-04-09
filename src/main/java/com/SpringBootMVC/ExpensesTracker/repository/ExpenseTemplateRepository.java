package com.SpringBootMVC.ExpensesTracker.repository;

import com.SpringBootMVC.ExpensesTracker.entity.ExpenseTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseTemplateRepository extends JpaRepository<ExpenseTemplate, Integer> {
    List<ExpenseTemplate> findByClientIdOrderByCreatedAtDesc(int clientId);
}
