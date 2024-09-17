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
import com.vaadin.flow.component.charts.model.Series;
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
import java.util.HashMap;
import java.util.Map;
import com.vaadin.flow.component.textfield.TextField; 
import com.vaadin.flow.data.value.ValueChangeMode; 
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
    private Button allCategoriesBtn;
    private String selectedCategory = "All";  
    private TextField searchBar;  

    private final WebScrapedProductService webScrapedProductService;
    private Map<String, List<Number>> itemDataMap = new HashMap<>();


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

        allCategoriesBtn.click();
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
            allCategoriesBtn = new Button("All Categories");
            Button beerBtn = new Button("Beer");
            Button wineBtn = new Button("Wine");
            Button spiritsBtn = new Button("Spirits");
            Button premixBtn = new Button("Premix");
            Button miscBtn = new Button("Misc");
        
            // Create ComboBox for search
            searchBar = new TextField();
            searchBar.setPlaceholder("Search items...");
            searchBar.setWidth("300px");
            searchBar.setClearButtonVisible(true);
            searchBar.setValueChangeMode(ValueChangeMode.EAGER); 
            searchBar.addValueChangeListener(event -> updateItemList()); 

             // Create Clear Button
            Button clearBtn = new Button("Clear Graph");
            // Create Clear Button
            clearBtn.getStyle().set("margin-left", "40px");


            // clearBtn.getStyle().set("background-color", "#FF0000"); // Red color
            // clearBtn.getStyle().set("color", "#FFFFFF"); // White text
            // clearBtn.getStyle().set("border", "none");
            // clearBtn.getStyle().set("border-radius", "4px");
            // clearBtn.getStyle().set("padding", "10px");
            clearBtn.addClickListener(event -> {
                multiSelectListBox.deselectAll(); // Deselect all items
                searchBar.clear();
                updateItemList();
                // updateChartData(chart.getConfiguration(), new ArrayList<>()); // Clear the chart
            });

            HorizontalLayout buttonLayout = new HorizontalLayout(allCategoriesBtn, beerBtn, wineBtn, spiritsBtn, premixBtn, miscBtn, searchBar, clearBtn);
            buttonLayout.setAlignItems(FlexComponent.Alignment.CENTER);
            buttonLayout.getStyle().set("justify-content", "center");
            buttonLayout.getStyle().set("margin-bottom", "20px");

            allCategoriesBtn.addClickListener(event -> {
                selectedCategory = "All";
                updateItemList();  // Update list when category is clicked
            });
            
            beerBtn.addClickListener(event -> {
                selectedCategory = "Beer";
                updateItemList();  // Update list when category is clicked
            });
            
            wineBtn.addClickListener(event -> {
                selectedCategory = "Wine";
                updateItemList();  // Update list when category is clicked
            });
    
            spiritsBtn.addClickListener(event -> {
                selectedCategory = "Spirits";
                updateItemList();  // Update list when category is clicked
            });
    
            premixBtn.addClickListener(event -> {
                selectedCategory = "Premix";
                updateItemList();  // Update list when category is clicked
            });
    
            miscBtn.addClickListener(event -> {
                selectedCategory = "Misc";
                updateItemList();  // Update list when category is clicked
            });

            multiSelectListBox = new MultiSelectListBox<>();        
            multiSelectListBox.setHeightFull();
            multiSelectListBox.getStyle().set("overflow-y", "auto"); // Enables vertical scrolling

            
            // Layout for ComboBox and MultiSelectListBox
            VerticalLayout selectLayout = new VerticalLayout(multiSelectListBox);
            selectLayout.setPadding(false);
            selectLayout.setSpacing(false);
            selectLayout.setAlignItems(FlexComponent.Alignment.START);
            selectLayout.setHeight("70vh"); 


        

            // Chart configuration
            chart = new Chart(ChartType.LINE);
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
            conf.addSeries(new ListSeries("No Data", new Number[]{})); // Pass an empty list to display no lines initially

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
            add(mainLayout);
            return mainLayout;
    }

    private void updateItemList() {
        String filterText = searchBar.getValue().toLowerCase();
        List<String> filteredItems = suppliers.stream()
            .filter(product -> (selectedCategory.equals("All") || product.getCategory().equalsIgnoreCase(selectedCategory)) &&
                               product.getName().toLowerCase().contains(filterText))
            .map(WebScrapedProduct::getName)
            .collect(Collectors.toList());

        multiSelectListBox.setItems(filteredItems);
    }

    
    // Method to update chart data based on selected items
    private void updateChartData(Configuration conf, ArrayList<String> selectedItems) {
        Random random = new Random();
        
        // Temporary list for adding new series
        List<Series> updatedSeriesList = new ArrayList<>();
        conf.getLegend().setEnabled(true);

        // Add new series for selected items that are not already displayed
        for (String item : selectedItems) {
            System.out.println("Processing series for: " + item);
            
            List<Number> data;
            if (itemDataMap.containsKey(item)) {
                // Reuse existing data if available
                data = itemDataMap.get(item);
            } else {
                // Generate trend-based data points for the item
                List<Number> trendData = new ArrayList<>();
                int initialValue = random.nextInt(101); // Random starting value
                int trendDirection = random.nextBoolean() ? 1 : -1; // Randomly decide trend direction
                
                for (int i = 0; i < 11; i++) {
                    trendData.add(initialValue);
                    initialValue += trendDirection * random.nextInt(5); // Increment or decrement by a small value
                    // Ensure values stay within a reasonable range
                    if (initialValue < 0) initialValue = 0;
                    if (initialValue > 100) initialValue = 100;
                }
                
                // Save generated data to map
                itemDataMap.put(item, trendData);
                data = trendData;
            }
        
            // Create a new series and add it to the temporary list
            ListSeries newSeries = new ListSeries(item, data.toArray(new Number[0]));
            updatedSeriesList.add(newSeries);
        
            // Track the displayed series
            displayedItems.add(item);
        }
        
        // Remove items from displayedItems that are no longer selected
        displayedItems.removeIf(item -> {
            if (!selectedItems.contains(item)) {
                // Create a new series with no data (effectively removing it)
                ListSeries removedSeries = new ListSeries(item, new Number[0]);
                updatedSeriesList.add(removedSeries);
                return true;
            }
            return false;
        });
        
        // Clear existing series and set updated series list        
        conf.setSeries(updatedSeriesList);

        // Force chart redraw
        chart.drawChart(); // Ensure this method fully clears and redraws the chart
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