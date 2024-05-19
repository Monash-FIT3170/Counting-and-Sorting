package com.business.application.views.forecast;
import java.util.ArrayList;
import java.util.Arrays;
import com.business.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.combobox.ComboBox;
import java.util.stream.Stream;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
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
import com.vaadin.flow.component.textfield.TextFieldVariant;
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
import com.business.application.views.inventory.ProductFrontend;

@PageTitle("Forecast")
@Route(value = "forecast", layout = MainLayout.class)
@RolesAllowed("USER")
public class ForecastView extends Main {
    public ForecastView() {
        addClassName("forecast-view");
        Board board = new Board();
        // board.addRow(createHighlight("Revenue", "$513,434.40", 11.0), createHighlight("Total Inventory Count", "54.6k", -112.45),
        //          createHighlight("Conversion rate", "18%", 3.9));
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
        // HorizontalLayout header = createHeader("Forecasting view", "");
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
        ComboBox<String> searchComboBox = new ComboBox<>();
        // searchComboBox.setItems("Smirnoff Vodka 700ml", "Gordons Gin 700ml", "Jack Daniels 700ml");
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

// Add click listeners to each button to update the items in the search ComboBox
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
        yAxis.setTitle("Percentage of Full Capacity");
        yAxis.setTitle("Sales");
        yAxis.setMin(0);
        yAxis.setMax(100);
        conf.addyAxis(yAxis);

    
        // Adding series data
        updateChartData(conf, "Smirnoff Vodka 700ml");
        searchComboBox.addValueChangeListener(event -> {
            if (event.getValue() != null && !event.getValue().isEmpty()) {
                multiSelectListBox.select(event.getValue());
            }
        });

        // Change listener for MultiSelectListBox
        multiSelectListBox.addSelectionListener(event -> {
            if (!event.getValue().isEmpty()) {
                // Update chart based on selected value (for simplicity, take the first selected item)
                updateChartData(conf, event.getValue().iterator().next());
            }
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
         chartAndSelectLayout.setWidthFull(); // Set width to 100%
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
