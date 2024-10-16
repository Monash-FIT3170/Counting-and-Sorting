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
import com.vaadin.flow.theme.lumo.LumoIcon;

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
import com.business.application.domain.WebScrapedProduct;
import com.business.application.services.WebScrapedProductService;

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

    private final WebScrapedProductService webScrapedProductService;

    public ShoppingListsView(WebScrapedProductService webScrapedProductService) {
        this.webScrapedProductService = webScrapedProductService;
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
        
        searchField = new TextField();
        searchField.addClassName("toolbar-search-bar");
        searchField.setPlaceholder("Search Lists");
        searchField.setSuffixComponent(LumoIcon.SEARCH.create());
        searchField.setWidth("300px"); 

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

        Span nameLabel = new Span("List " + item.getListId() + " - " + item.getName());
        nameLabel.addClassName("name");

        Span priceLabel = new Span("Order cost: $" + item.getTotalPrice().toString());
        priceLabel.addClassName("price");


        Span dateLabel = new Span(item.getDateString()); 
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
            layout.add(new Span("ID: " + item.getListId()));
            layout.add(new Span("Date: " + item.getDateString()));
            layout.add(new Span("Status: " + item.getStatus()));
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
            List<WebScrapedProduct> products = webScrapedProductService.getAllWebscrapedProducts();

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


    
}