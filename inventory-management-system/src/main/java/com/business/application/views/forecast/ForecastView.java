package com.business.application.views.forecast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.business.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.button.Button;
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
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;
import jakarta.annotation.security.RolesAllowed;
import com.business.application.domain.WebScrapedProduct;
import com.business.application.services.WebScrapedProductService;
import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("Forecast")
@Route(value = "forecast", layout = MainLayout.class)
@RolesAllowed("USER")

public class ForecastView extends Main {

    private Set<String> displayedItems = new HashSet<>();
    private Chart chart;
    private MultiSelectListBox<String> multiSelectListBox;
    private List<WebScrapedProduct> suppliers;

    private final WebScrapedProductService webScrapedProductService;

    @Autowired
    public ForecastView(WebScrapedProductService webScrapedProductService) {
        this.webScrapedProductService = webScrapedProductService;
        this.suppliers = new ArrayList<>();
        addClassName("forecast-view");
        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.addClassName("forecast-view-layout");
        mainLayout.setSizeFull();
        mainLayout.setHeight("100%");
        Board board = new Board();
        board.addRow(createViewStockForecast());
        mainLayout.add(board);
        
        add(mainLayout);

        // Accessing webScrapedProductService in the constructor
        suppliers = webScrapedProductService.getAllWebscrapedProducts();
    }

        private List<String> getProductNamesByCategory(String category) {
        return suppliers.stream()
            .filter(product -> product.getCategory().equalsIgnoreCase(category))
            .map(WebScrapedProduct::getName)
            .collect(Collectors.toList());
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
        
            HorizontalLayout buttonLayout = new HorizontalLayout(allCategoriesBtn, beerBtn, wineBtn, spiritsBtn, premixBtn, miscBtn);
            buttonLayout.setAlignItems(FlexComponent.Alignment.START);
        
            // Create ComboBox for search
            ComboBox<String> searchComboBox = new ComboBox<>();
            searchComboBox.setLabel("Select Items");
            searchComboBox.setPlaceholder("Type to search...");
            searchComboBox.setItems(suppliers.stream().map(WebScrapedProduct::getName).collect(Collectors.toList()));
        
            MultiSelectListBox<String> multiSelectListBox = new MultiSelectListBox<>();
            multiSelectListBox.setItems(suppliers.stream().map(WebScrapedProduct::getName).collect(Collectors.toList()));
        
            // Set fixed height and make the list scrollable
            multiSelectListBox.setHeight("50vh");
            multiSelectListBox.getStyle().set("overflow-y", "auto"); // Enables vertical scrolling
        
            // VerticalLayout selectLayout = new VerticalLayout(searchComboBox, multiSelectListBox);
            // selectLayout.setPadding(false);
            // selectLayout.setSpacing(false);
            // selectLayout.setAlignItems(FlexComponent.Alignment.START);
            // selectLayout.setHeight("100%"); // Or any appropriate height based on your layout


        allCategoriesBtn.addClickListener(event -> {
            List<String> allCategoriesItems = suppliers.stream()
                .map(WebScrapedProduct::getName)
                .collect(Collectors.toList());
            searchComboBox.setItems(allCategoriesItems);
            multiSelectListBox.setItems(allCategoriesItems);
        });
        
        beerBtn.addClickListener(event -> {
            List<String> beerItems = getProductNamesByCategory("Beer");
            searchComboBox.setItems(beerItems);
            multiSelectListBox.setItems(beerItems);
        });
        
        wineBtn.addClickListener(event -> {
            List<String> wineItems = getProductNamesByCategory("Wine");
            searchComboBox.setItems(wineItems);
            multiSelectListBox.setItems(wineItems);
        });

        spiritsBtn.addClickListener(event -> {
            List<String> spiritsItems = getProductNamesByCategory("Spirits");
            searchComboBox.setItems(spiritsItems);
            multiSelectListBox.setItems(spiritsItems);
        });

        premixBtn.addClickListener(event -> {
            List<String> premixItems = getProductNamesByCategory("Premix");
            searchComboBox.setItems(premixItems);
            multiSelectListBox.setItems(premixItems);
        });
        
        miscBtn.addClickListener(event -> {
            List<String> miscItems = getProductNamesByCategory("Misc");
            searchComboBox.setItems(miscItems);
            multiSelectListBox.setItems(miscItems);
        });
        
        System.out.println("HEY");
        // Layout for ComboBox and MultiSelectListBox
        VerticalLayout selectLayout = new VerticalLayout(searchComboBox, multiSelectListBox);
        selectLayout.setPadding(false);
        selectLayout.setSpacing(false);
        selectLayout.setAlignItems(FlexComponent.Alignment.START);
        selectLayout.setHeightFull(); // Make this layout take full height

        // Chart configuration
        Chart chart = new Chart(ChartType.LINE);
        chart.setHeightFull(); // Make the chart take full height
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
        updateChartData(conf, new ArrayList<>()); // Pass an empty list to display no lines initially

        // Change listener for MultiSelectListBox
        multiSelectListBox.addSelectionListener(event -> {
            updateChartData(conf, new ArrayList<>(event.getValue()));
        });

        // Legend Configuration
        Legend legend = new Legend();
        legend.setLayout(LayoutDirection.HORIZONTAL); // Change layout direction to HORIZONTAL
        legend.setAlign(HorizontalAlign.CENTER); // Align to the center horizontally
        legend.setVerticalAlign(VerticalAlign.BOTTOM); // Align to the bottom vertically
        conf.setLegend(legend);

        // Layout for chart and selector
        HorizontalLayout chartAndSelectLayout = new HorizontalLayout(chart, selectLayout);
        chartAndSelectLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        chartAndSelectLayout.setWidthFull();
        chartAndSelectLayout.setHeightFull(); // Make this layout take full height
        chartAndSelectLayout.setPadding(false);
        chartAndSelectLayout.setSpacing(false);

        // Layout for buttons and chartAndSelectLayout
        VerticalLayout mainContentLayout = new VerticalLayout(buttonLayout, chartAndSelectLayout);
        mainContentLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        mainContentLayout.setSizeFull();
        mainContentLayout.setHeightFull(); // Make this layout take full height
        mainContentLayout.setPadding(false);
        mainContentLayout.setSpacing(false);

        // Main layout combining everything
        VerticalLayout mainLayout = new VerticalLayout(header, mainContentLayout);
        mainLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        mainLayout.setSizeFull();
        mainLayout.setHeightFull(); // Make this layout take full height
        mainLayout.setPadding(false);
        mainLayout.setSpacing(false);

         

        // Add mainLayout to the ForecastView component
        System.out.println("Checking mainLayout: " + mainLayout);
        add(mainLayout);

        return mainLayout;
    }

