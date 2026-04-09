package com.SpringBootMVC.ExpensesTracker.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class ExpenseTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "default_amount", nullable = false)
    private int defaultAmount;
    @Column(name = "default_description", length = 400)
    private String defaultDescription;
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getDefaultAmount() { return defaultAmount; }
    public void setDefaultAmount(int defaultAmount) { this.defaultAmount = defaultAmount; }
    public String getDefaultDescription() { return defaultDescription; }
    public void setDefaultDescription(String defaultDescription) { this.defaultDescription = defaultDescription; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
