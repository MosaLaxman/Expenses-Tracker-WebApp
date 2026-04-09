package com.SpringBootMVC.ExpensesTracker.DTO;

public class BudgetStatusDTO {
    private String category;
    private String monthKey;
    private int limitAmount;
    private int spentAmount;
    private int remainingAmount;
    private boolean exceeded;
    private int progressPercent;

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getMonthKey() { return monthKey; }
    public void setMonthKey(String monthKey) { this.monthKey = monthKey; }
    public int getLimitAmount() { return limitAmount; }
    public void setLimitAmount(int limitAmount) { this.limitAmount = limitAmount; }
    public int getSpentAmount() { return spentAmount; }
    public void setSpentAmount(int spentAmount) { this.spentAmount = spentAmount; }
    public int getRemainingAmount() { return remainingAmount; }
    public void setRemainingAmount(int remainingAmount) { this.remainingAmount = remainingAmount; }
    public boolean isExceeded() { return exceeded; }
    public void setExceeded(boolean exceeded) { this.exceeded = exceeded; }
    public int getProgressPercent() { return progressPercent; }
    public void setProgressPercent(int progressPercent) { this.progressPercent = progressPercent; }
}
