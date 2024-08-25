package com.business.application.views.finance;

import com.business.application.domain.Transaction;
import com.business.application.services.TransactionService;
import com.business.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.DataSeries;
import com.vaadin.flow.component.charts.model.DataSeriesItem;
import com.vaadin.flow.component.charts.model.ListSeries;
import com.vaadin.flow.component.charts.model.Marker;
import com.vaadin.flow.component.charts.model.PlotOptionsSpline;
import com.vaadin.flow.component.charts.model.PointPlacement;
import com.vaadin.flow.component.charts.model.XAxis;
import com.vaadin.flow.component.charts.model.YAxis;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility.BoxSizing;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.FontWeight;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@PageTitle("Admin Finance")
@Route(value = "admin-finance-view", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class AdminFinanceView extends Div {

    private final TransactionService transactionService;

    private final VerticalLayout mainLayout;
    private final HorizontalLayout highlightsLayout;
    private final ComboBox<Integer> storeSelect;
    private final VerticalLayout contentContainer;
    private final ComboBox<String> selectionComboBox;

    @Autowired
    public AdminFinanceView(TransactionService transactionService) {
        this.transactionService = transactionService;
        addClassName("admin-finance-view");
    
        mainLayout = new VerticalLayout();
        mainLayout.setPadding(true);
        mainLayout.addClassName("admin-finance-view");
    
        // Overall metrics section
        HorizontalLayout overallMetricsLayout = new HorizontalLayout();
        overallMetricsLayout.setWidthFull();
        overallMetricsLayout.add(
                createHighlight("Total Revenue", formatCurrency(getTotalRevenue()), 3.6),
                createHighlight("Total Store Profits", formatCurrency(getTotalProfit()), -2.4),
                createHighlight("Total Royalty Fees Collected", formatCurrency(getTotalDisbursements()), 3.6)
        );
    
        mainLayout.add(overallMetricsLayout);
    
        highlightsLayout = new HorizontalLayout();
        highlightsLayout.setWidthFull();
    
        // Profit and Revenue Pie Charts for all stores
        HorizontalLayout chartsLayout = new HorizontalLayout();
        chartsLayout.setWidthFull();
    
        Chart profitPieChart = createProfitPieChart();
        Chart revenuePieChart = createRevenuePieChart();
    
        chartsLayout.add(profitPieChart, revenuePieChart);
        mainLayout.add(chartsLayout);
    
        storeSelect = new ComboBox<>();
        storeSelect.setLabel("Select Store");
        storeSelect.setPlaceholder("Select Store");
        storeSelect.addValueChangeListener(event -> updateStoreSpecificData(event.getValue()));
    
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
                contentContainer.add(createCumulativeSumChart(storeSelect.getValue()));
                contentContainer.add(createFinancialGraph(storeSelect.getValue()));
            } else if ("Table".equals(event.getValue())) {
                contentContainer.add(createTransactionsTable(storeSelect.getValue()));
            }
        });
    
        analysisLayout.add(selectionComboBox, contentContainer);
        add(mainLayout, analysisLayout);
    
        initializeStores();
    }
    private Chart createRevenuePieChart() {
        Chart chart = new Chart(ChartType.PIE);
        Configuration conf = chart.getConfiguration();
        conf.getChart().setStyledMode(true);
        conf.setTitle("Revenue Distribution Across Stores");
    
        // Get the revenue data for all stores
        java.util.Map<Integer, BigDecimal> revenues = transactionService.getTotalSalesForAllStores();
    
        // Create a series for the pie chart
        DataSeries series = new DataSeries();
    
        revenues.forEach((storeId, revenue) -> {
            DataSeriesItem item = new DataSeriesItem("Store " + storeId + " (" + formatCurrency(revenue) + ")", revenue.doubleValue());
            series.add(item);
        });
    
        conf.addSeries(series);
        chart.setWidth("97%");
        return chart;
    }

    private void initializeStores() {
        storeSelect.setItems(1, 2, 3, 4);
        storeSelect.setValue(1); // Set Store 1 as the default selected store
    }

    private void updateStoreSpecificData(Integer storeId) {
        if (storeId == null) return;

        BigDecimal cashOnHand = transactionService.getAccountBalanceForStore(storeId);
        BigDecimal profit = transactionService.getProfitForStore(storeId);
        BigDecimal totalSales = transactionService.getTotalSalesForStore(storeId);
        BigDecimal totalExpenses = transactionService.getTotalExpensesForStore(storeId);

        highlightsLayout.removeAll();
        highlightsLayout.add(
                createHighlight("Cash on Hand", formatCurrency(cashOnHand), 0.0),
                createHighlight("Profit", formatCurrency(profit), calculatePercentage(profit, totalSales)),
                createHighlight("Total Sales", formatCurrency(totalSales), 0.0),
                createHighlight("Total Expenses", formatCurrency(totalExpenses), 0.0)
        );

        // Update the graph and transactions table when the store is changed
        contentContainer.removeAll();
        contentContainer.add(createCumulativeSumChart(storeId), createFinancialGraph(storeId), createTransactionsTable(storeId));
    }

    private String formatCurrency(BigDecimal value) {
        return String.format("$%,.2f", value);
    }

    private double calculatePercentage(BigDecimal value, BigDecimal total) {
        if (total.compareTo(BigDecimal.ZERO) == 0) return 0.0;
        return value.divide(total, BigDecimal.ROUND_HALF_EVEN).multiply(BigDecimal.valueOf(100)).doubleValue();
    }

    private BigDecimal getTotalRevenue() {
        return transactionService.getTotalSalesForAllStores().values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal getTotalProfit() {
        return transactionService.getProfitsForAllStores().values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal getTotalDisbursements() {
        return transactionService.getTotalDisbursements();
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
       // Method to create a pie chart showing profit distribution among stores
    private Chart createProfitPieChart() {
        Chart chart = new Chart(ChartType.PIE);
        Configuration conf = chart.getConfiguration();
        conf.getChart().setStyledMode(true);
        conf.setTitle("Profit Distribution Across Stores");

        // Get the profit data for all stores
        java.util.Map<Integer, BigDecimal> profits = transactionService.getProfitsForAllStores();

        // Create a series for the pie chart
        DataSeries series = new DataSeries();

        profits.forEach((storeId, profit) -> {
            DataSeriesItem item = new DataSeriesItem("Store " + storeId + " (" + formatCurrency(profit) + ")", profit.doubleValue());
            series.add(item);
        });

        conf.addSeries(series);
        chart.setWidth("97%");
        return chart;
    }


    private Component createFinancialGraph(Integer storeId) {
        // Get the necessary data for the selected store
        BigDecimal profit = transactionService.getProfitForStore(storeId);
        BigDecimal income = transactionService.getTotalSalesForStore(storeId);
        BigDecimal expenses = transactionService.getTotalExpensesForStore(storeId);
    
        // Header
        IntegerField year = new IntegerField();
        year.addClassName("user-financial-graph");
        year.setValue(2024);
        year.setStepButtonsVisible(true);
        year.setMin(2020);
        year.setMax(2024);
    
        HorizontalLayout header = createHeader("Yearly Financial Analysis", "Store " + storeId);
        header.add(year);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
    
        // Bar Chart
        Chart chart = new Chart(ChartType.COLUMN);
        Configuration conf = chart.getConfiguration();
        conf.getChart().setStyledMode(true);
        conf.setTitle("Yearly Financial Analysis");
        chart.setWidth("97%");
    
        XAxis xAxis = new XAxis();
        xAxis.setCategories("Income", "Expenses", "Profit");
        conf.addxAxis(xAxis);
    
        YAxis yAxis = new YAxis();
        yAxis.setTitle("$k");
        conf.addyAxis(yAxis);
    
        // Adding the series for the bar chart
        DataSeries series = new DataSeries();
    
        // Income
        DataSeriesItem incomeItem = new DataSeriesItem("Income", income.doubleValue());
 // Green for positive
        series.add(incomeItem);
    
        // Expenses
        DataSeriesItem expensesItem = new DataSeriesItem("Expenses", expenses.doubleValue());

        series.add(expensesItem);
    
        // Profit
        DataSeriesItem profitItem = new DataSeriesItem("Profit", profit.doubleValue());
        series.add(profitItem);
    
        conf.addSeries(series);
    
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
    


    private VerticalLayout createTransactionsTable(Integer storeId) {
        Grid<Transaction> grid = new Grid<>(Transaction.class);
        List<Transaction> transactions = transactionService.getTransactionsForStore(storeId);
        grid.setItems(transactions);
        grid.removeAllColumns();

        // Add columns
        grid.addColumn(Transaction::getItem).setHeader("Category").setSortable(true);
        grid.addColumn(Transaction::getType).setHeader("Type").setSortable(true);
        grid.addColumn(Transaction::getAmount).setHeader("Amount").setSortable(true);
        grid.addColumn(Transaction::getDate).setHeader("Date").setSortable(true);
        grid.addColumn(Transaction::getTxId).setHeader("Transaction ID").setSortable(true);


        // Add filtering capability
        TextField categoryFilter = new TextField();
        categoryFilter.setPlaceholder("Filter by Category...");
        categoryFilter.setValueChangeMode(ValueChangeMode.EAGER);
        categoryFilter.addValueChangeListener(e -> {
            String filter = e.getValue().trim();
            grid.setItems(transactions.stream()
                    .filter(tx -> tx.getItem().toLowerCase().contains(filter.toLowerCase()))
                    .collect(Collectors.toList()));
        });

        TextField typeFilter = new TextField();
        typeFilter.setPlaceholder("Filter by Type...");
        typeFilter.setValueChangeMode(ValueChangeMode.EAGER);
        typeFilter.addValueChangeListener(e -> {
            String filter = e.getValue().trim();
            grid.setItems(transactions.stream()
                    .filter(tx -> tx.getType().name().toLowerCase().contains(filter.toLowerCase()))
                    .collect(Collectors.toList()));
        });

        HorizontalLayout filters = new HorizontalLayout(categoryFilter, typeFilter);
        filters.setWidthFull();

        // Add export functionality
        Button exportButton = new Button("Export CSV");
        exportButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Anchor downloadLink = createDownloadLink(grid);
        downloadLink.add(exportButton);

        HorizontalLayout actions = new HorizontalLayout(filters, downloadLink);
        actions.setWidthFull();

        // Add grid to layout
        VerticalLayout layout = new VerticalLayout(actions, grid);
        layout.setWidthFull();

        // Add styling
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);

        return layout;
    }

    private Anchor createDownloadLink(Grid<Transaction> grid) {
        StreamResource resource = new StreamResource("transactions.csv", () -> {
            List<Transaction> transactions = grid.getListDataView().getItems().collect(Collectors.toList());
            StringBuilder csv = new StringBuilder("Category,Type,Amount,Date,Transaction ID\n");
            transactions.forEach(tx -> csv.append(tx.getItem()).append(",")
                    .append(tx.getType()).append(",")
                    .append(tx.getAmount()).append(",")
                    .append(tx.getDate()).append(",")
                    .append(tx.getTxId()).append("\n"));
            return new ByteArrayInputStream(csv.toString().getBytes(StandardCharsets.UTF_8));
        });

        Anchor downloadLink = new Anchor(resource, "");
        downloadLink.getElement().setAttribute("download", true);
        return downloadLink;
    }

    private Component createCumulativeSumChart(Integer storeId) {
        List<Transaction> transactions = transactionService.getTransactionsForStore(storeId);
        transactions.sort(Comparator.comparing(Transaction::getDate));
    
        List<LocalDate> dates = transactions.stream()
                .map(Transaction::getDate)
                .distinct()
                .collect(Collectors.toList());
    
        List<BigDecimal> cumulativeSums = transactions.stream()
                .collect(Collectors.groupingBy(Transaction::getDate, Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)))
                .entrySet().stream()
                .sorted(java.util.Map.Entry.comparingByKey())
                .map(java.util.Map.Entry::getValue)
                .collect(Collectors.toList());
    
        // Accumulate sums
        for (int i = 1; i < cumulativeSums.size(); i++) {
            cumulativeSums.set(i, cumulativeSums.get(i).add(cumulativeSums.get(i - 1)));
        }
    
        // Convert cumulative sums to Number[] for chart compatibility
        Number[] cumulativeSumArray = cumulativeSums.toArray(new Number[0]);
    
        // Chart for cumulative sum
        Chart chart = new Chart(ChartType.SPLINE);
        Configuration conf = chart.getConfiguration();
        conf.getChart().setStyledMode(true);
        conf.setTitle("Cumulative Sum of Transactions Over Time");
        chart.setWidth("97%");
    
        XAxis xAxis = new XAxis();
        xAxis.setCategories(dates.stream().map(LocalDate::toString).toArray(String[]::new));
        conf.addxAxis(xAxis);
    
        YAxis yAxis = new YAxis();
        yAxis.setTitle("Cumulative Sum ($)");
        conf.addyAxis(yAxis);
    
        PlotOptionsSpline plotOptions = new PlotOptionsSpline();
        plotOptions.setPointPlacement(PointPlacement.ON);
        plotOptions.setMarker(new Marker(true));
        conf.addPlotOptions(plotOptions);
    
        conf.addSeries(new ListSeries("Cumulative Sum", cumulativeSumArray));
    
        return chart;
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

    private List<BigDecimal> splitToQuarters(BigDecimal value) {
        BigDecimal quarterValue = value.divide(BigDecimal.valueOf(4), BigDecimal.ROUND_HALF_EVEN);
        return List.of(quarterValue, quarterValue, quarterValue, quarterValue);
    }
}
