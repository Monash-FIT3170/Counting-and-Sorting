package com.business.application.views.shoppinglists;

import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.business.application.views.MainLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.annotation.security.RolesAllowed;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.business.application.domain.ListOfShoppingList;
import com.business.application.domain.Product;
import com.business.application.domain.ShoppingList;
import com.business.application.domain.ShoppingListItem;

import java.util.Collections;
import java.util.Date;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@SuppressWarnings("removal")
@PageTitle("Shopping Lists")
@Route(value = "master-detail/:sampleAddressID?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed("USER")
public class ShoppingListsView extends Div {

    private Grid<ShoppingList> grid;
    private final TextField searchField;
    private final Button newButton;
    private final List<ShoppingList> shoppingLists;
    private final FlexLayout container;

    public ShoppingListsView() {
        shoppingLists = new ArrayList<>();
        shoppingLists.addAll(getInitialShoppingListItems());

        container = new FlexLayout();
        container.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        container.setWidthFull();
        container.setJustifyContentMode(FlexLayout.JustifyContentMode.START);
        container.addClassName("container");
        container.getStyle().set("gap", "1em");
        
        addClassName("shopping-lists-view");
        grid = new Grid<>(ShoppingList.class);
        searchField = new TextField("Search", "Search Lists");
        // Add 10 px xpacing on left
        searchField.getStyle().set("margin-left", "10px");

        newButton = new Button("Create New Shopping List");
        newButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        newButton.getStyle().set("margin-top", "auto");

        setupGrid();
        configureSearchField();
        configureNewButton();

        Div content = new Div(grid);
        content.addClassName("content");
        add(getToolbar(), content);

        // Simulated data fetch
        updateDisplayedCards(shoppingLists);

        add(container);
        // Set up polling every 5 seconds
        UI.getCurrent().setPollInterval(1000);
        UI.getCurrent().addPollListener(e -> updateShoppingLists());
    }

    private Div createCard(ShoppingList item) {
        Div card = new Div();
        card.addClassName("card");

        Label nameLabel = new Label("List " + item.getListId() + " - " + item.getName());
        nameLabel.addClassName("name");

        Label priceLabel = new Label("Order cost: $" + item.getTotalPrice().toString());
        priceLabel.addClassName("price");


        Label dateLabel = new Label(item.getDateString()); 
        dateLabel.addClassName("date");

        Span statusLabel = createStatusLabel(item.getStatus());
        statusLabel.addClassName("status");

        card.add(nameLabel,priceLabel ,dateLabel, statusLabel);
        
        Map<String, List<String>> parametersMap = new HashMap<>();
        parametersMap.put("listId", Collections.singletonList(String.valueOf(item.getListId())));
        QueryParameters queryParams = new QueryParameters(parametersMap);
        card.addClickListener(e -> {
            UI.getCurrent().navigate("Shopping List Items", queryParams);
        });

        return card;
    }

    private void updateShoppingLists() {
        List<ShoppingList> currentShoppingLists = getInitialShoppingListItems();
        // If number of Pending shopping lists has decreased, show a notification
        if (currentShoppingLists.size() < shoppingLists.size()) {
            Notification.show("A shopping list has been approved or declined.");
        }
        shoppingLists.clear();
        shoppingLists.addAll(currentShoppingLists);
        grid.setItems(shoppingLists);
        gridSearch(searchField.getValue());
    }

    /**
     * This method creates a Span component for the status with appropriate theming.
     * @param status The status of the item
     * @return A styled Span element representing the status.
     */
    private Span createStatusLabel(String status) {
        Span statusLabel = new Span(status);
        switch (status) {
            case "Pending":
                statusLabel.getElement().getThemeList().add("badge small");
                break;
            case "Approved":
                statusLabel.getElement().getThemeList().add("badge success small");
                break;
            case "Declined":
                statusLabel.getElement().getThemeList().add("badge error small");
                break;
            case "In Progress":
                statusLabel.getElement().getThemeList().add("badge contrast small");
                break;
            default:
                statusLabel.getElement().getThemeList().add("badge small");
                break;
        }
        return statusLabel;
    }

    private void setupGrid() {
        grid.setItems(shoppingLists);

        // Adding a component renderer to the grid
        grid.addColumn(new ComponentRenderer<>(item -> {
            VerticalLayout layout = new VerticalLayout();
            layout.add(new Label("ID: " + item.getListId()));
            layout.add(new Label("Date: " + item.getDateString()));
            layout.add(new Label("Status: " + item.getStatus()));
            return layout;
        })).setHeader("Details");
    }

    private void configureSearchField() {
        searchField.setClearButtonVisible(true);
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> gridSearch(e.getValue()));
    }

    private void configureNewButton() {
        newButton.addClickListener(e -> {
            UI.getCurrent().navigate("new-shopping-list");
        });
    }

    private HorizontalLayout getToolbar() {
        HorizontalLayout toolbar = new HorizontalLayout(searchField, newButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void gridSearch(String searchTerm) {
        List<ShoppingList> filteredLists;
        if (searchTerm == null || searchTerm.isEmpty()) {
            filteredLists = shoppingLists;
        } else {
            filteredLists = shoppingLists.stream()
                    .filter(item -> item.getName().toLowerCase().contains(searchTerm.toLowerCase()))
                    .collect(Collectors.toList());
        }

        grid.setItems(filteredLists);
        updateDisplayedCards(filteredLists);
    }

    private void updateDisplayedCards(List<ShoppingList> lists) {
        container.removeAll();
        for (ShoppingList item : lists) {
            Div card = createCard(item);
            container.add(card);
        }
    }

    private List<ShoppingList> getInitialShoppingListItems() {
        ListOfShoppingList shoppingListInstance = ListOfShoppingList.getInstance();
        if (shoppingListInstance.getShoppingLists().isEmpty()) {
            List<Product> products = getProductList();

            // Create sample shopping list 1 
            List<ShoppingListItem> sampleItems = Arrays.asList(
                new ShoppingListItem(products.get(0), 5),
                new ShoppingListItem(products.get(1), 10),
                new ShoppingListItem(products.get(2), 15)
            );

            // Create and add sample shopping list
            ShoppingList sampleShoppingList = new ShoppingList(
                1,
                111,
                Date.from(new Date().toInstant()),
                123,
                "Sample Shopping List",
                new ArrayList<>(sampleItems),
                "Approved",
                new BigDecimal(5000)
            );
            shoppingListInstance.addShoppingList(sampleShoppingList);

            // Create sample shopping list 2
            List<ShoppingListItem> sampleItems2 = Arrays.asList(
                new ShoppingListItem(products.get(3), 5),
                new ShoppingListItem(products.get(4), 10),
                new ShoppingListItem(products.get(5), 12),
                new ShoppingListItem(products.get(6), 15)
            );
            
            ShoppingList sampleShoppingList2 = new ShoppingList(
                2,
                111,
                Date.from(new Date().toInstant()),
                123,
                "Sample Shopping List 2",
                new ArrayList<>(sampleItems2),
                "Pending",
                new BigDecimal(1000)
            );
            shoppingListInstance.addShoppingList(sampleShoppingList2);

        }

        return shoppingListInstance.getShoppingLists();
    }


    private List<Product> getProductList() {
        List<Product> products = new ArrayList<>();
        products.add(new Product(174926328L, "Vodka Cruiser: Wild Raspberry 275mL", new BigDecimal(375), "Premix", "600",500));
        products.add(new Product(174036988L, "Suntory: -196 Double Lemon 10 Pack Cans 330mL", new BigDecimal(802), "Wine", "1000",500));
        products.add(new Product(846302592L, "Smirnoff: Ice Double Black Cans 10 Pack 375mL", new BigDecimal(3079296), "Premix", "5000000",500));
        products.add(new Product(769035037L, "Good Day: Watermelon Soju", new BigDecimal(3514346), "Misc", "5000000",500));
        products.add(new Product(185035836L, "Absolut: Vodka 1L", new BigDecimal(542669), "Beer", "1000000",500));
        products.add(new Product(562784657L, "Fireball: Cinnamon Flavoured Whisky 1.14L", new BigDecimal(458), "Spirit", "2000",500));
        products.add(new Product(186538594L, "Brookvale Union: Vodka Lemon Squash Cans 330mL", new BigDecimal(997), "Premix", "1000",500));
        products.add(new Product(879467856L, "Moët & Chandon: Impérial Brut", new BigDecimal(1700250), "Wine", "2000000",500));
        products.add(new Product(108767894L, "Moët & Chandon: Rosé Impérial", new BigDecimal(1429048), "Wine", "2000000",500));
        products.add(new Product(265743940L, "Vodka Cruiser: Lush Guava 275mL", new BigDecimal(472648), "Premix", "5000000",500));
        products.add(new Product(123454352L, "Vodka Cruiser: Juicy Watermelon 275mL", new BigDecimal(833), "Misc", "1500",500));
        products.add(new Product(456374567L, "Fireball: Cinnamon Flavoured Whisky 1.14L", new BigDecimal(222), "Spirit", "1000",500));
        products.add(new Product(867584756L, "Smirnoff: Ice Double Black Cans 10 Pack 375mL", new BigDecimal(438), "Premix", "1000",500));
        products.add(new Product(347453482L, "Absolut: Vodka 1L", new BigDecimal(1913750), "Beer", "2000000",500));
        products.add(new Product(956836417L, "Suntory: -196 Double Lemon 10 Pack Cans 330mL", new BigDecimal(528950), "Wine", "600000",500));
        products.add(new Product(958403584L, "Fireball: Cinnamon Flavoured Whisky 1.14L", new BigDecimal(3750), "Spirit", "8000",500));
        products.add(new Product(239563895L, "Good Day: Watermelon Soju", new BigDecimal(290600), "Spirit", "500000",500));
        products.add(new Product(375845219L, "Smirnoff: Ice Double Black Cans 10 Pack 375mL", new BigDecimal(4933400), "Misc", "5000000",500));
        products.add(new Product(384926414L, "Vodka Cruiser: Lush Guava 275mL", new BigDecimal(2266200), "Premix", "3000000",500));
        products.add(new Product(194637894L, "Fireball: Cinnamon Flavoured Whisky 1.14L", new BigDecimal(1563450), "Beer", "2000000",500));

        return products;
    }
}