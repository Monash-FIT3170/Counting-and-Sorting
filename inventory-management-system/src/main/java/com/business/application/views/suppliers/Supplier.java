package com.business.application.views.suppliers;
import com.business.application.views.inventory.ProductFrontend;

public class Supplier {

    private String Supplier;
    private ProductFrontend product;
    private int salePrice;
    private int Qty;

    public void createProduct(int itemID, String name, String category, int quantity, int capacity) {
        this.product = new ProductFrontend(itemID, name, category, quantity, capacity);
    }
    public String getSupplier() {
        return Supplier;
    }
    public void setSupplier(String supplier) {
        this.Supplier = supplier;
    }
    public ProductFrontend getProduct() {
        return product;
    }
    public int getSalePrice() {
        return salePrice;
    }
    public int getQty() {
        return Qty;
    }
    public void setSalePrice(int salePrice) {
        this.salePrice = salePrice;
    }
    public void setQty(int qty) {
        this.Qty = qty;
    }
    public int getItemID() {
        return this.product.getItemID();
    }
    public String getName() {
        return this.product.getName();
    }
    public String getCategory() {
        return this.product.getCategory();
    }
    public int getQuantity() {
        return this.product.getQuantity();
    }
    public int getCapacity() {
        return this.product.getCapacity();
    }
    public double getStockPercentage() {
        return this.product.getStockPercentage();
    }
    public void setProduct(ProductFrontend product) {
        this.product = product;
    }
}
