package com.SpringBootMVC.ExpensesTracker.service;

import com.SpringBootMVC.ExpensesTracker.DTO.SharedExpenseRequestDTO;
import com.SpringBootMVC.ExpensesTracker.DTO.SplitGroupDTO;
import com.SpringBootMVC.ExpensesTracker.entity.*;
import com.SpringBootMVC.ExpensesTracker.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class SharedExpenseServiceImpl implements SharedExpenseService {
    private final SplitGroupRepository splitGroupRepository;
    private final SplitGroupMemberRepository splitGroupMemberRepository;
    private final SharedExpenseRepository sharedExpenseRepository;
    private final SharedExpenseShareRepository sharedExpenseShareRepository;
    private final ClientService clientService;

    public SharedExpenseServiceImpl(SplitGroupRepository splitGroupRepository,
                                    SplitGroupMemberRepository splitGroupMemberRepository,
                                    SharedExpenseRepository sharedExpenseRepository,
                                    SharedExpenseShareRepository sharedExpenseShareRepository,
                                    ClientService clientService) {
        this.splitGroupRepository = splitGroupRepository;
        this.splitGroupMemberRepository = splitGroupMemberRepository;
        this.sharedExpenseRepository = sharedExpenseRepository;
        this.sharedExpenseShareRepository = sharedExpenseShareRepository;
        this.clientService = clientService;
    }

    @Override
    @Transactional
    public SplitGroupDTO createGroup(int ownerClientId, String name) {
        Client owner = clientService.findClientById(ownerClientId);
        if (owner == null || name == null || name.trim().isEmpty()) {
            return null;
        }
        SplitGroup group = new SplitGroup();
        group.setName(name.trim());
        group.setOwnerClient(owner);
        splitGroupRepository.save(group);
        addMember(group.getId(), ownerClientId, ownerClientId);

        SplitGroupDTO dto = new SplitGroupDTO();
        dto.setId(group.getId());
        dto.setName(group.getName());
        dto.setOwnerClientId(ownerClientId);
        return dto;
    }

    @Override
    @Transactional
    public void addMember(int groupId, int ownerClientId, int memberClientId) {
        SplitGroup group = splitGroupRepository.findById(groupId).orElse(null);
        Client member = clientService.findClientById(memberClientId);
        if (group == null || member == null || group.getOwnerClient().getId() != ownerClientId) {
            return;
        }
        if (splitGroupMemberRepository.findByGroupIdAndClientId(groupId, memberClientId).isPresent()) {
            return;
        }
        SplitGroupMember groupMember = new SplitGroupMember();
        groupMember.setGroup(group);
        groupMember.setClient(member);
        splitGroupMemberRepository.save(groupMember);
    }

    @Override
    @Transactional
    public void addSharedExpense(SharedExpenseRequestDTO dto, int ownerClientId) {
        SplitGroup group = splitGroupRepository.findById(dto.getGroupId()).orElse(null);
        if (group == null || group.getOwnerClient().getId() != ownerClientId) {
            return;
        }
        Client payer = clientService.findClientById(dto.getPaidByClientId());
        if (payer == null || dto.getSplitClientIds() == null || dto.getSplitClientIds().isEmpty()) {
            return;
        }
        SharedExpense sharedExpense = new SharedExpense();
        sharedExpense.setGroup(group);
        sharedExpense.setPaidByClient(payer);
        sharedExpense.setDescription(dto.getDescription());
        sharedExpense.setTotalAmount(dto.getTotalAmount());
        sharedExpenseRepository.save(sharedExpense);

        int splitAmount = dto.getTotalAmount() / dto.getSplitClientIds().size();
        for (Integer clientId : dto.getSplitClientIds()) {
            Client member = clientService.findClientById(clientId);
            if (member == null) {
                continue;
            }
            SharedExpenseShare share = new SharedExpenseShare();
            share.setSharedExpense(sharedExpense);
            share.setClient(member);
            share.setShareAmount(splitAmount);
            share.setSettled(false);
            sharedExpenseShareRepository.save(share);
        }
    }

    @Override
    public List<SplitGroupDTO> groups(int ownerClientId) {
        List<SplitGroupDTO> result = new ArrayList<>();
        for (SplitGroup group : splitGroupRepository.findByOwnerClientIdOrderByCreatedAtDesc(ownerClientId)) {
            SplitGroupDTO dto = new SplitGroupDTO();
            dto.setId(group.getId());
            dto.setName(group.getName());
            dto.setOwnerClientId(ownerClientId);
            result.add(dto);
        }
        return result;
    }

    @Override
    public Map<Integer, Integer> balances(int groupId, int requesterClientId) {
        SplitGroup group = splitGroupRepository.findById(groupId).orElse(null);
        if (group == null || group.getOwnerClient().getId() != requesterClientId) {
            return Map.of();
        }
        Map<Integer, Integer> balances = new LinkedHashMap<>();
        for (SharedExpense sharedExpense : sharedExpenseRepository.findByGroupIdOrderByDateTimeDesc(groupId)) {
            int payerId = sharedExpense.getPaidByClient().getId();
            balances.put(payerId, balances.getOrDefault(payerId, 0) + sharedExpense.getTotalAmount());
            for (SharedExpenseShare share : sharedExpenseShareRepository.findBySharedExpenseId(sharedExpense.getId())) {
                int memberId = share.getClient().getId();
                balances.put(memberId, balances.getOrDefault(memberId, 0) - share.getShareAmount());
            }
        }
        return balances;
    }
}
