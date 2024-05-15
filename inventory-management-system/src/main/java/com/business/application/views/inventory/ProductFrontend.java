package com.business.application.views.inventory;

public class ProductFrontend {

    private int itemID;
    private String name;
    private String category;
    private int quantity;
    private int capacity;

    public ProductFrontend(int itemID, String name, String category, int quantity) {
        this.itemID = itemID;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.capacity = capacity;
    }


    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public double getStockPercentage() {
        return quantity / capacity * 100;
    }

    public String getStockStatus() {
        if (getStockPercentage() < 30) {
            return "LOW";
        } else if (getStockPercentage() < 70) {
            return "MED";
        } else {
            return "HIGH";
        }
    }
}
