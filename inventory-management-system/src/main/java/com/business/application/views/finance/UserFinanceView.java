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
import com.vaadin.flow.theme.lumo.LumoUtility.*;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@PageTitle("Store Finance")
@Route(value = "store-finance", layout = MainLayout.class)
@RolesAllowed("USER")
public class UserFinanceView extends Div {

    private final TransactionService transactionService;
    private ComboBox<String> selectionComboBox;
    

    @Autowired
    public UserFinanceView(TransactionService transactionService) {
        this.transactionService = transactionService;
        addClassName("manager-finance-view");


        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setPadding(true);

        // Assuming storeId is passed or selected, here we're using storeId = 1 as an example
        int storeId = 1;

        // Get real data for the store
        BigDecimal accountBalance = transactionService.getAccountBalanceForStore(storeId);
        BigDecimal totalSales = transactionService.getTotalSalesForStore(storeId);
        BigDecimal totalExpenses = transactionService.getTotalExpensesForStore(storeId);
        BigDecimal profit = transactionService.getProfitForStore(storeId);
        Map<String, BigDecimal> costBreakdown = transactionService.getCostBreakdownForStore(storeId);
        Map<String, BigDecimal> revenueBreakdown = transactionService.getRevenueBreakdownForStore(storeId);

        // Highlights section
        HorizontalLayout highlightsLayout = new HorizontalLayout();
        highlightsLayout.setWidthFull();
        highlightsLayout.add(
                createHighlight("Account Balance", formatCurrency(accountBalance), 56.3),
                createHighlight("Profit", formatCurrency(profit), 3.9),
                createHighlight("Total Sales", formatCurrency(totalSales), 8.3),
                createHighlight("Total Expenses", formatCurrency(totalExpenses), 0.0)
        );

        // Cost and Revenue breakdown section side by side
        HorizontalLayout breakdownLayout = new HorizontalLayout();
        breakdownLayout.setWidthFull();
        breakdownLayout.add(
                createPieChartWithDetails("Cost Breakdown", costBreakdown, true),
                createPieChartWithDetails("Revenue Breakdown", revenueBreakdown, false)
        );

        // Create a separate layout for the ComboBox to keep it persistent
        HorizontalLayout toggleLayout = new HorizontalLayout();
        toggleLayout.setWidthFull();
        
        // ComboBox for selecting view
        selectionComboBox = new ComboBox<>("Select View");
        selectionComboBox.setItems("Graph View", "Table View");
        selectionComboBox.setValue("Graph View"); // Set default value
        toggleLayout.add(selectionComboBox);

        // Create layout for dynamic content (Graph/Table)
        VerticalLayout analysisLayout = new VerticalLayout();

        // Set up ComboBox event listener
        selectionComboBox.addValueChangeListener(e -> switchView(e.getValue(), analysisLayout, storeId));

        // Set the default view
        switchView("Graph View", analysisLayout, storeId);

        // Add all components to main layout
        mainLayout.add(highlightsLayout, breakdownLayout, toggleLayout, analysisLayout);
        add(mainLayout);
    }

    private Component createPieChartWithDetails(String title, Map<String, BigDecimal> breakdown, boolean isCost) {
        // Create the pie chart
        Chart chart = new Chart(ChartType.PIE);
        Configuration conf = chart.getConfiguration();
        conf.getChart().setStyledMode(true);
        conf.setTitle(title);

        DataSeries series = new DataSeries();

        breakdown.forEach((key, value) -> {
            // Costs should be positive in the chart even though they are negative values
            BigDecimal displayValue = isCost ? value.abs() : value;
            DataSeriesItem item = new DataSeriesItem(key, displayValue.doubleValue());
            item.setName(key + " (" + formatCurrency(displayValue) + ")");
            series.add(item);
        });

        conf.addSeries(series);

        // Combine the chart and details in a single layout
        VerticalLayout layout = new VerticalLayout(chart);
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.addClassName("rounded-rectangle");
        layout.setWidth("100%");

        return layout;
    }

    private String formatCurrency(BigDecimal accountBalance) {
        return String.format("$%,.2f", accountBalance);
    }

    private void switchView(String viewType, VerticalLayout analysisLayout, int storeId) {
        analysisLayout.removeAll();

        if ("Graph View".equals(viewType)) {
            analysisLayout.add(createCumulativeSumChart(storeId), createMonthlyRevenueProfitBarChart(storeId));
        } else if ("Table View".equals(viewType)) {
            analysisLayout.add(createTransactionsTable(storeId));
        }
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

        Span badge = new Span(i, new Span(prefix + percentage.toString() + "%"));
        badge.getElement().getThemeList().add(theme);

        VerticalLayout layout = new VerticalLayout(h2, span, badge);
        layout.addClassName("rounded-rectangle");
        layout.addClassName(Padding.LARGE);
        layout.setPadding(false);
        layout.setSpacing(false);
        return layout;
    }

    private Component createCumulativeSumChart(int storeId) {
        List<Transaction> transactions = transactionService.getTransactionsForStore(storeId);
        transactions.sort(Comparator.comparing(Transaction::getDate));

        List<LocalDate> dates = transactions.stream()
                .map(Transaction::getDate)
                .distinct()
                .collect(Collectors.toList());

        List<BigDecimal> cumulativeSums = transactions.stream()
                .collect(Collectors.groupingBy(Transaction::getDate, Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
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

    private VerticalLayout createTransactionsTable(int storeId) {
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
    private Component createMonthlyRevenueProfitBarChart(Integer storeId) {
        int currentYear = LocalDate.now().getYear(); // Assuming you want to display data for the current year
    
        // Fetch monthly data
        List<BigDecimal> monthlyRevenues = transactionService.getMonthlyRevenueForStore(storeId, currentYear);
        List<BigDecimal> monthlyProfits = transactionService.getMonthlyProfitForStore(storeId, currentYear);
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    
        // Create the bar chart
        Chart chart = new Chart(ChartType.COLUMN);
        Configuration conf = chart.getConfiguration();
        conf.getChart().setStyledMode(true);
        conf.setTitle("Monthly Revenue and Profit Analysis");
        chart.setWidth("97%");
    
        XAxis xAxis = new XAxis();
        xAxis.setCategories(months);
        conf.addxAxis(xAxis);
    
        YAxis yAxis = new YAxis();
        yAxis.setTitle("Amount ($)");
        conf.addyAxis(yAxis);
    
        // Adding the revenue series to the bar chart
        ListSeries revenueSeries = new ListSeries("Revenue", monthlyRevenues.toArray(new Number[0]));
        conf.addSeries(revenueSeries);
    
        // Adding the profit series to the bar chart
        ListSeries profitSeries = new ListSeries("Profit", monthlyProfits.toArray(new Number[0]));
        conf.addSeries(profitSeries);
    
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
