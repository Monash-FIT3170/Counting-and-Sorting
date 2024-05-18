package com.business.application.domain;

public class ShoppingListItem extends AbstractEntity{
    private Product product;
    private int quantity;
    private int requestedQuantity;

    public ShoppingListItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        
    }

    public Product getProduct() {
        return product;
    }

    public Long getProductId(){
        return this.product.getProductId();
    }
    public String getProductName(){
        return this.product.getName();
    }

    public String getProductCategory() {
        return this.product.getCategory();
    }

    public int getQuantity(){
        return this.quantity;
    }

    public String getQuantityStr() {
        return String.valueOf(this.quantity);
    }

    public String getRequestedQuantityStr() {
        return String.valueOf(this.requestedQuantity);
    }
}


