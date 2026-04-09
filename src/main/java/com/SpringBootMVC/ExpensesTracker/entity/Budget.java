package com.SpringBootMVC.ExpensesTracker.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"client_id", "category_id", "month_key"}))
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @Column(name = "month_key", length = 7, nullable = false)
    private String monthKey;
    @Column(name = "limit_amount", nullable = false)
    private int limitAmount;
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public String getMonthKey() { return monthKey; }
    public void setMonthKey(String monthKey) { this.monthKey = monthKey; }
    public int getLimitAmount() { return limitAmount; }
    public void setLimitAmount(int limitAmount) { this.limitAmount = limitAmount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