    // Method to update chart data based on selected items
    private void updateChartData(Configuration conf, ArrayList<String> selectedItems) {
        // Add new series
        for (String item : selectedItems) {
            if (!displayedItems.contains(item)) {
                if ("Absolut: Vodka 1L".equals(item)) {
                    conf.addSeries(new ListSeries(item, 100, 95, 85, 80, 75, 70, 65, 60, 55, 50, 45));
                } else if ("Fireball: Cinnamon Flavoured Whisky 1.14L".equals(item)) {
                    conf.addSeries(new ListSeries(item, 90, 85, 80, 75, 70, 65, 60, 55, 50, 45, 40));
                } else if ("Suntory: -196 Double Lemon 10 Pack Cans 330mL".equals(item)) {
                    conf.addSeries(new ListSeries(item, 100, 90, 82, 75, 68, 60, 52, 45, 38, 30, 25));
                } else if ("Moët & Chandon: Impérial Brut".equals(item)) {
                    conf.addSeries(new ListSeries(item, 100, 92, 85, 78, 64, 50, 49, 42, 37, 27, 22));
                } else if ("Moët & Chandon: Rosé Impérial".equals(item)) {
                    conf.addSeries(new ListSeries(item, 98, 90, 82, 75, 68, 60, 52, 45, 38, 30, 25));
                } else if ("Good Day: Watermelon Soju".equals(item)) {
                    conf.addSeries(new ListSeries(item, 96, 90, 82, 75, 70, 65, 55, 47, 42, 37, 20));
                } else if ("Vodka Cruiser: Wild Raspberry 275mL".equals(item)) {
                    conf.addSeries(new ListSeries(item, 100, 92, 82, 78, 68, 63, 52, 45, 40, 30, 20));
                } else if ("Smirnoff: Ice Double Black Cans 10 Pack 375mL".equals(item)) {
                    conf.addSeries(new ListSeries(item, 99, 94, 88, 77, 66, 60, 50, 48, 38, 33, 19));
                } else if ("Brookvale Union: Vodka Lemon Squash Cans 330mL".equals(item)) {
                    conf.addSeries(new ListSeries(item, 93, 88, 85, 80, 68, 64, 61, 50, 33, 27, 21));
                } else if ("Vodka Cruiser: Lush Guava 275mL".equals(item)) {
                    conf.addSeries(new ListSeries(item, 92, 87, 80, 75, 70, 68, 60, 46, 34, 26, 25));
                } else if ("Vodka Cruiser: Juicy Watermelon 275mL".equals(item)) {
                    conf.addSeries(new ListSeries(item, 88, 80, 75, 70, 64, 57, 55, 42, 35, 32, 20));
                }
                displayedItems.add(item);
            }
        }

        // Remove series for items that are no longer selected
        displayedItems.removeIf(item -> {
            if (!selectedItems.contains(item)) {
                conf.getSeries().removeIf(series -> series.getName().equals(item));
                return true;
            }
            return false;
        });

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