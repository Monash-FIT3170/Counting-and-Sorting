package com.business.application.views.finance;

import com.business.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.ListSeries;
import com.vaadin.flow.component.charts.model.Marker;
import com.vaadin.flow.component.charts.model.PlotOptionsSpline;
import com.vaadin.flow.component.charts.model.PointPlacement;
import com.vaadin.flow.component.charts.model.XAxis;
import com.vaadin.flow.component.charts.model.YAxis;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.BoxSizing;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.FontWeight;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;

import jakarta.annotation.security.RolesAllowed;
import java.util.Arrays;
import java.util.List;

@PageTitle("Admin Finance")
@Route(value = "admin-finance-view", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class AdminFinanceView extends Div {

    private final VerticalLayout mainLayout;
    private final HorizontalLayout highlightsLayout;
    private final ComboBox<Store> storeSelect;
    private final VerticalLayout contentContainer;
    private final ComboBox<String> selectionComboBox;

    public AdminFinanceView() {
        addClassName("admin-finance-view");

        mainLayout = new VerticalLayout();
        mainLayout.setPadding(true);

        highlightsLayout = new HorizontalLayout();
        highlightsLayout.setWidthFull();

        storeSelect = new ComboBox<>();
        storeSelect.setLabel("Select Store");
        storeSelect.setPlaceholder("Select Store");
        storeSelect.setItemLabelGenerator(Store::getName);
        storeSelect.addValueChangeListener(event -> updateHighlightsAndGraph(event.getValue()));

        HorizontalLayout storeSelectorLayout = new HorizontalLayout();
        storeSelectorLayout.setWidthFull();
        storeSelectorLayout.add(storeSelect);

        mainLayout.add(storeSelectorLayout, highlightsLayout);

        VerticalLayout analysisLayout = new VerticalLayout();
        selectionComboBox = new ComboBox<>("Select View");
        selectionComboBox.setItems("Graph", "Table");

        contentContainer = new VerticalLayout();

        selectionComboBox.addValueChangeListener(event -> {
            contentContainer.removeAll();
            if ("Graph".equals(event.getValue())) {
                contentContainer.add(createFinancialGraph(storeSelect.getValue()));
            } else if ("Table".equals(event.getValue())) {
                contentContainer.add(createList(storeSelect.getValue()));
            }
        });

        analysisLayout.add(selectionComboBox, contentContainer);
        add(mainLayout, analysisLayout);

        initializeStores();
    }

    private void initializeStores() {
        Store store1 = new Store("Store 1",
                10000, 50000, 40000, 10000,
                Arrays.asList(109, 141, 91, 126),
                Arrays.asList(138, 190, 109, 150),
                Arrays.asList(65, 61, 66, 71));

        Store store2 = new Store("Store 2",
                20000, 60000, 40000, 20000,
                Arrays.asList(85, 130, 70, 100),
                Arrays.asList(110, 170, 90, 140),
                Arrays.asList(55, 50, 55, 60));

        Store store3 = new Store("Store 3",
                30000, 70000, 40000, 30000,
                Arrays.asList(120, 150, 100, 130),
                Arrays.asList(150, 200, 120, 160),
                Arrays.asList(70, 65, 70, 75));

        storeSelect.setItems(store1, store2, store3);
        storeSelect.setValue(store1); // Set Store 1 as the default selected store
    }

    private void updateHighlightsAndGraph(Store store) {
        if (store == null) return;

        highlightsLayout.removeAll();
        highlightsLayout.add(createHighlight("Cash on Hand", "$" + store.getCashOnHand(), 0.0),
                             createHighlight("Projected Profit", "$" + store.getProfit(), calculatePercentage(store.getProfit(), store.getRevenue())));

        // Update the graph when the store is changed
        contentContainer.removeAll();
        contentContainer.add(createFinancialGraph(store));
    }

    private double calculatePercentage(int profit, int revenue) {
        if (revenue == 0) return 0.0;
        return (double) profit / revenue * 100;
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
        layout.addClassName("rounded-rectangle");
        layout.addClassName(Padding.LARGE);
        layout.setPadding(false);
        layout.setSpacing(false);
        return layout;
    }

    // Method to create a dynamic financial graph based on store data
    private Component createFinancialGraph(Store store) {
        // Header
        IntegerField year = new IntegerField();
        year.addClassName("user-financial-graph");
        year.setValue(2024);
        year.setStepButtonsVisible(true);
        year.setMin(2020);
        year.setMax(2024);
    
        HorizontalLayout header = createHeader("Yearly Financial Analysis", store.getName());
        header.add(year);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
    
        // Chart
        Chart chart = new Chart(ChartType.SPLINE);
        Configuration conf = chart.getConfiguration();
        conf.getChart().setStyledMode(true);
        chart.setWidth("97%");
    
        XAxis xAxis = new XAxis();
        xAxis.setCategories("Q1", "Q2", "Q3", "Q4");
        conf.addxAxis(xAxis);
    
        YAxis yAxis = new YAxis();
        yAxis.setTitle("$k");
        conf.addyAxis(yAxis);
    
        PlotOptionsSpline plotOptions = new PlotOptionsSpline();
        plotOptions.setPointPlacement(PointPlacement.ON);
        plotOptions.setMarker(new Marker(false));
        conf.addPlotOptions(plotOptions);
    
        // Convert List<Integer> to Integer[] and add to series
        conf.addSeries(new ListSeries("Profit", store.getQuarterlyProfit().toArray(new Integer[0])));
        conf.addSeries(new ListSeries("Income", store.getQuarterlyIncome().toArray(new Integer[0])));
        conf.addSeries(new ListSeries("Expenses", store.getQuarterlyExpenses().toArray(new Integer[0])));
    
        // Add it all together
        VerticalLayout viewEvents = new VerticalLayout(header, chart);
        viewEvents.addClassName("Graph");
        viewEvents.setPadding(false);
        viewEvents.setSpacing(false);
        viewEvents.getElement().getThemeList().add("spacing-l");
        viewEvents.addClassName("rounded-rectangle");
        viewEvents.setWidth("98.5%");
        return viewEvents;
    }

    private Grid<String> createList(Store store) {
        Grid<String> grid = new Grid<>();
        grid.setItems("Item 1", "Item 2", "Item 3"); // Example items, you can customize this to show store-specific data
        grid.addColumn(item -> item).setHeader("Items");

        return grid;
    }

    private HorizontalLayout createHeader(String title, String subtitle) {
        H2 h2 = new H2(title);
        h2.addClassName("financial-view-h2-1");
        h2.addClassNames(FontSize.XLARGE, Margin.NONE);

        Span span = new Span(subtitle);
        span.addClassNames(TextColor.SECONDARY, FontSize.XSMALL);

        VerticalLayout column = new VerticalLayout(h2, span);
        column.setPadding(true);
        column.setSpacing(false);

        HorizontalLayout header = new HorizontalLayout(column);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setSpacing(false);
        header.setWidth("97%");
        return header;
    }

    // Store class with financial data and methods
    public static class Store {
        private final String name;
        private final Integer profit;
        private final Integer revenue;
        private final Integer expenses;
        private final Integer cashOnHand;
        private final List<Integer> quarterlyProfit;
        private final List<Integer> quarterlyIncome;
        private final List<Integer> quarterlyExpenses;

        public Store(String name, Integer profit, Integer revenue, Integer expenses, Integer cashOnHand,
                     List<Integer> quarterlyProfit, List<Integer> quarterlyIncome, List<Integer> quarterlyExpenses) {
            this.name = name;
            this.profit = profit;
            this.revenue = revenue;
            this.expenses = expenses;
            this.cashOnHand = cashOnHand;
            this.quarterlyProfit = quarterlyProfit;
            this.quarterlyIncome = quarterlyIncome;
            this.quarterlyExpenses = quarterlyExpenses;
        }

        public String getName() {
            return name;
        }

        public Integer getProfit() {
            return profit;
        }

        public Integer getRevenue() {
            return revenue;
        }

        public Integer getExpenses() {
            return expenses;
        }

        public Integer getCashOnHand() {
            return cashOnHand;
        }

        public List<Integer> getQuarterlyProfit() {
            return quarterlyProfit;
        }

        public List<Integer> getQuarterlyIncome() {
            return quarterlyIncome;
        }

        public List<Integer> getQuarterlyExpenses() {
            return quarterlyExpenses;
        }
    }
}
