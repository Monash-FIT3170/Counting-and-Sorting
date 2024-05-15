package com.business.application.views.shoppinglists;

import com.business.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;
import java.util.Map;

import com.vaadin.flow.component.html.Span;

@PageTitle("Shopping List")
@Route(value = "Shopping List Items", layout = MainLayout.class)
@RolesAllowed("USER")
public class ShoppingListItemsView extends Div implements BeforeEnterObserver {

    private int listId;

    public ShoppingListItemsView() {

        FlexLayout container = new FlexLayout();
        container.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        container.setWidthFull();
        container.setJustifyContentMode(FlexLayout.JustifyContentMode.START);
        container.addClassName("container");

        // Add back button
        Button backButton = new Button("Back", e -> UI.getCurrent().navigate(ShoppingListsView.class));
        backButton.addClassName("back-button");

        add(backButton);
        add(container);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        QueryParameters queryParams = event.getLocation().getQueryParameters();
        Map<String, List<String>> parametersMap = queryParams.getParameters();
        if (parametersMap.containsKey("param")) {
            List<String> paramValues = parametersMap.get("param");
            if (!paramValues.isEmpty()) {
                int intValue = Integer.parseInt(paramValues.get(0));
                add(new Span("Shopping List " + intValue));
            }
        }
    }

    public int getListId() {
        return listId;
    }

    public void setListId() {
        this.listId = 1;
    }
}
