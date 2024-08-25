package com.business.application.domain;

import com.vaadin.flow.component.template.Id;

import jakarta.persistence.Table;
import jakarta.persistence.Entity;

@Entity
@Table(name = "webscraped_products")
public class WebScrapedProduct {
    @Id
    private String name;
    private String category;
    private Double price;
    private String supplier;

    


//Getters and setters
public String getName() {
    return name;
}

public void setName(String name) {
    this.name = name;
}

public String getCategory() {
    return category;
}

public void setCategory(String category) {
    this.category = category;
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
}