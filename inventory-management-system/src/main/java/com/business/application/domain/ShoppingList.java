package com.business.application.domain;

import java.util.Date;



import java.util.ArrayList;
import jakarta.persistence.Entity;
import java.text.SimpleDateFormat;

@Entity
public class ShoppingList extends AbstractEntity{

    private int listId;
    private int managerId;
    private int storeId;
    private Date date;
    private String name;
    private ArrayList<ShoppingListItem> products;
    private String status;

    public ShoppingList(int listId, int managerId,Date date ,int storeId, String name, ArrayList<ShoppingListItem> products, String status) {

        setListId(listId);
        setManagerId(managerId);
        setStoreId(storeId);
        setDate(date);
        setName(name);
        setProducts(products);
        setStatus(status);

    }

    public int getListId() {
        return listId;
    }

    public void setListId(int listId) {
        this.listId = listId;
    }

    public int getManagerId() {
        return managerId;
    }

    public void setManagerId(int managerId) {
        this.managerId = managerId;
    }

    public int getStoreId() {
        return storeId;
    }

    public ArrayList<ShoppingListItem> getProducts() {
        return products;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public Date getDate() {
        return date;
    }

    public String getDateString(){
        SimpleDateFormat format = new SimpleDateFormat("d MMM yyyy");
        String dateStr = format.format(getDate());
        return dateStr;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProducts(ArrayList<ShoppingListItem> products) {
        this.products = products;
    }

}
