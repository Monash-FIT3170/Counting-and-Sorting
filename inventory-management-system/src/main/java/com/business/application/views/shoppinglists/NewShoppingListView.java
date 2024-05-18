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
import com.vaadin.flow.component.gridpro.GridPro;
import com.vaadin.flow.component.gridpro.GridProVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
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
import java.util.Date;
import java.util.List;

@PageTitle("Create A New Shopping List")
@Route(value = "new-shopping-list", layout = MainLayout.class)
@AnonymousAllowed
public class NewShoppingListView extends Div {

    private List<Product> productList = getProductList();
    private ArrayList<ShoppingListItem> shoppingListItems = new ArrayList<>();
    private Grid<Product> productGrid = new Grid<>(Product.class);
    private GridPro<ShoppingListItem> shoppingListGrid = new GridPro<>();
    private ListDataProvider<Product> productDataProvider;
    private ListDataProvider<ShoppingListItem> shoppingListDataProvider;
    private Date chosenDate;
    private String ShoppingListNameEntered;
    ListOfShoppingList shoppingListInstance = ListOfShoppingList.getInstance();
    int currentList = shoppingListInstance.getShoppingListLength() + 1;

    public NewShoppingListView() {
        // Create date picker for order date
        DatePicker orderDate = new DatePicker("Order Date");
        orderDate.setRequired(true);
        orderDate.addValueChangeListener(event -> {
            LocalDate selectedDate = event.getValue();
            Date sqlDate = java.sql.Date.valueOf(selectedDate);
            this.setChosenDate(sqlDate);
        });

        // Create text field for shopping list name
        TextField ShoppingListName = new TextField();
        ShoppingListName.setLabel("ShoppingList Name");
        ShoppingListName.setValue("");
        ShoppingListName.setClearButtonVisible(true);
        ShoppingListName.setPrefixComponent(VaadinIcon.MAP_MARKER.create());
        ShoppingListName.addValueChangeListener(event -> setShoppingListName(event.getValue()));

        HorizontalLayout dateAndShoppingListName = new HorizontalLayout(orderDate, ShoppingListName);
        dateAndShoppingListName.setSpacing(true);
        dateAndShoppingListName.getStyle().set("padding", "10px").set("border", "1px solid #ccc").set("border-radius", "5px").set("background-color", "#f9f9f9");

        // Create a grid for all the products
        productDataProvider = new ListDataProvider<>(productList);
        productGrid.setDataProvider(productDataProvider);
        productGrid.setColumns("productId", "name", "salePrice", "category", "description");

        // Create a grid for all products being added to the shopping list
        shoppingListDataProvider = new ListDataProvider<>(shoppingListItems);
        shoppingListGrid.setDataProvider(shoppingListDataProvider);
        shoppingListGrid.addThemeVariants(GridProVariant.LUMO_HIGHLIGHT_EDITABLE_CELLS);
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
            if (ShoppingListName.isEmpty()) {
                Notification.show("Enter A ShoppingList Name");
            }

            if (orderDate.isEmpty()) {
                Notification.show("Enter A Date");
            }
            if (shoppingListItems.isEmpty()) {
                Notification.show("No Products Ordered");
            }

            if (!ShoppingListName.isEmpty() && !orderDate.isEmpty() && !shoppingListItems.isEmpty()) {
                saveShoppingList();
                UI.getCurrent().navigate("master-detail");
            }
        });

        VerticalLayout productLayout = new VerticalLayout(new H3("Products"), productGrid);
        productLayout.getStyle().set("padding", "10px").set("border", "1px solid #ccc").set("border-radius", "5px").set("background-color", "#f9f9f9");

        VerticalLayout shoppingListLayout = new VerticalLayout(new H3("Shopping list items"), shoppingListGrid);
        shoppingListLayout.getStyle().set("padding", "10px").set("border", "1px solid #ccc").set("border-radius", "5px").set("background-color", "#f9f9f9");

        HorizontalLayout gridsLayout = new HorizontalLayout(productLayout, shoppingListLayout);
        gridsLayout.setSpacing(true);
        gridsLayout.setWidthFull();

        VerticalLayout quantityAndButtonLayout = new VerticalLayout(quantityField, addButton);
        quantityAndButtonLayout.setSpacing(true);
        quantityAndButtonLayout.getStyle().set("padding", "10px").set("border", "1px solid #ccc").set("border-radius", "5px").set("background-color", "#f9f9f9");

        VerticalLayout layout = new VerticalLayout(dateAndShoppingListName, gridsLayout, quantityAndButtonLayout, saveButton);
        layout.setSpacing(true);
        layout.getStyle().set("padding", "10px").set("border", "1px solid #ccc").set("border-radius", "5px").set("background-color", "#f9f9f9");

        add(layout);
    }

    public void setChosenDate(Date date) {
        this.chosenDate = date;
    }

    public Date getChosenDate() {
        return this.chosenDate;
    }

    public void setShoppingListName(String name) {
        this.ShoppingListNameEntered = name;
    }

    public String getShoppingListName() {
        return this.ShoppingListNameEntered;
    }

    private void addItemToShoppingList(ShoppingListItem item) {
        shoppingListItems.add(item);
        shoppingListDataProvider.refreshAll();
    }

    private void saveShoppingList() {
        ListOfShoppingList shoppingListInstance = ListOfShoppingList.getInstance();
        shoppingListInstance.addShoppingList(new ShoppingList(5, getChosenDate(), 5, getShoppingListName(), shoppingListItems, "Pending"));

        Notification.show("Shopping List saved successfully");
    }

    private List<Product> getProductList() {
        List<Product> products = new ArrayList<>();
        products.add(new Product(1427816L, "Product 1", new BigDecimal(10), "Category 1", "Description 1"));
        products.add(new Product(127323816L, "Product 2", new BigDecimal(20), "Category 2", "Description 2"));
        products.add(new Product(982178216L, "Product 3", new BigDecimal(30), "Category 3", "Description 3"));

        return products;
    }
}
