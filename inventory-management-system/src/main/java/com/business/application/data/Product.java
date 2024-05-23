package com.business.application.data;


import java.math.BigDecimal;

import jakarta.persistence.Entity;



@Entity
public class Product extends AbstractEntity {

    private Long productId;
    private String name;
    private BigDecimal salePrice;
    private String category;
    private String description;
    private int currentQuantity;
    private int requestedQuantity;

    // Standard getters and setters

    public Long getProductId() {
        return productId;
    }
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public BigDecimal getSalePrice() {
        return salePrice;
    }
    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

     public int getCurrentQuantity() {
        return currentQuantity;
    }
    public void setCurrentQuantity(int currentQuantity) {
        this.currentQuantity = currentQuantity;
    }
    public int getRequestedQuantity() {
        return requestedQuantity;
    }
    public void setRequestedQuantity(int requestedQuantity) {
        this.requestedQuantity = requestedQuantity;
    }
}