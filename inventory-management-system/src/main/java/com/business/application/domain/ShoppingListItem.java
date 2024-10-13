package com.business.application.domain;

public class ShoppingListItem extends AbstractEntity{
    private WebScrapedProduct product;
    private int quantity = 0;
    private int requestedQuantity;

    public ShoppingListItem(WebScrapedProduct product, int quantity) {
        this.product = product;
        this.requestedQuantity = quantity;
        
    }

    public WebScrapedProduct getProduct() {
        return product;
    }

    public Long getProductId(){
        return this.product.getId();
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

    public double getProductPrice() {
        return this.product.getPrice();
    }

    public int getRequestedQuantityStr() {
        return this.requestedQuantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}


