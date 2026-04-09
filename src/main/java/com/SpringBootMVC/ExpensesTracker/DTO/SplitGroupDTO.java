package com.SpringBootMVC.ExpensesTracker.DTO;

public class SplitGroupDTO {
    private int id;
    private String name;
    private int ownerClientId;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getOwnerClientId() { return ownerClientId; }
    public void setOwnerClientId(int ownerClientId) { this.ownerClientId = ownerClientId; }
}
