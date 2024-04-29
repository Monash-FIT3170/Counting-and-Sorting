package com.business.application.views.dashboard;


import java.util.Arrays;

import com.business.application.views.MainLayout;
import com.business.application.views.admindashboard.ServiceHealth;
import com.business.application.views.dashboard.ServiceStockItem.Status;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
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

@PageTitle("Dashboard")
@Route(value = "dashboard", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@RolesAllowed("USER")
public class DashboardView extends Main {

    public DashboardView() {
        addClassName("dashboard-view");

        Board board = new Board();
        board.addRow(createHighlight("Revenue", "$213,434.40", 11.0), createHighlight("Total Inventory Count", "54.6k", -112.45),
                createHighlight("Conversion rate", "18%", 3.9), createHighlight("Custom metric", "-123.45", 0.0));
        board.addRow(createViewSales(), createViewStockLevels());
        board.addRow(createLowStockItems(), createResponseTimes());
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

    private Component createViewSales() {
        // Header
        Select year = new Select();
        year.setItems("2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021");
        year.setValue("2021");
        year.setWidth("100px");

        HorizontalLayout header = createHeader("View sales", "City/month");
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

    private Component createViewStockLevels() {
        // Header
        Select storeSelect = new Select();
        storeSelect.setItems("Store 1", "Store 2", "Store 3"); // Add your stores here
        storeSelect.setValue("Store 1");
        storeSelect.setWidth("100px");
    
        HorizontalLayout header = createHeader("Stock Levels By Category", "Store");
        header.add(storeSelect);
    
        // Chart
        Chart chart = new Chart(ChartType.BAR); // Changed to BAR type
        Configuration conf = chart.getConfiguration();
        conf.getChart().setStyledMode(true);
    
        XAxis xAxis = new XAxis();
        xAxis.setCategories("Beer", "Wine", "Spirits", "Premix", "Misc."); // Categories as per your image
        conf.addxAxis(xAxis);
    
        conf.getyAxis().setTitle("Percentage Of Full Capacity");
    
        // Assuming you have the stock level percentages for each category
        conf.addSeries(new ListSeries("Stock Levels", 30, 70, 60, 80, 50)); // Replace with actual values
    
        // Add it all together
        VerticalLayout viewStockLevels = new VerticalLayout(header, chart);
        viewStockLevels.addClassName(Padding.LARGE);
        viewStockLevels.setPadding(false);
        viewStockLevels.setSpacing(false);
        viewStockLevels.getElement().getThemeList().add("spacing-l");
        return viewStockLevels;
    }
    
    private Component createLowStockItems() {
        // Header
        HorizontalLayout header = createHeader("Low Stock Items", "Store");
    
        // Create a grid bound to the StockItem type
        Grid<ServiceStockItem> grid = new Grid<>(ServiceStockItem.class);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setAllRowsVisible(true);
    
        // Customize the 'Status' column to show a colored circle based on status

        grid.addColumn(new ComponentRenderer<>(serviceStockItem -> {
            Span status = new Span();
            //String statusText = getStatusDisplayName(serviceStockItem);
            //status.getElement().setAttribute("aria-label", "Status: " + statusText);
            status.getElement().getThemeList().add(getStatusTheme(serviceStockItem));
            return status;
        })).setHeader("").setFlexGrow(0).setAutoWidth(true);
    
        // Sample data
        ServiceStockItem[] stockItems = new ServiceStockItem[] {
            new ServiceStockItem(ServiceStockItem.Status.EXCELLENT, "-196 Can 10 x 330ml", 12),
            new ServiceStockItem(ServiceStockItem.Status.VERYLOW, "Smirnoff Vodka 700ml", 26),
            new ServiceStockItem(ServiceStockItem.Status.EXCELLENT, "Gordons Gin 700ml", 54),
            new ServiceStockItem(ServiceStockItem.Status.LOW, "Jack Daniels 700ml", 26),
            new ServiceStockItem(ServiceStockItem.Status.VERYLOW, "Bacardi White Rum 700ml", 26),
            new ServiceStockItem(ServiceStockItem.Status.VERYLOW, "Captain Morgan Dark Rum 700ml", 54),
            new ServiceStockItem(ServiceStockItem.Status.LOW, "Bells Whiskey 700ml", 26),
        };
        grid.setItems(Arrays.asList(stockItems));

        // Add it all together
        VerticalLayout layout = new VerticalLayout(header, grid);
        layout.addClassName("service-health");
        layout.setPadding(true);
        layout.setSpacing(true);
        layout.setSizeFull();
        return layout;
    }
    
    private String getStatusDisplayName(ServiceStockItem serviceStockItem) {
        Status status = serviceStockItem.getStatus();
        String statusText = "";
        if (status == Status.EXCELLENT) {
            statusText = "Excellent";
        } else if (status == Status.LOW) {
            statusText = "Low";
        } else if (status == Status.VERYLOW) {
            statusText = "Very low";
        }
        return statusText;
    }

    private Component createResponseTimes() {
        HorizontalLayout header = createHeader("Response times", "Average across all systems");

        // Chart
        Chart chart = new Chart(ChartType.PIE);
        Configuration conf = chart.getConfiguration();
        conf.getChart().setStyledMode(true);
        chart.setThemeName("gradient");

        DataSeries series = new DataSeries();
        series.add(new DataSeriesItem("System 1", 12.5));
        series.add(new DataSeriesItem("System 2", 12.5));
        series.add(new DataSeriesItem("System 3", 12.5));
        series.add(new DataSeriesItem("System 4", 12.5));
        series.add(new DataSeriesItem("System 5", 12.5));
        series.add(new DataSeriesItem("System 6", 12.5));
        conf.addSeries(series);

        // Add it all together
        VerticalLayout serviceHealth = new VerticalLayout(header, chart);
        serviceHealth.addClassName(Padding.LARGE);
        serviceHealth.setPadding(false);
        serviceHealth.setSpacing(false);
        serviceHealth.getElement().getThemeList().add("spacing-l");
        return serviceHealth;
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


    private String getStatusTheme(ServiceStockItem serviceStockItem) {
        Status status = serviceStockItem.getStatus();
        String theme = "badge primary small";
        if (status == Status.EXCELLENT) {
            theme += " success";
        } else if (status == Status.VERYLOW) {
            theme += " error";
        }
        return theme;
    }


}
    