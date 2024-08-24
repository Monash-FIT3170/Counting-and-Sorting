package com.business.application.views.finance;

import com.business.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.BoxSizing;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.FontWeight;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;
import com.vaadin.flow.component.select.Select;
import jakarta.annotation.security.RolesAllowed;


@PageTitle("Admin Finance")
@Route(value = "admin-finance-view", layout = MainLayout.class)
@RolesAllowed("ADMIN")

public class AdminFinanceView extends Div{

    public AdminFinanceView() {
        addClassName("admin-finance-view");

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setPadding(true);
        
        HorizontalLayout storeSelector = new HorizontalLayout();
        storeSelector.setWidthFull();
        storeSelector.add(createStoreSelector(new String[]{"Store 1", "Store 2", "Store 3"}));
                
        HorizontalLayout highlightsLayout = new HorizontalLayout();
        highlightsLayout.setWidthFull();
        highlightsLayout.add(createHighlight("Cash on Hand", "$157,434.40", 0.0),
                createHighlight("Projected Profit", "$143,777", 5.0));

        mainLayout.add(highlightsLayout);
        add(mainLayout);

    }

    private Component createHighlight(String title, String value, Double percentage) {
        VaadinIcon icon = VaadinIcon.ARROW_UP;
        String prefix = "";
        String theme = "badge";

        if (percentage == 0) {
            prefix = "±";
        } else if (percentage > 0) {
            prefix = "+";
            theme += " success";
        } else if (percentage < 0) {
            icon = VaadinIcon.ARROW_DOWN;
            theme += " error";
        }

        H2 h2 = new H2(title);
        h2.addClassNames(FontWeight.NORMAL, Margin.NONE, TextColor.SECONDARY, FontSize.XSMALL);

        Span span = new Span(value);
        span.addClassNames(FontWeight.SEMIBOLD, FontSize.XXXLARGE);

        Icon i = icon.create();
        i.addClassNames(BoxSizing.BORDER, Padding.XSMALL);

        Span badge = new Span(i, new Span(prefix + percentage.toString()));
        badge.getElement().getThemeList().add(theme);

        VerticalLayout layout = new VerticalLayout(h2, span, badge);
        layout.addClassName("rounded-rectangle");
        layout.addClassName(Padding.LARGE);
        layout.setPadding(false);
        layout.setSpacing(false);
        return layout;
    }

    private Component createStoreSelector(String[] stores) {
        FlexLayout layout = new FlexLayout();
        Select<String> select = new Select<>();
        select.setItems(stores);
        select.setLabel("Select Store");
        select.setPlaceholder("Select Store");
        

        add(select);

        return layout;
    }

    private class Store{
        private String name;
        private Integer profit;
        private Integer revenue;
        private Integer expenses;
        private Integer cashOnHand;

        public Store(String name, Integer profit, Integer revenue, Integer expenses, Integer cashOnHand) {
            this.name = name;
            this.profit = profit;
            this.revenue = revenue;
            this.expenses = expenses;
            this.cashOnHand = cashOnHand;
        }

        public String getName() {
            return name;
        }

        public Integer getProfit() {
            return profit;
        }

        public Integer getRevenue() {
            return revenue;
        }

        public Integer getExpenses() {
            return expenses;
        }
    }
    
}
