package com.business.application.views.admindashboard;

import com.business.application.views.MainLayout;
import com.business.application.views.admindashboard.ServiceHealth.Status;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.BoxSizing;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.FontWeight;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("Admin Dashboard")
@Route(value = "dashboard2", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class AdminDashboardView extends Main {

    public AdminDashboardView() {
        addClassName("admin-dashboard-view");

        HorizontalLayout storeInfoLayout = createStoreInfoLayout();
        add(storeInfoLayout);

        Board board = new Board();
        board.addRow(createHighlight("Monthly Revenue", "$213,434.40", 11.0), createHighlight("Total Inventory Count", "12,345,340", 0.0));
        board.addRow(createViewSalesQty());
        add(board);
    }

    private HorizontalLayout createStoreInfoLayout() {
        H1 storeName = new H1("CLAYTON");
        storeName.addClassNames(FontWeight.NORMAL, Margin.NONE, TextColor.SECONDARY, FontSize.MEDIUM);

        // Search bar
        TextField searchBar = new TextField();
        searchBar.addClassName("admin-dashboard-view-store-search");
        searchBar.setPlaceholder("Select Store");
        searchBar.setSuffixComponent(new Icon(VaadinIcon.SEARCH));
        searchBar.setWidth("300px");

        // Layout for store info and search bar
        HorizontalLayout layout = createHeader("CLAYTON", "");
        layout.add(searchBar);
        layout.setWidthFull();
        layout.setHeight("50px");
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        layout.addClassName(Padding.LARGE);
        return layout;
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
        layout.addClassName(Padding.LARGE);
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.addClassName("rounded-rectangle");
        return layout;
    }

    private Component createViewSalesQty() {
        // Header
        IntegerField year = new IntegerField();
        year.addClassName("admin-dashboard-view-year-1");
        year.setValue(2024);
        year.setStepButtonsVisible(true);
        year.setMin(1970);
        year.setMax(2024);

        HorizontalLayout header = createHeader("VIEW SALES QTY", "");
        header.add(year);
        header.setAlignItems(FlexComponent.Alignment.CENTER);

        // Chart
        Chart chart = new Chart(ChartType.AREASPLINE);
        Configuration conf = chart.getConfiguration();
        conf.getChart().setStyledMode(true);

        XAxis xAxis = new XAxis();
        xAxis.setCategories("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
        conf.addxAxis(xAxis);

        conf.getyAxis().setTitle("Values");

        PlotOptionsAreaspline plotOptions = new PlotOptionsAreaspline();
        plotOptions.setPointPlacement(PointPlacement.ON);
        plotOptions.setMarker(new Marker(false));
        conf.addPlotOptions(plotOptions);

        conf.addSeries(new ListSeries("Beer", 189, 191, 291, 396, 501, 403, 609, 712, 729, 942, 1044, 1247));
        conf.addSeries(new ListSeries("Wine", 138, 246, 248, 348, 352, 353, 463, 573, 778, 779, 885, 887));
        conf.addSeries(new ListSeries("Spirits", 65, 65, 166, 171, 293, 302, 308, 317, 427, 429, 535, 636));

        // Add it all together
        VerticalLayout viewEvents = new VerticalLayout(header, chart);
        viewEvents.addClassName(Padding.LARGE);
        viewEvents.setPadding(false);
        viewEvents.setSpacing(false);
        viewEvents.getElement().getThemeList().add("spacing-l");
        viewEvents.addClassName("rounded-rectangle");
        return viewEvents;
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