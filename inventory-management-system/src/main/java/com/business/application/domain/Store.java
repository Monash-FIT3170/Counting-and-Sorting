package com.business.application.domain;

import jakarta.persistence.*;



@Entity
@Table(name = "stores")
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "storeid")
    private int storeId;

    @Column(name = "location", nullable = false, length = 255)
    private String location;

    @Column(name = "manager_id", unique = true)
    private Long managerId;

    @Column(name ="budget")
    private int budget;

    // Constructors, Getters, Setters
    public Store() {}

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public int getBudget(){
        return budget;
    }
    public void setBudget(int amount){
        this.budget = amount;
    }

   
}

