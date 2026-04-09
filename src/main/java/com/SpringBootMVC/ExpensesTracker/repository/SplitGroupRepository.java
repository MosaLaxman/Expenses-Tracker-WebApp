package com.SpringBootMVC.ExpensesTracker.repository;

import com.SpringBootMVC.ExpensesTracker.entity.SplitGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SplitGroupRepository extends JpaRepository<SplitGroup, Integer> {
    List<SplitGroup> findByOwnerClientIdOrderByCreatedAtDesc(int ownerClientId);
}
