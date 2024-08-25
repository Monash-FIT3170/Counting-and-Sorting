package com.business.application.views.shoppinglists;

import com.business.application.domain.ListOfShoppingList;
import com.business.application.domain.Product;
import com.business.application.domain.ShoppingList;
import com.business.application.domain.ShoppingListItem;
import com.business.application.domain.WebScrapedProduct;
import com.business.application.services.WebScrapedProductService;
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
import com.vaadin.flow.data.value.ValueChangeMode;
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

import org.springframework.beans.factory.annotation.Autowired;


@PageTitle("Create A New Shopping List")
@Route(value = "new-shopping-list", layout = MainLayout.class)
@AnonymousAllowed
public class NewShoppingListView extends Div {

    private ArrayList<ShoppingListItem> shoppingListItems = new ArrayList<>();
    private Grid<WebScrapedProduct> productGrid = new Grid<>(WebScrapedProduct.class);
    private GridPro<ShoppingListItem> shoppingListGrid = new GridPro<>();
    private ListDataProvider<WebScrapedProduct> productDataProvider;
    private ListDataProvider<ShoppingListItem> shoppingListDataProvider;
    private Date chosenDate;
    private String ShoppingListNameEntered;
    ListOfShoppingList shoppingListInstance = ListOfShoppingList.getInstance();
    int currentList = shoppingListInstance.getShoppingListLength() + 1;
    private BigDecimal totalPrice;
    private Text shoppingListPriceText;
    private TextField searchField;

    private WebScrapedProductService webScrapedProductService;
    
    @Autowired
    public NewShoppingListView(WebScrapedProductService webScrapedProductService) {
        this.webScrapedProductService = webScrapedProductService;
        addClassName("new-shopping-list-view");
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

        searchField = new TextField();
        searchField.setPlaceholder("Search products...");
        searchField.setClearButtonVisible(true);
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(event -> filterProducts(event.getValue()));

        HorizontalLayout dateAndShoppingListName = new HorizontalLayout(orderDate, ShoppingListName,shoppingListPriceText);
        dateAndShoppingListName.setSpacing(true);
        dateAndShoppingListName.addClassName("dynamic-style");

        // Create a grid for all the products
        productDataProvider = new ListDataProvider<>(webScrapedProductService.getAllWebscrapedProducts());
        productGrid.setDataProvider(productDataProvider);
        //productGrid.setColumns("productId", "name", "salePrice", "category", "description");
        productGrid.removeAllColumns();
        productGrid.addColumn(WebScrapedProduct::getName).setHeader("Product Name").setSortable(true);
        productGrid.addColumn(WebScrapedProduct::getPrice).setHeader("Product Sale Price").setSortable(true);
        productGrid.addColumn(WebScrapedProduct::getQuantity).setHeader("Current Stock").setSortable(true);
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
            WebScrapedProduct selectedProduct = productGrid.asSingleSelect().getValue();
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

        VerticalLayout productLayout = new VerticalLayout(new H3("Products"),searchField, productGrid);
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

    private void filterProducts(String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            productDataProvider.clearFilters();
        } else {
            productDataProvider.setFilter(product ->
                    product.getName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                    product.getCategory().toLowerCase().contains(searchTerm.toLowerCase()) ||
                    product.getPrice().toString().contains(searchTerm.toLowerCase())
            );
        }
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

    public void updateTotalPrice(WebScrapedProduct item,int amount){
        BigDecimal orderPriceOfItem = BigDecimal.valueOf(item.getPrice()).multiply(new BigDecimal(amount));
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

}
