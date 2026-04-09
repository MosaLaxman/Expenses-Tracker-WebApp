package com.SpringBootMVC.ExpensesTracker.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"group_id", "client_id"}))
public class SplitGroupMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private SplitGroup group;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
    @Column(name = "joined_at")
    private LocalDateTime joinedAt = LocalDateTime.now();

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public SplitGroup getGroup() { return group; }
    public void setGroup(SplitGroup group) { this.group = group; }
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
}
