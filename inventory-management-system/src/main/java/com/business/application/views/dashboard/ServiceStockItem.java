package com.business.application.views.dashboard;

public class ServiceStockItem {

    private String status;
    private String stockName;
    private int qtyRemaining;

    public enum Status {
        EXCELLENT, // Represent items with enough stock
        LOW,        // Represent items with low stock
        VERYLOW;   // Represent items with very low stock
    }

    public ServiceStockItem(String status, String stockName, int qtyRemaining) {
        this.status = status;
        this.stockName = stockName;
        this.qtyRemaining = qtyRemaining;
    }

    // Getters and setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public int getQtyRemaining() {
        return qtyRemaining;
    }

    public void setQtyRemaining(int qtyRemaining) {
        this.qtyRemaining = qtyRemaining;
    }
}