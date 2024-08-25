package com.business.application.views.adminforecast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.List;
import java.util.Random;

import com.business.application.views.MainLayout;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.board.Row;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.AbstractSeries;
import com.vaadin.flow.component.charts.model.AxisType;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.Crosshair;
import com.vaadin.flow.component.charts.model.HorizontalAlign;
import com.vaadin.flow.component.charts.model.LayoutDirection;
import com.vaadin.flow.component.charts.model.Legend;
import com.vaadin.flow.component.charts.model.ListSeries;
import com.vaadin.flow.component.charts.model.RangeSeries;
import com.vaadin.flow.component.charts.model.Tooltip;
import com.vaadin.flow.component.charts.model.VerticalAlign;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;

@PageTitle("Admin Forecast")
@Route(value = "admin-forecast", layout = MainLayout.class)
@RolesAllowed("ADMIN")

public class AdminForecastView extends Div {
    public AdminForecastView() {
        addClassName("admin-forecast-view");
        // Create a board layout
        Board board = new Board();
        
        Tab allCategoriesTab = new Tab("All Categories");
        Tab beerTab = new Tab("Beer");
        Tab wineTab = new Tab("Wine");
        Tab spiritsTab = new Tab("Spirits");
        Tab premixTab = new Tab("Premix");
        Tab miscTab = new Tab("Misc");

        Tabs tabs = new Tabs(allCategoriesTab, beerTab, wineTab, spiritsTab, premixTab, miscTab);
        tabs.addThemeVariants(TabsVariant.LUMO_EQUAL_WIDTH_TABS);

        tabs.setSelectedTab(wineTab);

        add(tabs);

        // Create a chart

        Chart chart = new Chart(ChartType.SPLINE);

        regenerateChartData(chart);        

        tabs.addSelectedChangeListener(event -> {
            Tab selectedTab = event.getSelectedTab();
            if (selectedTab == allCategoriesTab) { displayAllGraphs(selectedTab, chart); }
            else if (selectedTab == beerTab) { displayBeerGraph(selectedTab, chart); }
            else if (selectedTab == wineTab) { displayWineGraph(selectedTab, chart); }
            else if (selectedTab == spiritsTab ) { displaySpiritsGraph(selectedTab, chart); }
            else if (selectedTab == premixTab ) { displayPremixGraph(selectedTab, chart); }
            else if (selectedTab == miscTab ) { displayMiscGraph(selectedTab, chart); }
        });

        // Add components to the board
        Row row = new Row(tabs);
        board.addRow(row);
        board.addRow(new Row(chart));

        add(board);
    }

    private void regenerateChartData(Chart chart) {
    Configuration conf = chart.getConfiguration();
    
    conf.getxAxis().setTitle("Percentage of Full Capacity");
    conf.getxAxis().setType(AxisType.DATETIME);
    conf.getxAxis().setCrosshair(new Crosshair());

    Tooltip tooltip = new Tooltip();
    tooltip.setShared(true);
    tooltip.setValueSuffix("%");
    conf.setTooltip(tooltip);

    List<ListSeries> allSeries = new ArrayList<>();

    ListSeries[] PremixDataSet = {
        new ListSeries("Vodka Cruiser: Wild Raspberry 275mL", generateRandomNumbers(365)),
        new ListSeries("Smirnoff: Ice Double Black Cans 10 Pack 375mL", generateRandomNumbers(365)),
        new ListSeries("Brookvale Union: Vodka Lemon Squash Cans 330mL", generateRandomNumbers(365)),
        new ListSeries("Vodka Cruiser: Lush Guava 275mL", generateRandomNumbers(365)),
        new ListSeries("Smirnoff: Ice Double Black Cans 10 Pack 375mL", generateRandomNumbers(365))
    };

    ListSeries[] WineDataSet = {
        new ListSeries("Suntory: -196 Double Lemon 10 Pack Cans 330mL", generateRandomNumbers(365)),
        new ListSeries("Moét & Chandon: Impérial Brut", generateRandomNumbers(365)),
        new ListSeries("Moét & Chandon: Rosé Impérial", generateRandomNumbers(365))
    };
    
    ListSeries[] BeerDataSet = {
        new ListSeries("Absolut: Vodka 1L", generateRandomNumbers(365))
    };

    ListSeries[] SpiritsDataSet = {
        new ListSeries("Fireball: Cinnamon Flavoured Whisky 1.14L", generateRandomNumbers(365))
    };

    ListSeries[] MiscDataSet = {
        new ListSeries("Good Day: Watermelon Soju", generateRandomNumbers(365)),
        new ListSeries("Vodka Cruiser: Juicy Watermelon 275mL", generateRandomNumbers(365))
    };

    allSeries.addAll(Arrays.asList(PremixDataSet));
    allSeries.addAll(Arrays.asList(WineDataSet));
    allSeries.addAll(Arrays.asList(BeerDataSet));
    allSeries.addAll(Arrays.asList(SpiritsDataSet));
    allSeries.addAll(Arrays.asList(MiscDataSet));

    conf.setSeries(allSeries.toArray(new ListSeries[0]));

    chart.setTimeline(true);
    
    Legend legend = conf.getLegend();
    legend.setLayout(LayoutDirection.VERTICAL);
    legend.setVerticalAlign(VerticalAlign.MIDDLE);
    legend.setAlign(HorizontalAlign.RIGHT);

    conf.setLegend(legend);
}

