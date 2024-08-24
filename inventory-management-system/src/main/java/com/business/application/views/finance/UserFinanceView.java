package com.business.application.views.finance;

import com.business.application.domain.Transaction;
import com.business.application.services.TransactionService;
import com.business.application.views.MainLayout;
import com.vaadin.flow.component.Component;
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
import com.vaadin.flow.theme.lumo.LumoUtility.*;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@PageTitle("Store Finance")
@Route(value = "store-finance", layout = MainLayout.class)
@RolesAllowed("USER")
public class UserFinanceView extends Div {

    private final TransactionService transactionService;

    @Autowired
    public UserFinanceView(TransactionService transactionService) {
        this.transactionService = transactionService;
        addClassName("store-finance-view");

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
                createHighlight("Account Balance", formatCurrency(accountBalance), 0.0),
                createHighlight("Profit", formatCurrency(profit), 0.0),
                createHighlight("Total Sales", formatCurrency(totalSales), 0.0),
                createHighlight("Total Expenses", formatCurrency(totalExpenses), 0.0)
        );

        // Cost and Revenue breakdown section side by side
        HorizontalLayout breakdownLayout = new HorizontalLayout();
        breakdownLayout.setWidthFull();
        breakdownLayout.add(
                createPieChartWithDetails("Cost Breakdown", costBreakdown, true),
                createPieChartWithDetails("Revenue Breakdown", revenueBreakdown, false)
        );

        VerticalLayout analysisLayout = new VerticalLayout();

        // ComboBox for selecting view
        ComboBox<String> selectionComboBox = new ComboBox<>("Select View");
        selectionComboBox.setItems("Graph View", "Table View");
        selectionComboBox.addValueChangeListener(e -> switchView(e.getValue(), analysisLayout, storeId));

        analysisLayout.add(selectionComboBox);

        mainLayout.add(highlightsLayout, breakdownLayout, analysisLayout);
        add(mainLayout);
    }

    private void switchView(String viewType, VerticalLayout analysisLayout, int storeId) {
        analysisLayout.removeAll();

        if ("Graph View".equals(viewType)) {
            List<BigDecimal> accountBalanceOverTime = transactionService.getAccountBalancesForAllStores()
                    .values().stream().collect(Collectors.toList());
            analysisLayout.add(createFinancialGraph(accountBalanceOverTime));
        } else if ("Table View".equals(viewType)) {
            analysisLayout.add(createTransactionGrid(storeId));
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

    private Component createFinancialGraph(List<BigDecimal> accountBalanceOverTime) {
        // Header
        IntegerField year = new IntegerField();
        year.addClassName("store-financial-graph");
        year.setValue(2024);
        year.setStepButtonsVisible(true);
        year.setMin(2020);
        year.setMax(2024);

        HorizontalLayout header = createHeader("Account Balance Over Time", "");
        header.add(year);
        header.setAlignItems(FlexComponent.Alignment.CENTER);

        // Chart
        Chart chart = new Chart(ChartType.SPLINE);
        Configuration conf = chart.getConfiguration();
        conf.getChart().setStyledMode(true);
        chart.setWidth("97%");

        XAxis xAxis = new XAxis();
        xAxis.setCategories("Month 1", "Month 2", "Month 3", "Month 4", "Month 5", "Month 6", "Month 7", "Month 8", "Month 9", "Month 10", "Month 11", "Month 12");
        conf.addxAxis(xAxis);

        YAxis yAxis = new YAxis();
        yAxis.setTitle("Account Balance ($)");
        conf.addyAxis(yAxis);

        PlotOptionsSpline plotOptions = new PlotOptionsSpline();
        plotOptions.setPointPlacement(PointPlacement.ON);
        plotOptions.setMarker(new Marker(false));
        conf.addPlotOptions(plotOptions);

        // Use real data from the services
        conf.addSeries(new ListSeries("Account Balance", accountBalanceOverTime.toArray(new BigDecimal[0])));

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

    private Grid<Transaction> createTransactionGrid(int storeId) {
        Grid<Transaction> grid = new Grid<>(Transaction.class);
        grid.addColumn(Transaction::getStoreId).setHeader("Store ID");
        grid.addColumn(Transaction::getItem).setHeader("Item");
        grid.addColumn(Transaction::getAmount).setHeader("Amount");

        return grid;
    }

    private String formatCurrency(BigDecimal value) {
        return "$" + value.setScale(2, BigDecimal.ROUND_HALF_EVEN).toString();
    }

    private Component createPieChartWithDetails(String title, Map<String, BigDecimal> breakdown, boolean isCost) {
        // Create the pie chart
        Chart chart = new Chart(ChartType.PIE);
        Configuration conf = chart.getConfiguration();
        conf.setTitle(title);

        DataSeries series = new DataSeries();

        breakdown.forEach((key, value) -> {
            // Costs should be positive in the chart even though they are negative values
            BigDecimal displayValue = isCost ? value.abs() : value;
            series.add(new DataSeriesItem(key, displayValue.doubleValue()));
        });

        conf.setSeries(series);
        chart.setWidth("50%");

        // Create the details list
        VerticalLayout detailsLayout = new VerticalLayout();
        breakdown.forEach((key, value) -> {
            Span detail = new Span(key + ": " + formatCurrency(value));
            detailsLayout.add(detail);
        });
        detailsLayout.setPadding(false);
        detailsLayout.setSpacing(false);

        // Combine the chart and details in a single layout
        VerticalLayout layout = new VerticalLayout(chart, detailsLayout);
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.addClassName("rounded-rectangle");
        layout.setWidth("100%");

        return layout;
    }
}
