package com.business.application.views.shoppinglists;

import com.vaadin.flow.component.orderedlayout.FlexLayout;

import com.business.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import java.util.Arrays;
import java.util.List;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.Anchor;

@SuppressWarnings("removal")
@PageTitle("Shopping Lists")
@Route(value = "master-detail/:sampleAddressID?/:action?(edit)", layout = MainLayout.class)
@AnonymousAllowed
public class ShoppingListsView extends Div {

    private Grid<ShoppingListItem> grid;
    private final TextField searchField;
    private final Button newButton;

    public ShoppingListsView() {
        FlexLayout container = new FlexLayout();
        container.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        container.setWidthFull();
        container.setJustifyContentMode(FlexLayout.JustifyContentMode.START);
        container.addClassName("container");
        // Set layout to right justify 
        
        addClassName("shopping-lists-view");
        grid = new Grid<>(ShoppingListItem.class);
        searchField = new TextField("Search", "Search Lists");
        newButton = new Button("New");

        setupGrid();
        configureSearchField();
        configureNewButton();

        Div content = new Div(grid);
        content.addClassName("content");
        add(getToolbar(), content);

        // Simulated data fetch
        List<ShoppingListItem> items = getShoppingListItems();
        for (ShoppingListItem item : items) {
            Anchor card = createCard(item);
            container.add(card);
        }

        add(container);
    }

    private Anchor createCard(ShoppingListItem item) {
        Div card = new Div();
        card.addClassName("card");

        Label nameLabel = new Label("Shopping List " + item.getId());
        nameLabel.addClassName("name");

        Label dateLabel = new Label(item.getDateCreated() + " · $" + "32,345"); // Assuming a fixed amount for illustration
        dateLabel.addClassName("date");

        Span statusLabel = createStatusLabel(item.getStatus());
        statusLabel.addClassName("status");

        Anchor anchor = new Anchor("https://google.com");
        anchor.addClassName("card_button");

        card.add(nameLabel, dateLabel, statusLabel);
        anchor.add(card);

        return anchor;
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
        grid.setItems(getShoppingListItems());

        // Adding a component renderer to the grid
        grid.addColumn(new ComponentRenderer<>(item -> {
            VerticalLayout layout = new VerticalLayout();
            layout.add(new Label("ID: " + item.getId()));
            layout.add(new Label("Date: " + item.getDateCreated()));
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
            // Handle new shopping list creation
        });
    }

    private HorizontalLayout getToolbar() {
        HorizontalLayout toolbar = new HorizontalLayout(searchField, newButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void gridSearch(String searchTerm) {
        // Implement search functionality
    }
    private List<ShoppingListItem> getShoppingListItems() {
        // Hardcoded shopping list items for demonstration
        return Arrays.asList(
                new ShoppingListItem(1L, "10 May 2024", "Approved"),
                new ShoppingListItem(2L, "10 May 2024", "Edited"),
                new ShoppingListItem(3L, "10 May 2024", "Approved"),
                new ShoppingListItem(4L, "10 May 2024", "Declined"),
                new ShoppingListItem(5L, "10 May 2024", "Declined"),
                new ShoppingListItem(6L, "10 May 2024", "Approved"),
                new ShoppingListItem(7L, "10 May 2024", "Pending")
        );
    }

    // Inner class representing a shopping list item
    public static class ShoppingListItem {
        private Long id;
        private String dateCreated;
        private String status;

        public ShoppingListItem(Long id, String dateCreated, String status) {
            this.id = id;
            this.dateCreated = dateCreated;
            this.status = status;
        }

        public Long getId() {
            return id;
        }

        public String getDateCreated() {
            return dateCreated;
        }

        public String getStatus() {
            return status;
        }
    }
}