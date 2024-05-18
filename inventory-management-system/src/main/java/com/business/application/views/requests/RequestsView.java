package com.business.application.views.requests;

import com.business.application.domain.ListOfShoppingList;
import com.business.application.domain.ShoppingList;
import com.business.application.domain.ShoppingListItem;
import com.business.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import jakarta.annotation.security.RolesAllowed;

import java.util.List;
import java.util.stream.Collectors;

@PageTitle("Requests")
@Route(value = "requests", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class RequestsView extends Div implements AfterNavigationObserver {

    private Grid<ShoppingList> grid;
    private List<ShoppingList> pendingShoppingLists;

    public RequestsView() {
        setSizeFull();
        pendingShoppingLists = getPendingShoppingLists();

        grid = new Grid<>(ShoppingList.class, false);
        configureGrid();

        VerticalLayout layout = new VerticalLayout(grid);
        layout.setSizeFull();
        add(layout);
    }

    private void configureGrid() {
        grid.addColumn(ShoppingList::getListId).setHeader("List ID").setSortable(true);
        grid.addColumn(ShoppingList::getName).setHeader("List Name").setSortable(true);
        grid.addColumn(ShoppingList::getDateString).setHeader("Date").setSortable(true);

        grid.addColumn(new ComponentRenderer<>(this::createItemsLayout)).setHeader("Items");

        grid.addColumn(new ComponentRenderer<>(this::createActionButtons)).setHeader("Actions");

        grid.setItems(pendingShoppingLists);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
    }

    private VerticalLayout createItemsLayout(ShoppingList shoppingList) {
        VerticalLayout itemsLayout = new VerticalLayout();
        for (ShoppingListItem item : shoppingList.getProducts()) {
            Label itemLabel = new Label(item.getProductName() + " - Qty: " + item.getRequestedQuantityStr());
            itemsLayout.add(itemLabel);
        }
        return itemsLayout;
    }

    private HorizontalLayout createActionButtons(ShoppingList shoppingList) {
        Button approveButton = new Button("Approve", VaadinIcon.CHECK.create(), e -> handleApprove(shoppingList));
        Button denyButton = new Button("Deny", VaadinIcon.CLOSE.create(), e -> handleDeny(shoppingList));

        HorizontalLayout actionsLayout = new HorizontalLayout(approveButton, denyButton);
        return actionsLayout;
    }

    private void handleApprove(ShoppingList shoppingList) {
        shoppingList.setStatus("Approved");
        Notification.show("Shopping List " + shoppingList.getListId() + " approved.");
        refreshGrid();
    }

    private void handleDeny(ShoppingList shoppingList) {
        shoppingList.setStatus("Declined");
        Notification.show("Shopping List " + shoppingList.getListId() + " declined.");
        refreshGrid();
    }

    private void refreshGrid() {
        pendingShoppingLists = getPendingShoppingLists();
        grid.setItems(pendingShoppingLists);
    }

    private List<ShoppingList> getPendingShoppingLists() {
        ListOfShoppingList shoppingListInstance = ListOfShoppingList.getInstance();
        return shoppingListInstance.getShoppingLists().stream()
                .filter(list -> "Pending".equals(list.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        refreshGrid();
    }
}
