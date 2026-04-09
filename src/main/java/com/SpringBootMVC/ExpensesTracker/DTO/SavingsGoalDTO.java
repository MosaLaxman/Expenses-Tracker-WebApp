package com.SpringBootMVC.ExpensesTracker.DTO;

public class SavingsGoalDTO {
    private int id;
    private int clientId;
    private String name;
    private int targetAmount;
    private String deadline;
    private int projectedSavedAmount;
    private int progressPercent;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getClientId() { return clientId; }
    public void setClientId(int clientId) { this.clientId = clientId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getTargetAmount() { return targetAmount; }
    public void setTargetAmount(int targetAmount) { this.targetAmount = targetAmount; }
    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
    public int getProjectedSavedAmount() { return projectedSavedAmount; }
    public void setProjectedSavedAmount(int projectedSavedAmount) { this.projectedSavedAmount = projectedSavedAmount; }
    public int getProgressPercent() { return progressPercent; }
    public void setProgressPercent(int progressPercent) { this.progressPercent = progressPercent; }
}
