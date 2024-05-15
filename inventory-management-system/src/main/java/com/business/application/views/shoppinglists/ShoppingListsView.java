package com.business.application.views.shoppinglists;

import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.business.application.views.MainLayout;
import com.vaadin.flow.component.Component;
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
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.business.application.domain.ListOfShoppingList;
import com.business.application.domain.Product;
import com.business.application.domain.ShoppingList;
import java.util.Collections;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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
        // Set layout to right justify 
        
        addClassName("shopping-lists-view");
        grid = new Grid<>(ShoppingList.class);
        searchField = new TextField("Search", "Search Lists");
        newButton = new Button("Create new Shopping List");

        setupGrid();
        configureSearchField();
        configureNewButton();

        Div content = new Div(grid);
        content.addClassName("content");
        add(getToolbar(), content);

        // Simulated data fetch
        for (ShoppingList item : shoppingLists) {
            Div card = createCard(item);
            container.add(card);
        }

        add(container);
    }

    private Div createCard(ShoppingList item) {
        Div card = new Div();
        card.addClassName("card");

        Label nameLabel = new Label(item.getName() + item.getListId());
        nameLabel.addClassName("name");

        Label dateLabel = new Label(item.getDateString() + " · $" + "32,345"); // Assuming a fixed amount for illustration
        dateLabel.addClassName("date");

        Span statusLabel = createStatusLabel(item.getStatus());
        statusLabel.addClassName("status");

        card.add(nameLabel, dateLabel, statusLabel);
        card.getStyle().set("flex", "0 0 calc(30% - 1em)");
        
        Map<String, List<String>> parametersMap = new HashMap<>();
        parametersMap.put("param", Collections.singletonList(String.valueOf(item.getListId())));
        QueryParameters queryParams = new QueryParameters(parametersMap);
        card.addClickListener(e -> {
            UI.getCurrent().navigate("Shopping List Items", queryParams);
        });

        return card;
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
            case "Edited":
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

        /** 
        newButton.addClickListener(e -> {
            Dialog dialog = new Dialog();
            dialog.setHeaderTitle("Create New Shopping List");

            FormLayout formLayout = new FormLayout();

            TextField shoppingListName = new TextField("Shopping List Name");
            shoppingListName.setRequiredIndicatorVisible(true);

            DatePicker orderDate = new DatePicker("Order Date");
            orderDate.setRequired(true);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            TextField todaysDate = new TextField("Today's Date");
            todaysDate.setValue(LocalDate.now().format(formatter));
            todaysDate.setReadOnly(true);

            formLayout.add(shoppingListName, orderDate, todaysDate);

            Button saveButton = new Button("Save", event -> {
                // Handle save logic here
                String name = shoppingListName.getValue();
                LocalDate date = orderDate.getValue();
                if (name != null && !name.isEmpty() && date != null) {
                    ShoppingList newShoppingList = new ShoppingList(
                            shoppingLists.size() + 1,
                            111,  // Replace with actual customer ID
                            123,  // Replace with actual user ID
                            name,
                            new ArrayList<>(),  // Replace with actual products list
                            "Pending"  // Default status
                    );
                    shoppingLists.add(newShoppingList);
                    grid.setItems(shoppingLists);
                    container.add(createCard(newShoppingList));
                    dialog.close();
                }
            });
            saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

            Button cancelButton = new Button("Cancel", event -> dialog.close());
            cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

            HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);
            dialog.add(formLayout, buttonLayout);

            dialog.open();
        });
        */
    }

    private HorizontalLayout getToolbar() {
        HorizontalLayout toolbar = new HorizontalLayout(searchField, newButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void gridSearch(String searchTerm) {
        // Implement search functionality
    }

    private List<ShoppingList> getInitialShoppingListItems() {
        // Hardcoded shopping list items for demonstration
        //ArrayList<Product> products = new ArrayList<Product>();
        ListOfShoppingList shoppingListInstance = ListOfShoppingList.getInstance();
        List<ShoppingList> listofallshoppings = shoppingListInstance.getShoppingLists();
        return listofallshoppings;
        /**return Arrays.asList(
                new ShoppingList(1, 111, 123, "Whiskey Restock", products, "Approved"),
                new ShoppingList(2, 111, 123, "Wine Restock", products, "Edited"),
                new ShoppingList(3, 222, 321, "Beer Restock", products, "Declined"),
                new ShoppingList(4, 222, 321, "Spirits Restock", products,"Pending")
        );*/
    }
}
