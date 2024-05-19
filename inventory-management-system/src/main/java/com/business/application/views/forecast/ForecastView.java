package com.business.application.views.forecast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

import com.business.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.Legend;
import com.vaadin.flow.component.charts.model.LayoutDirection;
import com.vaadin.flow.component.charts.model.HorizontalAlign;
import com.vaadin.flow.component.charts.model.VerticalAlign;
import com.vaadin.flow.component.charts.model.XAxis;
import com.vaadin.flow.component.charts.model.YAxis;
import com.vaadin.flow.component.charts.model.ListSeries;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
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
        board.addRow(createViewStockForecast());
        add(board);
    }

    private Component createViewStockForecast() {
        // Header
        HorizontalLayout header = createHeader("", "");

        // Create buttons
        Button allCategoriesBtn = new Button("All Categories");
        Button beerBtn = new Button("Beer");
        Button wineBtn = new Button("Wine");
        Button spiritsBtn = new Button("Spirits");
        Button premixBtn = new Button("Premix");
        Button miscBtn = new Button("Misc");

        // Add buttons to a HorizontalLayout
        HorizontalLayout buttonLayout = new HorizontalLayout(allCategoriesBtn, beerBtn, wineBtn, spiritsBtn, premixBtn, miscBtn);
        buttonLayout.setAlignItems(FlexComponent.Alignment.START);

        // Create ComboBox for search
        ComboBox<String> searchComboBox = new ComboBox<>();
        searchComboBox.setLabel("Select Items");
        searchComboBox.setPlaceholder("Type to search...");

        // Define the items for each button
        String[] beerItems = {"Beer 1", "Beer 2", "Beer 3"};
        String[] wineItems = {"Wine 1", "Wine 2", "Wine 3"};
        String[] spiritsItems = {"Spirits 1", "Spirits 2", "Spirits 3"};
        String[] premixItems = {"Premix 1", "Premix 2", "Premix 3"};
        String[] miscItems = {"Misc 1", "Misc 2", "Misc 3"};

        String[] allCategoriesItems = Stream.of(beerItems, wineItems, spiritsItems, premixItems, miscItems).flatMap(Stream::of).toArray(String[]::new);

        // Set initial items for the search ComboBox
        searchComboBox.setItems(allCategoriesItems);

        // MultiSelectListBox for multi-selector
        MultiSelectListBox<String> multiSelectListBox = new MultiSelectListBox<>();
        multiSelectListBox.setItems(allCategoriesItems);
        multiSelectListBox.setHeight("150px");

        // Add click listeners to each button to update the items in the search ComboBox and MultiSelectListBox
        allCategoriesBtn.addClickListener(event -> {
            searchComboBox.setItems(allCategoriesItems);
            multiSelectListBox.setItems(allCategoriesItems);
        });
        beerBtn.addClickListener(event -> {
            searchComboBox.setItems(beerItems);
            multiSelectListBox.setItems(beerItems);
        });
        wineBtn.addClickListener(event -> {
            searchComboBox.setItems(wineItems);
            multiSelectListBox.setItems(wineItems);
        });
        spiritsBtn.addClickListener(event -> {
            searchComboBox.setItems(spiritsItems);
            multiSelectListBox.setItems(spiritsItems);
        });
        premixBtn.addClickListener(event -> {
            searchComboBox.setItems(premixItems);
            multiSelectListBox.setItems(premixItems);
        });
        miscBtn.addClickListener(event -> {
            searchComboBox.setItems(miscItems);
            multiSelectListBox.setItems(miscItems);
        });

        // Layout for ComboBox and MultiSelectListBox
        VerticalLayout selectLayout = new VerticalLayout(searchComboBox, multiSelectListBox);
        selectLayout.setPadding(false);
        selectLayout.setSpacing(false);
        selectLayout.setAlignItems(FlexComponent.Alignment.START);

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
        yAxis.setTitle("Sales");
        yAxis.setMin(0);
        yAxis.setMax(100);
        conf.addyAxis(yAxis);

        // Initial chart data update
        updateChartData(conf, new ArrayList<>(multiSelectListBox.getSelectedItems()));

        // Change listener for MultiSelectListBox
        multiSelectListBox.addSelectionListener(event -> {
            updateChartData(conf, new ArrayList<>(event.getValue()));
        });

        // Legend Configuration
        Legend legend = new Legend();
        legend.setLayout(LayoutDirection.VERTICAL);
        legend.setAlign(HorizontalAlign.RIGHT);
        legend.setVerticalAlign(VerticalAlign.MIDDLE);
        legend.setItemMarginTop(5);
        legend.setItemMarginBottom(5);
        conf.setLegend(legend);

        // Layout for chart and selector
        HorizontalLayout chartAndSelectLayout = new HorizontalLayout(chart, selectLayout);
        chartAndSelectLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        chartAndSelectLayout.setWidthFull();
        chartAndSelectLayout.setPadding(false);
        chartAndSelectLayout.setSpacing(false);

        // Layout for buttons and chartAndSelectLayout
        VerticalLayout mainContentLayout = new VerticalLayout(buttonLayout, chartAndSelectLayout);
        mainContentLayout.setAlignItems(FlexComponent.Alignment.START);
        mainContentLayout.setSizeFull();
        mainContentLayout.setPadding(false);
        mainContentLayout.setSpacing(false);

        // Main layout combining everything
        VerticalLayout mainLayout = new VerticalLayout(header, mainContentLayout);
        mainLayout.setAlignItems(FlexComponent.Alignment.START);
        mainLayout.setSizeFull();
        mainLayout.setPadding(false);
        mainLayout.setSpacing(false);

        return mainLayout;
    }

    // Method to update chart data based on selected items
    private void updateChartData(Configuration conf, ArrayList<String> selectedItems) {
        conf.setSeries(new ArrayList<>()); // Clear previous series
        for (String item : selectedItems) {
            if ("Beer 1".equals(item)) {
                conf.addSeries(new ListSeries(item, 100, 95, 85, 80, 75, 70, 65, 60, 55, 50, 45));
            } else if ("Beer 2".equals(item)) {
                conf.addSeries(new ListSeries(item, 100, 90, 80, 75, 70, 65, 60, 55, 50, 45, 40));
            } else if ("Beer 3".equals(item)) {
                conf.addSeries(new ListSeries(item, 100, 90, 82, 75, 68, 60, 52, 45, 38, 30, 25));
            } else if ("Wine 1".equals(item)) {
                conf.addSeries(new ListSeries(item, 100, 92, 85, 78, 64, 50, 49, 42, 37, 27, 22));
            } else if ("Wine 2".equals(item)) {
                conf.addSeries(new ListSeries(item, 98, 90, 82, 75, 68, 60, 52, 45, 38, 30, 25));
            } else if ("Wine 3".equals(item)) {
                conf.addSeries(new ListSeries(item, 96, 90, 82, 75, 70, 65, 55, 47, 42, 37, 20));
            } else if ("Spirits 1".equals(item)) {
                conf.addSeries(new ListSeries(item, 100, 92, 82, 78, 68, 63, 52, 45, 40, 30, 20));
            } else if ("Spirits 2".equals(item)) {
                conf.addSeries(new ListSeries(item, 99, 94, 88, 77, 66, 60, 50, 48, 38, 33, 19));
            } else if ("Spirits 3".equals(item)) {
                conf.addSeries(new ListSeries(item, 93, 88, 85, 80, 68, 64, 61, 50, 33, 27, 21));
            } else if ("Premix 1".equals(item)) {
                conf.addSeries(new ListSeries(item, 92, 87, 80, 75, 70, 68, 60, 46, 34, 26, 25));
            } else if ("Premix 2".equals(item)) {
                conf.addSeries(new ListSeries(item, 88, 80, 75, 70, 64, 57, 55, 42, 35, 32, 20));
            } else if ("Premix 3".equals(item)) {
                conf.addSeries(new ListSeries(item, 86, 73, 68, 66, 62, 53, 52, 41, 36, 38, 15));
            } else if ("Misc 1".equals(item)) {
                conf.addSeries(new ListSeries(item, 90, 88, 80, 70, 61, 57, 56, 49, 37, 30, 10));
            } else if ("Misc 2".equals(item)) {
                conf.addSeries(new ListSeries(item, 70, 66, 60, 55, 49, 45, 41, 46, 38, 31, 7));
            } else if ("Misc 3".equals(item)) {
                conf.addSeries(new ListSeries(item, 77, 74, 70, 65, 51, 46, 40, 42, 39, 32, 26));
            }
        }
        // Redraw chart
        conf.getChart();
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
}
