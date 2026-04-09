package com.SpringBootMVC.ExpensesTracker.DTO;

public class FilterDTO {
    private String category;
    private int from;
    private int to;
    private String month;
    private String year;
    private String keyword;
    private String sortBy;
    private String datePreset;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getDatePreset() {
        return datePreset;
    }

    public void setDatePreset(String datePreset) {
        this.datePreset = datePreset;
    }

    @Override
    public String toString() {
        return "Filter{" +
                "category='" + category + '\'' +
                ", from=" + from +
                ", to=" + to +
                ", month='" + month + '\'' +
                ", year='" + year + '\'' +
                ", keyword='" + keyword + '\'' +
                ", sortBy='" + sortBy + '\'' +
                ", datePreset='" + datePreset + '\'' +
                '}';
    }
}
