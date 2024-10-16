package com.business.application.views.requests;

import com.business.application.domain.Inventory;
import com.business.application.domain.ListOfShoppingList;
import com.business.application.domain.ShoppingList;
import com.business.application.domain.ShoppingListItem;
import com.business.application.domain.Store;
import com.business.application.domain.WebScrapedProduct;
import com.business.application.repository.InventoryRepository;
import com.business.application.services.InventoryService;
import com.business.application.services.StoreService;
import com.business.application.services.WebScrapedProductService;
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
import java.util.Optional;
import java.util.stream.Collectors;

@PageTitle("Requests")
@Route(value = "requests", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class RequestsView extends Div implements AfterNavigationObserver {

    private Grid<ShoppingList> grid;
    private List<ShoppingList> pendingShoppingLists;
    private StoreService storeService;
    private InventoryService inventoryService;
    private WebScrapedProductService webScrapedProductService;

    private InventoryRepository inventoryRepository;


    public RequestsView(InventoryService inventoryService, StoreService storeService, WebScrapedProductService webScrapedProductService, InventoryRepository inventoryRepository) {
        this.inventoryService = inventoryService;
        this.webScrapedProductService = webScrapedProductService;

        addClassName("requests-view");
        setSizeFull();
        pendingShoppingLists = getPendingShoppingLists();
        this.storeService = storeService;

        grid = new Grid<>(ShoppingList.class, false);
        configureGrid();
    
        VerticalLayout layout = new VerticalLayout(grid);
        layout.setSizeFull();
        add(layout);
    
        // Set up polling 
        UI.getCurrent().setPollInterval(1000);
        UI.getCurrent().addPollListener(e -> updateShoppingLists());
    }

    private List<ShoppingList> lastKnownShoppingLists = null;
        private void updateShoppingLists() {
                List<ShoppingList> currentShoppingLists = getPendingShoppingLists();
                if (lastKnownShoppingLists == null) {
                lastKnownShoppingLists = currentShoppingLists;
                return;
                }
                if (currentShoppingLists.size() > lastKnownShoppingLists.size()) {
                Notification.show("New shopping list request received.");
                }
                lastKnownShoppingLists = currentShoppingLists;
                refreshGrid();
        }




    private void configureGrid() {
        grid.addColumn(ShoppingList::getListId).setHeader("List ID").setSortable(true);
        grid.addColumn(ShoppingList::getName).setHeader("List Name").setSortable(true);
        grid.addColumn(ShoppingList::getTotalPrice).setHeader("Cost");
        grid.addColumn(ShoppingList::getDateString).setHeader("Date").setSortable(true);

        grid.addColumn(new ComponentRenderer<>(this::createActionButtons)).setHeader("Actions");

        grid.setItems(pendingShoppingLists);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        grid.setItemDetailsRenderer(new ComponentRenderer<>(this::createDetailsLayout));
        grid.setDetailsVisibleOnClick(false);  // Disable auto opening details on click
        grid.addItemClickListener(event -> {
            grid.setDetailsVisible(event.getItem(), !grid.isDetailsVisible(event.getItem()));
        });
    }

    private VerticalLayout createDetailsLayout(ShoppingList shoppingList) {
        VerticalLayout layout = new VerticalLayout();
        layout.add(new Label("Items:"));
        shoppingList.getProducts().forEach(item -> {
            layout.add(new Label(item.getProductName() + " - Qty: " + item.getRequestedQuantityStr()));
        });
        return layout;
    }

    private HorizontalLayout createActionButtons(ShoppingList shoppingList) {
        Button approveButton = new Button("Approve", VaadinIcon.CHECK.create(), e -> handleApprove(shoppingList));
        Button denyButton = new Button("Decline", VaadinIcon.CLOSE.create(), e -> handleDeny(shoppingList));


        return new HorizontalLayout(approveButton, denyButton);
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
        return ListOfShoppingList.getInstance().getShoppingLists().stream()
            .filter(list -> "Pending".equals(list.getStatus()))
            .collect(Collectors.toList());
    }

    private void updateInventory(ShoppingList shoppingList) {
        int storeId = shoppingList.getStoreId();
        
        //Add products in shopping list to inventory
        Inventory inventory = inventoryService.getOrCreateInventory(storeId);
        List<WebScrapedProduct> products = shoppingList.getProducts().stream()
            .map(item -> webScrapedProductService.getWebscrapedProductById(item.getProductId()))
            .collect(Collectors.toList());

        for (WebScrapedProduct product : products) {
            inventoryService.addProductToInventory(inventory.getInventoryId(), product.getId());
        }        

    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        refreshGrid();
    }
}
