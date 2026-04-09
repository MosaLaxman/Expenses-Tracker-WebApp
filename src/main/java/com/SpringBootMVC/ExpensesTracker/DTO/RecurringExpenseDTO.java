package com.SpringBootMVC.ExpensesTracker.DTO;

public class RecurringExpenseDTO {
    private int id;
    private int clientId;
    private String category;
    private int amount;
    private String description;
    private String frequency;
    private String nextExecutionDate;
    private boolean active = true;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getClientId() { return clientId; }
    public void setClientId(int clientId) { this.clientId = clientId; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }
    public String getNextExecutionDate() { return nextExecutionDate; }
    public void setNextExecutionDate(String nextExecutionDate) { this.nextExecutionDate = nextExecutionDate; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
