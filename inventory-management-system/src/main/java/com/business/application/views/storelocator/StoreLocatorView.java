package com.business.application.views.storelocator;

import org.apache.commons.lang3.StringUtils;

import com.business.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;

import jakarta.annotation.security.RolesAllowed;
@PageTitle("Store Locator")
@Route(value = "store-locator", layout = MainLayout.class)
@RolesAllowed("USER")

public class StoreLocatorView extends VerticalLayout {


    public StoreLocatorView() {
        // Create the toolbar for search and filter options
        HorizontalLayout toolbar = createToolbar();
        addClassName("store-locator-view");


        // Main layout
        VerticalLayout mainLayout = new VerticalLayout(toolbar);
        mainLayout.setSizeFull();
        mainLayout.setPadding(false);
        mainLayout.setSpacing(false);

        // Add everything to the view
        add(mainLayout);
    }

    private HorizontalLayout createToolbar() {
        // Create the search bar
        TextField searchBar = new TextField();
        searchBar.addClassName("toolbar-search-bar");
        searchBar.setPlaceholder("Search Stores");
        searchBar.setSuffixComponent(VaadinIcon.SEARCH.create());
        searchBar.setWidth("300px");

        // Add filters to search bar
        searchBar.setValueChangeMode(ValueChangeMode.EAGER);

        // Create the layout for the label and search field
        HorizontalLayout toolbar = createHeader("STORE LOCATOR", "");
        toolbar.add(searchBar);
        toolbar.setWidthFull();
        toolbar.setHeight("50px");
        toolbar.setAlignItems(FlexComponent.Alignment.CENTER);
        toolbar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        toolbar.addClassName(Padding.LARGE);
        toolbar.addClassName("search-top-section");
        return toolbar;
    }

     private HorizontalLayout createHeader(String title, String subtitle) {
        H2 h2 = new H2(title);
        h2.addClassName("admin-dashboard-view-h2-1");
        h2.addClassNames(FontSize.XLARGE, Margin.NONE);

        Span span = new Span(subtitle);
        span.addClassNames(TextColor.SECONDARY, FontSize.XSMALL);

        VerticalLayout column = new VerticalLayout(h2, span);
        column.setPadding(false);
        column.setSpacing(false);

        HorizontalLayout header = new HorizontalLayout(column);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setSpacing(false);
        header.setWidthFull();
        return header;
    }
}
