package com.business.application.views.shoppinglists;

import com.business.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.Router;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import jakarta.annotation.security.RolesAllowed;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.Anchor;

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
        // Set layout to right justify 

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
