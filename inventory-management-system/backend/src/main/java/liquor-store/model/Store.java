package model;

import javax.persistence.*;

*//**
 * Represents a store in the system.
 *//*
@Entity
@Table(name = "Stores")
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer storeID;
    private String location;

    @OneToOne
    @JoinColumn(name = "ManagerID", referencedColumnName = "UserID")
    private User manager;

    // Default constructor
    public Store() {}

    // constructor with parameters
    public Store(String location, User manager) {
        this.location = location;
        this.manager = manager;
    }

    // Getters
    public Integer getStoreID() {
        return storeID;
    }

    public String getLocation() {
        return location;
    }

    public User getManager() {
        return manager;
    }

    // Setters
    public void setStoreID(Integer storeID) {
        this.storeID = storeID;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }

    // toString method - Don't need this
    @Override
    public String toString() {
        return "Store{" +
                "storeID=" + storeID +
                ", location='" + location + '\'' +
                ", manager=" + (manager != null ? manager.getUsername() : "null") +
                '}';
    }
}
