package com.business.application.views.finance;

import com.business.application.views.MainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;


@PageTitle("User Finance")
@Route(value = "User Finance", layout = MainLayout.class)
@RolesAllowed("USER")

public class UserFinanceView extends Div{

    public UserFinanceView() {
        addClassName("user-finance-view");
        FlexLayout container = new FlexLayout();
        container.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        container.setWidthFull();
        container.setJustifyContentMode(FlexLayout.JustifyContentMode.START);
        container.addClassName("container");
    }
    
}
