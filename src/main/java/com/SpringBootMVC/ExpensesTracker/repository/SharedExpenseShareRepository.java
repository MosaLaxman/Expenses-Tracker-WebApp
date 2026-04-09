package com.SpringBootMVC.ExpensesTracker.repository;

import com.SpringBootMVC.ExpensesTracker.entity.SharedExpenseShare;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SharedExpenseShareRepository extends JpaRepository<SharedExpenseShare, Integer> {
    List<SharedExpenseShare> findBySharedExpenseId(int sharedExpenseId);
    List<SharedExpenseShare> findByClientId(int clientId);
}
