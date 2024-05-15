package com.business.application.views.inventory;

public class ProductFrontend {

    private int itemID;
    private String name;
    private String category;
    private int quantity;
    private int capacity;

    public ProductFrontend(int itemID, String name, String category, int quantity, int capacity) {
        this.itemID = itemID;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.capacity = capacity;
    }


    public int getItemID() {
        return this.itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getCapacity() {
        return this.capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public double getStockPercentage() {
        // System.out.println(this.quantity);
        // System.out.println(this.capacity);
        // System.out.println( (float) this.quantity / this.capacity);
        return ((float) this.quantity / this.capacity) * 100;
    }

    public String getStockStatus() {
        if (this.getStockPercentage() < 30) {
            return "Low";
        } else if (this.getStockPercentage() < 70) {
            return "Medium";
        } else {
            return "High";
        }
    }
}
