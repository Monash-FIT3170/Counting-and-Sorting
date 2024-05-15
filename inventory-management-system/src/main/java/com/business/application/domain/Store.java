package com.business.application.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "Stores")
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer StoreID;

    @Column(nullable = false)
    private String location;

    @OneToOne
    //@JoinColumn(name = "ManagerID", referencedColumnName = "UserID", unique = true)
    private User manager;

    // Constructors, getters, and setters
    public Store() {
    }

    public Integer getStoreID() {
        return StoreID;
    }

    public void setStoreID(Integer StoreID) {
        this.StoreID = StoreID;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }
}
