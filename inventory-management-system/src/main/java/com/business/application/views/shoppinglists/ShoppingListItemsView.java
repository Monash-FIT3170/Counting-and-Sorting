package com.business.application.views.shoppinglists;

import com.business.application.domain.ListOfShoppingList;
import com.business.application.domain.ShoppingList;
import com.business.application.domain.ShoppingListItem;
import com.business.application.views.MainLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.annotation.security.RolesAllowed;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@PageTitle("Shopping List Items")
@Route(value = "Shopping List Items", layout = MainLayout.class)
@RolesAllowed("USER")
public class ShoppingListItemsView extends Div implements BeforeEnterObserver {

    private Grid<ShoppingListItem> grid = new Grid<>(ShoppingListItem.class, false);
    private H1 header;
    private ShoppingList shoppingList;
    private H2 totalPrice;

    
    public ShoppingListItemsView() {
        // Back button
        Button backButton = new Button("Back", e -> UI.getCurrent().navigate(ShoppingListsView.class));
        backButton.addClassName("back-button");

        // Header
        header = new H1();
        header.addClassName("header");
        
        totalPrice = new H2();
        totalPrice.addClassName("Header2");

        // Configure Grid
        grid.addColumn(ShoppingListItem::getProductId).setHeader("Item ID").setSortable(true);
        grid.addColumn(ShoppingListItem::getProductName).setHeader("Item Name").setSortable(true);
        grid.addColumn(ShoppingListItem::getProductCategory).setHeader("Category").setSortable(true);
        grid.addColumn(ShoppingListItem::getQuantity).setHeader("Current Qty").setSortable(true);
        grid.addColumn(ShoppingListItem::getRequestedQuantityStr).setHeader("Requested Qty").setSortable(true);

        // Layout setup
        VerticalLayout layout = new VerticalLayout(backButton, header,totalPrice ,grid);
        layout.setSizeFull();
        add(layout);
    }

    private void openConfirmationDialog() {
        Dialog dialog = new Dialog();

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.add(new Span("Are you sure you want to place this order?"));

        // Summary of items
        StringBuilder summary = new StringBuilder();
        for (ShoppingListItem item : shoppingList.getProducts()) {
            summary.append(item.getProductName())
                   .append(" - Qty: ")
                   .append(item.getRequestedQuantityStr())
                   .append("\n");
        }
        Text summaryText = new Text(summary.toString());
        dialogLayout.add(summaryText);

        // Confirm and Cancel buttons
        Button confirmButton = new Button("Confirm", e -> {
            updateShoppingListStatusToPending();
            dialog.close();
            returnToShoppingListsView();
        });
        Button cancelButton = new Button("Cancel", e -> dialog.close());
        HorizontalLayout buttonsLayout = new HorizontalLayout(confirmButton, cancelButton);
        dialogLayout.add(buttonsLayout);

        dialog.add(dialogLayout);
        dialog.open();
    }

    private void updateShoppingListStatusToPending() {
        // Update the shopping list status to pending
        shoppingList.setStatus("Pending");
    }
    

    private void returnToShoppingListsView() {
        // Notify the user that the order has been placed successfully
        Notification.show("Order placed successfully");
        // Redirect to the shopping lists view
        UI.getCurrent().navigate(ShoppingListsView.class);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        QueryParameters queryParams = event.getLocation().getQueryParameters();
        Map<String, List<String>> parametersMap = queryParams.getParameters();
        if (parametersMap.containsKey("listId")) {
            List<String> paramValues = parametersMap.get("listId");
            if (!paramValues.isEmpty()) {
                int listId = Integer.parseInt(paramValues.get(0));
                ListOfShoppingList shoppingListInstance = ListOfShoppingList.getInstance();
                shoppingList = shoppingListInstance.getShoppingList(listId - 1);
                String listName = shoppingList.getName();
                header.setText("Shopping List " + listId + " - " + listName);
                totalPrice.setText("Shopping List Price :$" + shoppingList.getTotalPrice());
                ArrayList<ShoppingListItem> products = shoppingList.getProducts();
                ListDataProvider<ShoppingListItem> shoppingListDataProvider = new ListDataProvider<>(products);
                grid.setDataProvider(shoppingListDataProvider);
                grid.setItems(products);

                // Add order button if the status is "In Progress"
                if ("In Progress".equals(shoppingList.getStatus())) {
                    Button orderButton = new Button("Order", e -> openConfirmationDialog());
                    orderButton.addClassName("order-button");
                    ((VerticalLayout) getContent()).add(orderButton);
                }
            }
        }
    }

    private VerticalLayout getContent() {
        return (VerticalLayout) this.getChildren().filter(component -> component instanceof VerticalLayout).findFirst().orElse(null);
    }
}
