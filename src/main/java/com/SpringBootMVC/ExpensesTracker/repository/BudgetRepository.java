package com.SpringBootMVC.ExpensesTracker.repository;

import com.SpringBootMVC.ExpensesTracker.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Integer> {
    Optional<Budget> findByClientIdAndCategoryIdAndMonthKey(int clientId, int categoryId, String monthKey);
    List<Budget> findByClientIdAndMonthKey(int clientId, String monthKey);
}
