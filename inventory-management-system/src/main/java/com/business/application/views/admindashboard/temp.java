package com.business.application.views.admindashboard;


import java.util.List;
import java.util.stream.Collectors;

import com.business.application.domain.ListOfShoppingList;
import com.business.application.domain.ShoppingList;
import com.business.application.views.MainLayout;
import com.business.application.views.admindashboard.ServiceHealth.Status;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
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

        Board board = new Board();
        board.addRow(createHighlight("Current users", "745", 33.7), createHighlight("View events", "54.6k", -112.45),
                createHighlight("Conversion rate", "18%", 3.9), createHighlight("Custom metric", "-123.45", 0.0));
        board.addRow(createViewEvents());
        board.addRow(createShoppingListRequests(), createResponseTimes());
        add(board);
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
        return layout;
    }

    private Component createViewEvents() {
        // Header
        Select year = new Select();
        year.setItems("2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021");
        year.setValue("2021");
        year.setWidth("100px");

        HorizontalLayout header = createHeader("View events", "City/month");
        header.add(year);

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

        conf.addSeries(new ListSeries("Berlin", 189, 191, 291, 396, 501, 403, 609, 712, 729, 942, 1044, 1247));
        conf.addSeries(new ListSeries("London", 138, 246, 248, 348, 352, 353, 463, 573, 778, 779, 885, 887));
        conf.addSeries(new ListSeries("New York", 65, 65, 166, 171, 293, 302, 308, 317, 427, 429, 535, 636));
        conf.addSeries(new ListSeries("Tokyo", 0, 11, 17, 123, 130, 142, 248, 349, 452, 454, 458, 462));

        // Add it all together
        VerticalLayout viewEvents = new VerticalLayout(header, chart);
        viewEvents.addClassName(Padding.LARGE);
        viewEvents.setPadding(false);
        viewEvents.setSpacing(false);
        viewEvents.getElement().getThemeList().add("spacing-l");
        return viewEvents;
    }

    private Component createShoppingListRequests() {
        // Header
        HorizontalLayout header = createHeader("Pending Shopping List Requests", "Approve or Deny Requests");

        // Grid
        Grid<ShoppingList> grid = new Grid<>(ShoppingList.class, false);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setAllRowsVisible(true);

        grid.addColumn(ShoppingList::getListId).setHeader("List ID").setSortable(true);
        grid.addColumn(ShoppingList::getName).setHeader("List Name").setSortable(true);
        grid.addColumn(ShoppingList::getDateString).setHeader("Date").setSortable(true);

        List<ShoppingList> pendingShoppingLists = getPendingShoppingLists();
        grid.setItems(pendingShoppingLists);

        // Add click listener to navigate to requests tab
        grid.addItemClickListener(event -> {
            // Navigate to requests tab
            getUI().ifPresent(ui -> ui.navigate("requests"));
        });

        // Add it all together
        VerticalLayout serviceHealth = new VerticalLayout(header, grid);
        serviceHealth.addClassName(Padding.LARGE);
        serviceHealth.setPadding(false);
        serviceHealth.setSpacing(false);
        serviceHealth.getElement().getThemeList().add("spacing-l");
        return serviceHealth;
    }

    private List<ShoppingList> getPendingShoppingLists() {
        return ListOfShoppingList.getInstance().getShoppingLists().stream()
            .filter(list -> "Pending".equals(list.getStatus()))
            .collect(Collectors.toList());
    }


    private Component createResponseTimes() {
        HorizontalLayout header = createHeader("Request Status Distribution", "Number of each type of request");
    
        // Chart
        Chart chart = new Chart(ChartType.PIE);
        Configuration conf = chart.getConfiguration();
        conf.getChart().setStyledMode(true);
        chart.setThemeName("gradient");
    
        DataSeries series = new DataSeries();
    
        int approvedCount = getRequestCountByStatus("Approved");
        int pendingCount = getRequestCountByStatus("Pending");
        int declinedCount = getRequestCountByStatus("Declined");
        int inProgressCount = getRequestCountByStatus("In Progress");
    
        series.add(new DataSeriesItem("Approved", approvedCount));
        series.add(new DataSeriesItem("Pending", pendingCount));
        series.add(new DataSeriesItem("Declined", declinedCount));
        series.add(new DataSeriesItem("In Progress", inProgressCount));
    
        conf.addSeries(series);
    
        // Add it all together
        VerticalLayout responseTimes = new VerticalLayout(header, chart);
        responseTimes.addClassName(Padding.LARGE);
        responseTimes.setPadding(false);
        responseTimes.setSpacing(false);
        responseTimes.getElement().getThemeList().add("spacing-l");
        return responseTimes;
    }
    
    private int getRequestCountByStatus(String status) {
        ListOfShoppingList shoppingListInstance = ListOfShoppingList.getInstance();
        return (int) shoppingListInstance.getShoppingLists().stream()
                .filter(list -> status.equals(list.getStatus()))
                .count();
    }
    

    private HorizontalLayout createHeader(String title, String subtitle) {
        H2 h2 = new H2(title);
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

    private String getStatusDisplayName(ServiceHealth serviceHealth) {
        Status status = serviceHealth.getStatus();
        if (status == Status.OK) {
            return "Ok";
        } else if (status == Status.FAILING) {
            return "Failing";
        } else if (status == Status.EXCELLENT) {
            return "Excellent";
        } else {
            return status.toString();
        }
    }

    private String getStatusTheme(ServiceHealth serviceHealth) {
        Status status = serviceHealth.getStatus();
        String theme = "badge primary small";
        if (status == Status.EXCELLENT) {
            theme += " success";
        } else if (status == Status.FAILING) {
            theme += " error";
        }
        return theme;
    }

}
