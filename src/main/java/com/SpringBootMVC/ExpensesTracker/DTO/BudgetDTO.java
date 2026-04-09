package com.SpringBootMVC.ExpensesTracker.DTO;

public class BudgetDTO {
    private int id;
    private int clientId;
    private String category;
    private String monthKey;
    private int limitAmount;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getClientId() { return clientId; }
    public void setClientId(int clientId) { this.clientId = clientId; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getMonthKey() { return monthKey; }
    public void setMonthKey(String monthKey) { this.monthKey = monthKey; }
    public int getLimitAmount() { return limitAmount; }
    public void setLimitAmount(int limitAmount) { this.limitAmount = limitAmount; }
}
