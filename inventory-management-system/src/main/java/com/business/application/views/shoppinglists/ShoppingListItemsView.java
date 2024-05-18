package com.business.application.views.shoppinglists;

import com.business.application.domain.ListOfShoppingList;
import com.business.application.domain.ShoppingList;
import com.business.application.domain.ShoppingListItem;
import com.business.application.views.MainLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.annotation.security.RolesAllowed;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import com.vaadin.flow.component.html.H1;

@PageTitle("Shopping List Items")
@Route(value = "Shopping List Items", layout = MainLayout.class)
@RolesAllowed("USER")
public class ShoppingListItemsView extends Div implements BeforeEnterObserver {

    private Grid<ShoppingListItem> grid = new Grid<>(ShoppingListItem.class, false);;
    private H1 header;
    //private int listId;

    public ShoppingListItemsView() {

        // Back button
        Button backButton = new Button("Back", e -> UI.getCurrent().navigate(ShoppingListsView.class));
        backButton.addClassName("back-button");

        // Header
        header = new H1();
        header.addClassName("header");

        // Configure Grid
        // grid = new Grid<>(ShoppingListItem.class, false);
        grid.addColumn(ShoppingListItem::getProductId).setHeader("Item ID").setSortable(true);
        grid.addColumn(ShoppingListItem::getProductName).setHeader("Item Name").setSortable(true);
        grid.addColumn(ShoppingListItem::getProductCategory).setHeader("Category").setSortable(true);
        grid.addColumn(ShoppingListItem::getQuantityStr).setHeader("Current Qty").setSortable(true);
        grid.addColumn(ShoppingListItem::getRequestedQuantityStr).setHeader("Requested Qty").setSortable(true);

        // Layout setup
        VerticalLayout layout = new VerticalLayout(backButton, header, grid);
        layout.setSizeFull();
        add(layout);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        QueryParameters queryParams = event.getLocation().getQueryParameters();
        Map<String, List<String>> parametersMap = queryParams.getParameters();
        if (parametersMap.containsKey("listId")) {
            List<String> paramValues = parametersMap.get("listId");
            if (!paramValues.isEmpty()) {
                int listId = Integer.parseInt(paramValues.get(0));
                header.setText("Shopping List " + listId);
                ListOfShoppingList shoppingListInstance = ListOfShoppingList.getInstance();
                ShoppingList shoppingList = shoppingListInstance.getShoppingList(listId - 1);
                // grid.setItems(getShoppingListItems(listId));
                ArrayList<ShoppingListItem> products = shoppingList.getProducts();
                ListDataProvider<ShoppingListItem> shoppingListDataProvider = new ListDataProvider<>(products);
                grid.setDataProvider(shoppingListDataProvider);
                grid.setItems(products);
            }
        }
    }

    // private List<ShoppingListItem> getShoppingListItems(int listId) {
    //     // Dummy data for illustration purposes. Replace this with actual data fetching logic.
    //     return Arrays.asList(
    //         new ShoppingListItem("174926328", "Vodka Cruiser: Wild Raspberry 275mL", "Premix", "3,331,296", "1,296"),
    //         new ShoppingListItem("174036988", "Suntory: -196 Double Lemon 10 Pack Cans 330mL", "Wine", "1,012,997", "2,997"),
    //         new ShoppingListItem("846302592", "Smirnoff: Ice Double Black Cans 10 Pack 375mL", "Premix", "3,079,296", "9,296")
    //         // Add more items as needed
    //     );
    // }

//     public static class ShoppingListItem {
//         private String itemId;
//         private String itemName;
//         private String category;
//         private String currentQty;
//         private String requestedQty;

//         public ShoppingListItem(String itemId, String itemName, String category, String currentQty, String requestedQty) {
//             this.itemId = itemId;
//             this.itemName = itemName;
//             this.category = category;
//             this.currentQty = currentQty;
//             this.requestedQty = requestedQty;
//         }

//         public String getItemId() {
//             return itemId;
//         }

//         public String getItemName() {
//             return itemName;
//         }

//         public String getCategory() {
//             return category;
//         }

//         public String getCurrentQty() {
//             return currentQty;
//         }

//         public String getRequestedQty() {
//             return requestedQty;
//         }
//     }
}

