package com.business.application.views.forecast;


import java.util.ArrayList;
import java.util.Arrays;

import com.business.application.views.MainLayout;
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

@PageTitle("Forecast")
@Route(value = "forecast", layout = MainLayout.class)
@RolesAllowed("USER")
public class ForecastView extends Main {

    public ForecastView() {
        addClassName("forecast-view");

        Board board = new Board();
       // board.addRow(createHighlight("Revenue", "$513,434.40", 11.0), createHighlight("Total Inventory Count", "54.6k", -112.45),
                // createHighlight("Conversion rate", "18%", 3.9), createHighlight("Custom metric", "-123.45", 0.0));
        board.addRow(createViewStockForecast());
        // board.addRow( createResponseTimes());
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

    private Component createViewStockForecast() {
        // Header
        HorizontalLayout header = createHeader("Forecasting view", "");

        // Selector for Alcohol Types
        Select<String> alcoholTypeSelect = new Select<>();
        alcoholTypeSelect.setItems("Smirnoff Vodka 700ml", "Gordons Gin 700ml", "Jack Daniels 700ml");
        alcoholTypeSelect.setLabel("Select Alcohol Type");
        alcoholTypeSelect.setValue("Smirnoff Vodka 700ml"); // Default selected value
        alcoholTypeSelect.setWidth("200px");

        // Chart configuration
        Chart chart = new Chart(ChartType.LINE);
        Configuration conf = chart.getConfiguration();
        conf.getChart().setStyledMode(true);
        conf.getChart().setType(ChartType.LINE);

        // Setup X and Y Axes
        XAxis xAxis = new XAxis();
        xAxis.setCategories("5 Days Ago", "4 Days Ago", "3 Days Ago", "2 Days Ago", "Yesterday", "Today", "Tomorrow", "In 2 Days", "In 3 Days", "In 4 Days", "In 5 Days");
        conf.addxAxis(xAxis);

        YAxis yAxis = new YAxis();
        yAxis.setTitle("Percentage of Full Capacity");
        yAxis.setMin(0);
        yAxis.setMax(100);
        conf.addyAxis(yAxis);

        // Adding series data
        updateChartData(conf, "Smirnoff Vodka 700ml");

        // Change listener for select
        alcoholTypeSelect.addValueChangeListener(event -> updateChartData(conf, event.getValue()));

        // Legend Configuration
        Legend legend = new Legend();
        legend.setLayout(LayoutDirection.VERTICAL);
        legend.setAlign(HorizontalAlign.RIGHT);
        legend.setVerticalAlign(VerticalAlign.MIDDLE);
        legend.setItemMarginTop(5);
        legend.setItemMarginBottom(5);
        conf.setLegend(legend);

        // Layout for select and chart
        VerticalLayout chartAndSelectLayout = new VerticalLayout(alcoholTypeSelect, chart);
        chartAndSelectLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        chartAndSelectLayout.setSizeFull();
        chartAndSelectLayout.setPadding(false);
        chartAndSelectLayout.setSpacing(false);

        return chartAndSelectLayout;
    }

    // Method to update chart data based on selected alcohol type
    private void updateChartData(Configuration conf, String alcoholType) {
        conf.setSeries(new ArrayList<>()); // Clear previous series
        if ("Smirnoff Vodka 700ml".equals(alcoholType)) {
            conf.addSeries(new ListSeries(alcoholType, 100, 95, 85, 80, 75, 70, 65, 60, 55, 50, 45));
        } else if ("Gordons Gin 700ml".equals(alcoholType)) {
            conf.addSeries(new ListSeries(alcoholType, 100, 90, 80, 75, 70, 65, 60, 55, 50, 45, 40));
        } else if ("Jack Daniels 700ml".equals(alcoholType)) {
            conf.addSeries(new ListSeries(alcoholType, 100, 90, 82, 75, 68, 60, 52, 45, 38, 30, 25));
        }
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
    
        conf.getyAxis().setTitle("Percentage Full Capacity");
    
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
    



    // private Component createResponseTimes() {
    //     HorizontalLayout header = createHeader("Response times", "Average across all systems");

    //     // Chart
    //     Chart chart = new Chart(ChartType.PIE);
    //     Configuration conf = chart.getConfiguration();
    //     conf.getChart().setStyledMode(true);
    //     chart.setThemeName("gradient");

    //     DataSeries series = new DataSeries();
    //     series.add(new DataSeriesItem("System 1", 12.5));
    //     series.add(new DataSeriesItem("System 2", 12.5));
    //     series.add(new DataSeriesItem("System 3", 12.5));
    //     series.add(new DataSeriesItem("System 4", 12.5));
    //     series.add(new DataSeriesItem("System 5", 12.5));
    //     series.add(new DataSeriesItem("System 6", 12.5));
    //     conf.addSeries(series);

    //     // Add it all together
    //     VerticalLayout serviceHealth = new VerticalLayout(header, chart);
    //     serviceHealth.addClassName(Padding.LARGE);
    //     serviceHealth.setPadding(false);
    //     serviceHealth.setSpacing(false);
    //     serviceHealth.getElement().getThemeList().add("spacing-l");
    //     return serviceHealth;
    // }

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

}
    