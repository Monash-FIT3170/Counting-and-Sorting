package com.business.application.domain;

import java.util.ArrayList;
import java.util.List;

public class ListOfShoppingList {
    private static ListOfShoppingList instance;
    private List<ShoppingList> shoppingLists;

    private ListOfShoppingList() {
        // Private constructor to prevent instantiation from outside
        shoppingLists = new ArrayList<>();
    }

    public static synchronized ListOfShoppingList getInstance() {
        if (instance == null) {
            instance = new ListOfShoppingList();
        }
        return instance;
    }

    public List<ShoppingList> getShoppingLists() {
        return shoppingLists;
    }

    public ShoppingList getShoppingList(int index) {
        return shoppingLists.get(index);
    }

    public int getShoppingListLength() {
        return shoppingLists.size();
    }

    public void addShoppingList(ShoppingList shoppingList) {
        shoppingLists.add(shoppingList);
    }

    public void removeShoppingList(ShoppingList shoppingList) {
        shoppingLists.remove(shoppingList);
    }

}
