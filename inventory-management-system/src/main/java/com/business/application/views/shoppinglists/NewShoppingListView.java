package com.business.application.views.shoppinglists;

import com.business.application.domain.ListOfShoppingList;
import com.business.application.domain.Product;
import com.business.application.domain.ShoppingList;
import com.business.application.domain.ShoppingListItem;

import com.business.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

@PageTitle("Create A New Shopping List")
@Route(value = "new-shopping-list", layout = MainLayout.class)
@AnonymousAllowed
public class NewShoppingListView extends Div {

    private List<Product> productList = getProductList();
    private ArrayList<ShoppingListItem> shoppingListItems = new ArrayList<>();
    private Grid<Product> productGrid = new Grid<>(Product.class);
    private Grid<ShoppingListItem> shoppingListGrid = new Grid<>();
    private ListDataProvider<Product> productDataProvider;
    private ListDataProvider<ShoppingListItem> shoppingListDataProvider;
    private Date chosenDate;
    private String ShoppingListNameEntered;

    public NewShoppingListView() {
        // select date of shopping list

        DatePicker orderDate = new DatePicker("Order Date");
        orderDate.setRequired(true);
        orderDate.addValueChangeListener(event -> {
        LocalDate selectedDate = event.getValue();
        Date sqlDate = java.sql.Date.valueOf(selectedDate);
        this.setChosenDate(sqlDate);});

        //add(orderDate);

        // enter the name of the shopping list    
        TextField ShoppingListName = new TextField();
        ShoppingListName.setLabel("ShoppingList Name");
        ShoppingListName.setValue("");
        ShoppingListName.setClearButtonVisible(true);
        ShoppingListName.setPrefixComponent(VaadinIcon.MAP_MARKER.create());
        ShoppingListName.addValueChangeListener(event -> {
            
            setShoppingListName(event.getValue());
            
        });

        HorizontalLayout dateAndShoppingListName = new HorizontalLayout(orderDate,ShoppingListName);
        dateAndShoppingListName.setSpacing(true);
        add(dateAndShoppingListName);
        //add(ShoppingListName);
        
        // setup a grid for all the products
        productDataProvider = new ListDataProvider<>(productList);
        productGrid.setDataProvider(productDataProvider);
        productGrid.setColumns("productId", "name", "salePrice", "category", "description");
        // set up a grid for all products being added to the shopping list
        shoppingListDataProvider = new ListDataProvider<>(shoppingListItems);
        shoppingListGrid.setDataProvider(shoppingListDataProvider);
        shoppingListGrid.addColumn(ShoppingListItem::getProductId).setHeader("Product Id");
        shoppingListGrid.addColumn(ShoppingListItem::getProductName).setHeader("Product Name");
        shoppingListGrid.addColumn(ShoppingListItem::getQuantity).setHeader("Quantity");

        TextField quantityField = new TextField("Quantity");

        Button addButton = new Button("Add to Shopping List", event -> {
            Product selectedProduct = productGrid.asSingleSelect().getValue();
            if (selectedProduct != null) {
                try {
                    int quantity = Integer.parseInt(quantityField.getValue());
                    addItemToShoppingList(new ShoppingListItem(selectedProduct, quantity));
                    quantityField.clear();
                } catch (NumberFormatException e) {
                    Notification.show("Please enter a valid quantity");
                }
            } else {
                Notification.show("Please select a product");
            }
        });

        Button saveButton = new Button("Save Shopping List", event -> {
            saveShoppingList();
            UI.getCurrent().navigate("master-detail");

        });

        VerticalLayout layout = new VerticalLayout(productGrid, quantityField, addButton, shoppingListGrid, saveButton);
        add(layout);
    }

    public void setChosenDate(Date date){
        this.chosenDate = date;

    }
    public Date getChosenDate(){
        return this.chosenDate;
    }

    public void setShoppingListName(String name){
        this.ShoppingListNameEntered = name;

    }
    public String getShoppingListName(){
        return this.ShoppingListNameEntered;
    }

    private void addItemToShoppingList(ShoppingListItem item) {
        shoppingListItems.add(item);
        System.out.println("hello");
        // added method that updates stock
        shoppingListDataProvider.refreshAll();
    }

    private void saveShoppingList() {
        ListOfShoppingList shoppingListInstance = ListOfShoppingList.getInstance();
       
        shoppingListInstance.addShoppingList(new ShoppingList(7, 5, getChosenDate() ,5,getShoppingListName(),shoppingListItems,"Pending"));

        // save shopping list
        Notification.show("Shopping List saved successfully");
        shoppingListItems.clear();
        shoppingListDataProvider.refreshAll();
    }

    private List<Product> getProductList() {
        // This method should fetch the actual list of products from the database or any other data source.
        // For the purpose of this example, we will use hardcoded products.
        List<Product> products = new ArrayList<>();
        products.add(new Product(1427816L,"Product 1", new BigDecimal(10), "Category 1", "Description 1"));
        products.add(new Product(127323816L,"Product 2", new BigDecimal(20), "Category 2", "Description 2"));
        products.add(new Product(982178216L,"Product 3", new BigDecimal(30), "Category 3", "Description 3"));
        
        return products;
    }
}   
