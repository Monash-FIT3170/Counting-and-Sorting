package com.business.application.data;

import java.util.Date;
import jakarta.persistence.Entity;
import java.text.SimpleDateFormat;

@Entity
public class ShoppingList extends AbstractEntity{

    private int listId;
    private int managerId;
    private int storeId;
    private Date date;
    private String name;

    public ShoppingList(int listId, int managerId, int storeId, String name) {

        setListId(listId);
        setManagerId(managerId);
        setStoreId(storeId);
        setDate(new Date());
        setName(name);

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
