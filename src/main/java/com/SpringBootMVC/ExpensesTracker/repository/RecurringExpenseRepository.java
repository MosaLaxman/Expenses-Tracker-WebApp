package com.SpringBootMVC.ExpensesTracker.repository;

import com.SpringBootMVC.ExpensesTracker.entity.RecurringExpense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RecurringExpenseRepository extends JpaRepository<RecurringExpense, Integer> {
    List<RecurringExpense> findByClientId(int clientId);
    List<RecurringExpense> findByActiveTrueAndNextExecutionDateLessThanEqual(LocalDate date);
}