    private void displayMiscGraph(Tab selectedTab, Chart chart) {
        regenerateChartData(chart);
    
        Configuration conf = chart.getConfiguration();
        ListSeries[] MiscDataSet = {
            new ListSeries("Good Day: Watermelon Soju", generateRandomNumbers(365)),
            new ListSeries("Vodka Cruiser: Juicy Watermelon 275mL", generateRandomNumbers(365))
        };
        conf.setSeries(MiscDataSet);
    
        String selectedOption = selectedTab.getLabel();
        conf.setTitle("Forecast for " + selectedOption);
        chart.drawChart();
    }

    private void displayPremixGraph(Tab selectedTab, Chart chart) {
        regenerateChartData(chart);
    
        Configuration conf = chart.getConfiguration();
        ListSeries[] PremixDataSet = {
            new ListSeries("Vodka Cruiser: Wild Raspberry 275mL", generateRandomNumbers(365)),
            new ListSeries("Smirnoff: Ice Double Black Cans 10 Pack 375mL", generateRandomNumbers(365)),
            new ListSeries("Brookvale Union: Vodka Lemon Squash Cans 330mL", generateRandomNumbers(365)),
            new ListSeries("Vodka Cruiser: Lush Guava 275mL", generateRandomNumbers(365)),
            new ListSeries("Smirnoff: Ice Double Black Cans 10 Pack 375mL", generateRandomNumbers(365))
        };
        conf.setSeries(PremixDataSet);
    
        String selectedOption = selectedTab.getLabel();
        conf.setTitle("Forecast for " + selectedOption);
        chart.drawChart();
    }

    private void displaySpiritsGraph(Tab selectedTab, Chart chart) {
        regenerateChartData(chart);
    
        Configuration conf = chart.getConfiguration();
        ListSeries[] SpiritsDataSet = {
            new ListSeries("Fireball: Cinnamon Flavoured Whisky 1.14L", generateRandomNumbers(365))
        };
        conf.setSeries(SpiritsDataSet);
    
        String selectedOption = selectedTab.getLabel();
        conf.setTitle("Forecast for " + selectedOption);
        chart.drawChart();
    }

    private void displayWineGraph(Tab selectedTab, Chart chart) {
        regenerateChartData(chart);
    
        Configuration conf = chart.getConfiguration();
        ListSeries[] WineDataSet = {
            new ListSeries("Suntory: -196 Double Lemon 10 Pack Cans 330mL", generateRandomNumbers(365)),
            new ListSeries("Moét & Chandon: Impérial Brut", generateRandomNumbers(365)),
            new ListSeries("Moét & Chandon: Rosé Impérial", generateRandomNumbers(365))
        };
        conf.setSeries(WineDataSet);
    
        String selectedOption = selectedTab.getLabel();
        conf.setTitle("Forecast for " + selectedOption);
        chart.drawChart();
    }

    private void displayBeerGraph(Tab selectedTab, Chart chart) {
        regenerateChartData(chart); // Regenerate chart data
    
        Configuration conf = chart.getConfiguration();
        ListSeries[] beerDataSet = {new ListSeries("Absolut: Vodka 1L", generateRandomNumbers(365))};
        conf.setSeries(beerDataSet);
    
        String selectedOption = selectedTab.getLabel();
        conf.setTitle("Forecast for " + selectedOption);
        chart.drawChart();
    }

    private void displayAllGraphs(Tab selectedTab, Chart chart) {
        regenerateChartData(chart); // Regenerate chart data
    
        Configuration conf = chart.getConfiguration();
    
        String selectedOption = selectedTab.getLabel();
        conf.setTitle("Forecast for " + selectedOption);
        chart.drawChart();
    }

    public static Number[] generateRandomNumbers(int n) {
        Random random = new Random();
        Number[] numbers = new Number[n];
        double minValue = 0.0; // Minimum value
        double maxValue = 100.0; // Maximum value
    
        for (int i = 0; i < n; i++) {
            numbers[i] = minValue + (maxValue - minValue) * random.nextDouble();
        }
    
        return numbers;
    }

}