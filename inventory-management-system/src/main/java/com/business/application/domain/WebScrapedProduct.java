package com.business.application.domain;

import com.vaadin.flow.component.template.Id;

import jakarta.persistence.Table;
import jakarta.persistence.Entity;

@Entity
@Table(name = "webscraped_products")
public class WebScrapedProduct extends AbstractEntity {
    
    private String title;
    private String type;
    private Double price;
    private String supplier;

    


//Getters and setters
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
}