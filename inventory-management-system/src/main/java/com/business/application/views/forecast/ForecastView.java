package com.business.application.views.forecast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Random;
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
// import scala.collection.mutable.HashMap;

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
        
            // Create ComboBox for search
            ComboBox<String> searchComboBox = new ComboBox<>();
            searchComboBox.setWidth("200px");
            // searchComboBox.getStyle().set("margin-left", "auto");
            searchComboBox.setPlaceholder("Type to search...");

            HorizontalLayout buttonLayout = new HorizontalLayout(allCategoriesBtn, beerBtn, wineBtn, spiritsBtn, premixBtn, miscBtn, searchComboBox);
            buttonLayout.setAlignItems(FlexComponent.Alignment.CENTER);
            buttonLayout.getStyle().set("justify-content", "center");
            buttonLayout.getStyle().set("margin-bottom", "20px");
        
            MultiSelectListBox<String> multiSelectListBox = new MultiSelectListBox<>();
            
            // Set fixed height and make the list scrollable'
        
            // multiSelectListBox.setHeight("60vh");
            multiSelectListBox.setHeightFull();
            multiSelectListBox.getStyle().set("overflow-y", "auto"); // Enables vertical scrolling
            
                

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
        VerticalLayout selectLayout = new VerticalLayout(multiSelectListBox);
        selectLayout.setPadding(false);
        selectLayout.setSpacing(false);
        selectLayout.setAlignItems(FlexComponent.Alignment.START);
        selectLayout.setHeight("70vh"); 

        // selectLayout.getStyle().set("border", "1px solid #e0e0e0");
       

        // Chart configuration
        Chart chart = new Chart(ChartType.LINE);
        chart.setHeight("70vh"); 
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
        HorizontalLayout chartAndSelectLayout = new HorizontalLayout(selectLayout, chart);
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
        mainContentLayout.setSpacing(true);

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
        Random random = new Random();
        // Add new series
        for (String item : selectedItems) {
            System.out.println("Adding series for: " + item);
            if (!displayedItems.contains(item)) {
                // Generate random data points for the product
                List<Integer> randomData = new ArrayList<>();
                for (int i = 0; i < 11; i++) {
                    randomData.add(random.nextInt(101)); // Random integer between 0 and 100
                }
    
                // Create a new ListSeries and set its data
                ListSeries series = new ListSeries();
                series.setName(item);
                series.setData(randomData.toArray(new Number[0])); // Convert List<Integer> to Number[]
    
                // Add series to the chart configuration
                conf.addSeries(series);
    
                // Add item to displayedItems to keep track of displayed series
                displayedItems.add(item);
            }
        }
       

        // Remove series for items that are no longer selected
        displayedItems.removeIf(item -> {
            System.out.println("Removing series for: " + item);
            if (!selectedItems.contains(item)) {
                conf.getSeries().removeIf(series -> series.getName().equals(item));
                System.out.println("Removing series for: " + item);
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