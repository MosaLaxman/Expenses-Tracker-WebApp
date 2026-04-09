package com.SpringBootMVC.ExpensesTracker.repository;

import com.SpringBootMVC.ExpensesTracker.entity.SplitGroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SplitGroupMemberRepository extends JpaRepository<SplitGroupMember, Integer> {
    List<SplitGroupMember> findByGroupId(int groupId);
    Optional<SplitGroupMember> findByGroupIdAndClientId(int groupId, int clientId);
}
