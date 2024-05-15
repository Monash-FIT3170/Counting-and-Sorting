package com.business.application.domain;

public class ShoppingListItem {
    private Product product;
    private int quantity;

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

    public int getQuantity(){
        return this.quantity;
    }
}


