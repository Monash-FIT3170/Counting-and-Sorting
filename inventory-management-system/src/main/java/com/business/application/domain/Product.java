package com.business.application.domain;


import java.math.BigDecimal;

import com.vaadin.flow.component.template.Id;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;



@Entity
public class Product extends AbstractEntity {

    @Id
    private Long productId;

    @Column(nullable = false)
    private String name;

    @Column(name = "SalePrice", nullable = false)
    private BigDecimal salePrice;

    @Column(nullable = false)
    private String category;

    @Column
    private String description;

    private int quantity;

    public Product(Long productId, String name, BigDecimal salePrice, String category, String description, int quantity){
        this.productId = productId;
        this.name = name;
        this.salePrice = salePrice;
        this.category = category;
        this.description = description;
        this.quantity = quantity;
    

    }

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
    public int getQuantity(){
        return quantity;
    }
    public void setQuantity(int amount){
        this.quantity = amount;
    }


}