package com.SpringBootMVC.ExpensesTracker.repository;

import com.SpringBootMVC.ExpensesTracker.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Integer> {
    List<Expense> findByClientId(int id);
    List<Expense> findTop10ByClientIdOrderByDateTimeDesc(int clientId);
    List<Expense> findByClientIdAndDateTimeStartingWith(int clientId, String prefix);
    @Query("select coalesce(sum(e.amount), 0) from Expense e where e.client.id = :clientId and e.category.id = :categoryId and SUBSTRING(e.dateTime, 1, 7) = :monthKey")
    Integer sumByClientCategoryMonth(@Param("clientId") int clientId, @Param("categoryId") int categoryId, @Param("monthKey") String monthKey);
    @Query("select coalesce(sum(e.amount), 0) from Expense e where e.client.id = :clientId and SUBSTRING(e.dateTime, 1, 7) = :monthKey")
    Integer sumByClientMonth(@Param("clientId") int clientId, @Param("monthKey") String monthKey);
    @Query("select SUBSTRING(e.dateTime, 1, 7), coalesce(sum(e.amount),0) from Expense e where e.client.id = :clientId group by SUBSTRING(e.dateTime,1,7) order by SUBSTRING(e.dateTime,1,7)")
    List<Object[]> monthlyTrend(@Param("clientId") int clientId);
    @Query("select e.category.name, coalesce(sum(e.amount),0) from Expense e where e.client.id = :clientId group by e.category.name")
    List<Object[]> categoryDistribution(@Param("clientId") int clientId);
    @Query("select e.category.name, coalesce(sum(e.amount),0) from Expense e where e.client.id = :clientId and SUBSTRING(e.dateTime,1,7)=:monthKey group by e.category.name")
    List<Object[]> categoryDistributionByMonth(@Param("clientId") int clientId, @Param("monthKey") String monthKey);
}
