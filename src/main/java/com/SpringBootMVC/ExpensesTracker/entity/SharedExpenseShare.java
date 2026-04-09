package com.SpringBootMVC.ExpensesTracker.entity;

import jakarta.persistence.*;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"shared_expense_id", "client_id"}))
public class SharedExpenseShare {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shared_expense_id", nullable = false)
    private SharedExpense sharedExpense;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
    @Column(name = "share_amount", nullable = false)
    private int shareAmount;
    @Column(name = "settled")
    private boolean settled;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public SharedExpense getSharedExpense() { return sharedExpense; }
    public void setSharedExpense(SharedExpense sharedExpense) { this.sharedExpense = sharedExpense; }
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
    public int getShareAmount() { return shareAmount; }
    public void setShareAmount(int shareAmount) { this.shareAmount = shareAmount; }
    public boolean isSettled() { return settled; }
    public void setSettled(boolean settled) { this.settled = settled; }
}
