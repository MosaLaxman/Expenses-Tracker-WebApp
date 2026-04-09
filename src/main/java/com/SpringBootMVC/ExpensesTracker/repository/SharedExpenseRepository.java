package com.SpringBootMVC.ExpensesTracker.repository;

import com.SpringBootMVC.ExpensesTracker.entity.SharedExpense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SharedExpenseRepository extends JpaRepository<SharedExpense, Integer> {
    List<SharedExpense> findByGroupIdOrderByDateTimeDesc(int groupId);
}
