package com.business.application.domain;

public class ShoppingListItem extends AbstractEntity{
    private Product product;
    private int quantity = 0;
    private int requestedQuantity;

    public ShoppingListItem(Product product, int quantity) {
        this.product = product;
        this.requestedQuantity = quantity;
        
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
        return this.product.getQuantity();
    }

    public int getRequestedQuantity(){
        return this.requestedQuantity;
    }

    public void setRequestedQuantity(int requestedQuantity){
        this.requestedQuantity = requestedQuantity;
    }

    public String getQuantityStr() {
        return String.valueOf(this.quantity);
    }

    public String getProductPrice() {
        return this.product.getSalePrice().toString();
    }

    public String getRequestedQuantityStr() {
        return String.valueOf(this.requestedQuantity);
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}


