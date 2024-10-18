package com.business.application.domain;


import jakarta.persistence.Table;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;

@Entity
@Table(name = "webscraped_products")
public class WebScrapedProduct extends AbstractEntity {

    private String title;
    private String type;
    private Double price;
    private String supplier;

    @Column(nullable = true)
    private Integer quantity;

    @ManyToMany(mappedBy = "webScrapedProducts", fetch = FetchType.LAZY)
    private List<Inventory> inventories;

    


//Getters and setters

public Integer getQuantity() {
    return quantity;
}
public void setQuantity(Integer quantity) {
    this.quantity = quantity;
}

public String getName() {
    return title;
}

public void setName(String title) {
    this.title = title;
}

public String getCategory() {
    return type;
}

public void setCategory(String category) {
    this.type = category;
}

public Double getPrice() {
    return price;
}

public void setPrice(Double price) {
    this.price = price;
}

public String getSupplier() {
    return supplier;
}

public void setSupplier(String supplier) {
    this.supplier = supplier;
}



@PrePersist
    public void prePersist() {
        if (this.quantity == null) {
            this.quantity = 0;
        }
    }
}