package com.SpringBootMVC.ExpensesTracker.service;

import com.SpringBootMVC.ExpensesTracker.DTO.SharedExpenseRequestDTO;
import com.SpringBootMVC.ExpensesTracker.DTO.SplitGroupDTO;

import java.util.List;
import java.util.Map;

public interface SharedExpenseService {
    SplitGroupDTO createGroup(int ownerClientId, String name);
    void addMember(int groupId, int ownerClientId, int memberClientId);
    void addSharedExpense(SharedExpenseRequestDTO dto, int ownerClientId);
    List<SplitGroupDTO> groups(int ownerClientId);
    Map<Integer, Integer> balances(int groupId, int requesterClientId);
}
