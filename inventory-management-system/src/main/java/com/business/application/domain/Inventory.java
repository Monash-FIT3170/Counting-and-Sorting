package com.business.application.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.List;


import jakarta.persistence.*;
@Entity
@Table(name = "inventory_alpha")
public class Inventory  {
    // Define the primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id")
    private Long inventoryId;

    

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "inventory", fetch = FetchType.LAZY)
    private Store store;

    @ManyToMany
    @JoinTable(
            name = "inventory_products",
            joinColumns = @JoinColumn(name = "inventory_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<WebScrapedProduct> webScrapedProducts;

    // Constructors, getters, and setters
    public Inventory() {
    }

    public Long getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Long inventoryId) {
        this.inventoryId = inventoryId;
    }

    public List<WebScrapedProduct> getWebScrapedProducts() {
        return webScrapedProducts;
    }

    public void setWebScrapedProducts(List<WebScrapedProduct> webScrapedProducts) {
        this.webScrapedProducts = webScrapedProducts;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

}