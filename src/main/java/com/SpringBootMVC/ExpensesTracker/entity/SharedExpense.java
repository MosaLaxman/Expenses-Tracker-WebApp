package com.SpringBootMVC.ExpensesTracker.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class SharedExpense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private SplitGroup group;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "paid_by_client_id", nullable = false)
    private Client paidByClient;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "total_amount", nullable = false)
    private int totalAmount;
    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime = LocalDateTime.now();

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public SplitGroup getGroup() { return group; }
    public void setGroup(SplitGroup group) { this.group = group; }
    public Client getPaidByClient() { return paidByClient; }
    public void setPaidByClient(Client paidByClient) { this.paidByClient = paidByClient; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getTotalAmount() { return totalAmount; }
    public void setTotalAmount(int totalAmount) { this.totalAmount = totalAmount; }
    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }
}
