package com.SpringBootMVC.ExpensesTracker.DTO;

import java.util.List;

public class SharedExpenseRequestDTO {
    private int groupId;
    private int paidByClientId;
    private String description;
    private int totalAmount;
    private List<Integer> splitClientIds;

    public int getGroupId() { return groupId; }
    public void setGroupId(int groupId) { this.groupId = groupId; }
    public int getPaidByClientId() { return paidByClientId; }
    public void setPaidByClientId(int paidByClientId) { this.paidByClientId = paidByClientId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getTotalAmount() { return totalAmount; }
    public void setTotalAmount(int totalAmount) { this.totalAmount = totalAmount; }
    public List<Integer> getSplitClientIds() { return splitClientIds; }
    public void setSplitClientIds(List<Integer> splitClientIds) { this.splitClientIds = splitClientIds; }
}
