package com.SpringBootMVC.ExpensesTracker.DTO;

public class ExpenseTemplateDTO {
    private int id;
    private int clientId;
    private String name;
    private String category;
    private int defaultAmount;
    private String defaultDescription;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getClientId() { return clientId; }
    public void setClientId(int clientId) { this.clientId = clientId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public int getDefaultAmount() { return defaultAmount; }
    public void setDefaultAmount(int defaultAmount) { this.defaultAmount = defaultAmount; }
    public String getDefaultDescription() { return defaultDescription; }
    public void setDefaultDescription(String defaultDescription) { this.defaultDescription = defaultDescription; }
}
