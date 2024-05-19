package com.business.application.views.shoppinglists;

import com.business.application.domain.ListOfShoppingList;
import com.business.application.domain.Product;
import com.business.application.domain.ShoppingList;
import com.business.application.domain.ShoppingListItem;

import com.business.application.views.MainLayout;
import com.vaadin.flow.component.Text;
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
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;


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
    private BigDecimal totalPrice;
    private Text shoppingListPriceText;
    

    public NewShoppingListView() {
        // Create date picker for order date
        totalPrice = new BigDecimal(0.00);
        shoppingListPriceText = new Text("$" + totalPrice.toString());
        add(shoppingListPriceText);



        DatePicker orderDate = new DatePicker("Order Date");
        orderDate.setRequired(true);
        orderDate.addValueChangeListener(event -> {
            LocalDate selectedDate = event.getValue();
            Date sqlDate = java.sql.Date.valueOf(selectedDate);
            this.setChosenDate(sqlDate);
        });

        // Create text field for shopping list name
        TextField ShoppingListName = new TextField();
        ShoppingListName.setLabel("Shopping List Name");
        ShoppingListName.setValue("");
        ShoppingListName.setClearButtonVisible(true);
        ShoppingListName.setPrefixComponent(VaadinIcon.CART.create());
        ShoppingListName.addValueChangeListener(event -> setShoppingListName(event.getValue()));

        HorizontalLayout dateAndShoppingListName = new HorizontalLayout(orderDate, ShoppingListName,shoppingListPriceText);
        dateAndShoppingListName.setSpacing(true);
        dateAndShoppingListName.addClassName("dynamic-style");

        // Create a grid for all the products
        productDataProvider = new ListDataProvider<>(productList);
        productGrid.setDataProvider(productDataProvider);
        //productGrid.setColumns("productId", "name", "salePrice", "category", "description");
        productGrid.removeAllColumns();
        productGrid.addColumn(Product::getName).setHeader("Product Name").setSortable(true);
        productGrid.addColumn(Product::getSalePrice).setHeader("Product Sale Price").setSortable(true);
        productGrid.addColumn(Product::getQuantity).setHeader("Current Stock").setSortable(true);
        // Round sale price to 2 decimal places


        // Create a grid for all products being added to the shopping list
        shoppingListDataProvider = new ListDataProvider<>(shoppingListItems);
        shoppingListGrid.setDataProvider(shoppingListDataProvider);
        shoppingListGrid.addThemeVariants(GridProVariant.LUMO_HIGHLIGHT_EDITABLE_CELLS);
        //shoppingListGrid.addColumn(ShoppingListItem::getProductId).setHeader("Product Id");
        shoppingListGrid.addColumn(ShoppingListItem::getProductName).setHeader("Product Name");
        shoppingListGrid.addColumn(ShoppingListItem::getRequestedQuantityStr).setHeader("Requested Quantity");
        shoppingListGrid.addComponentColumn(item -> {
            Button deleteButton = new Button(new Icon(VaadinIcon.TRASH));
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            deleteButton.addClickListener(click -> {
                shoppingListItems.remove(item);
                // deleting price from total price
                updateTotalPrice(item.getProduct(), -1*item.getRequestedQuantity());
                shoppingListDataProvider.refreshAll();
            });
            return deleteButton;
        }).setHeader("Delete");
        

        TextField quantityField = new TextField("Quantity");

        Button addButton = new Button("Add to Shopping List", event -> {
            Product selectedProduct = productGrid.asSingleSelect().getValue();
            if (selectedProduct != null) {
                try {
                    String quantityValue = quantityField.getValue();
                    int quantity = evaluateExpression(quantityValue);
                    if (quantity <= 0) {
                        Notification.show("Please enter a valid quantity");
                    }else{
                    addItemToShoppingList(new ShoppingListItem(selectedProduct, quantity));}
                    updateTotalPrice(selectedProduct, quantity);
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
        productLayout.addClassName("dynamic-style");

        VerticalLayout shoppingListLayout = new VerticalLayout(new H3("Shopping List Items"), shoppingListGrid);
        shoppingListLayout.addClassName("dynamic-style");

        HorizontalLayout gridsLayout = new HorizontalLayout(productLayout, shoppingListLayout);
        gridsLayout.setSpacing(true);
        gridsLayout.setWidthFull();

        VerticalLayout quantityAndButtonLayout = new VerticalLayout(quantityField, addButton);
        quantityAndButtonLayout.setSpacing(true);
        quantityAndButtonLayout.addClassName("dynamic-style");
        VerticalLayout layout = new VerticalLayout(dateAndShoppingListName, gridsLayout, quantityAndButtonLayout, saveButton);
        layout.setSpacing(true);
        layout.addClassName("dynamic-style");
        
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
    public void setText(String string){
        this.shoppingListPriceText.setText(string);
    }

    public void updateTotalPrice(Product item,int amount){
        BigDecimal orderPriceOfItem = item.getSalePrice().multiply(new BigDecimal(amount));
        // Round to 2 decimal places
        orderPriceOfItem = orderPriceOfItem.setScale(2, BigDecimal.ROUND_HALF_UP);
        this.totalPrice = totalPrice.add(orderPriceOfItem);

        setText("$" + totalPrice.toString());
}

    private void addItemToShoppingList(ShoppingListItem newItem) {
        for (ShoppingListItem item : shoppingListItems) {
            if (item.getProductId().equals(newItem.getProductId())) {
                newItem.setRequestedQuantity(newItem.getRequestedQuantity() + item.getRequestedQuantity());
                shoppingListItems.remove(item);
                break;
            }
        }

        shoppingListItems.add(newItem);

        shoppingListDataProvider.refreshAll();
    }
    

    private void saveShoppingList() {
        ListOfShoppingList shoppingListInstance = ListOfShoppingList.getInstance();
       
        shoppingListInstance.addShoppingList(new ShoppingList( 5, currentList, getChosenDate() ,5,getShoppingListName(),shoppingListItems,"In Progress",this.totalPrice));

        // save shopping list
        Notification.show("Shopping List saved successfully");
        
    }
    public static int evaluateExpression(String expression) {
        // Validate the expression using a regular expression
        if (!expression.matches("[0-9\\+\\-\\*]+")) {
            return 0;
        }

        // Stack to hold numbers and operators, considering higher precedence of '*'
        Stack<Integer> numbers = new Stack<>();
        int length = expression.length();
        int currentNumber = 0;
        char operation = '+';

        for (int i = 0; i < length; i++) {
            char currentChar = expression.charAt(i);

            if (Character.isDigit(currentChar)) {
                currentNumber = currentNumber * 10 + (currentChar - '0');
            }

            if (!Character.isDigit(currentChar) && currentChar != ' ' || i == length - 1) {
                if (operation == '+') {
                    numbers.push(currentNumber);
                } else if (operation == '-') {
                    numbers.push(-currentNumber);
                } else if (operation == '*') {
                    numbers.push(numbers.pop() * currentNumber);
                }

                operation = currentChar;
                currentNumber = 0;
            }
        }

        // Sum up all values in the stack to get the final result
        int result = 0;
        while (!numbers.isEmpty()) {
            result += numbers.pop();
        }

        // Return 0 if the result is not positive
        return Math.max(result, 0);
    }

    private List<Product> getProductList() {
        List<Product> products = new ArrayList<>();
        products.add(new Product(174926328L, "Vodka Cruiser: Wild Raspberry 275mL", new BigDecimal(4.5), "Premix", "600",50));
        products.add(new Product(174036988L, "Suntory: -196 Double Lemon 10 Pack Cans 330mL", new BigDecimal(36), "Wine", "1000",900));
        products.add(new Product(846302592L, "Smirnoff: Ice Double Black Cans 10 Pack 375mL", new BigDecimal(44), "Premix", "5000000",300));
        products.add(new Product(769035037L, "Good Day: Watermelon Soju", new BigDecimal(5.8), "Misc", "5000000",567));
        products.add(new Product(185035836L, "Absolut: Vodka 1L", new BigDecimal(67), "Beer", "1000000",123));
        products.add(new Product(562784657L, "Fireball: Cinnamon Flavoured Whisky 1.14L", new BigDecimal(85), "Spirit", "2000",812));
        products.add(new Product(186538594L, "Brookvale Union: Vodka Lemon Squash Cans 330mL", new BigDecimal(3.6), "Premix", "1000",5000));
        products.add(new Product(879467856L, "Moët & Chandon: Impérial Brut", new BigDecimal(114), "Wine", "2000000",129));
        products.add(new Product(108767894L, "Moët & Chandon: Rosé Impérial", new BigDecimal(156), "Wine", "2000000",36));
        products.add(new Product(265743940L, "Vodka Cruiser: Lush Guava 275mL", new BigDecimal(5.7), "Premix", "5000000",983));
        products.add(new Product(123454352L, "Vodka Cruiser: Juicy Watermelon 275mL", new BigDecimal(5.7), "Misc", "1500",0));
        products.add(new Product(456374567L, "Fireball: Cinnamon Flavoured Whisky 1.14L", new BigDecimal(78), "Spirit", "1000",852));
        products.add(new Product(867584756L, "Smirnoff: Ice Double Black Cans 10 Pack 375mL", new BigDecimal(46), "Premix", "1000",89));
        products.add(new Product(347453482L, "Absolut: Vodka 1L", new BigDecimal(77), "Beer", "2000000",12));
        products.add(new Product(956836417L, "Suntory: -196 Double Lemon Can 330mL", new BigDecimal(4.5), "Wine", "600000",982));
        products.add(new Product(958403584L, "Fireball: Cinnamon Flavoured Whisky 1.14L", new BigDecimal(77), "Spirit", "8000",500));
        products.add(new Product(239563895L, "Good Day: Watermelon Soju", new BigDecimal(6.5), "Spirit", "500000",500));
        products.add(new Product(375845219L, "Smirnoff: Ice Double Black Cans 10 Pack 375mL", new BigDecimal(55), "Misc", "5000000",500));
        products.add(new Product(384926414L, "Vodka Cruiser: Lush Guava 275mL", new BigDecimal(4), "Premix", "3000000",500));
        products.add(new Product(194637894L, "Fireball: Cinnamon Flavoured Whisky 1.14L", new BigDecimal(66), "Beer", "2000000",500));

        return products;
    }
}
