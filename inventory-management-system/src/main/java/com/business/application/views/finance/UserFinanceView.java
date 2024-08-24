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
import com.vaadin.flow.component.orderedlayout.FlexLayout;
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


@PageTitle("User Finance")
@Route(value = "User Finance", layout = MainLayout.class)
@RolesAllowed("USER")

public class UserFinanceView extends Div{

    public UserFinanceView() {
        addClassName("user-finance-view");

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setPadding(true);
        
        HorizontalLayout highlightsLayout = new HorizontalLayout();
        highlightsLayout.setWidthFull();
        highlightsLayout.add(createHighlight("Cash on Hand", "$157,434.40", 0.0),
                createHighlight("Projected Profit", "$143,777", 5.0));

        VerticalLayout analysisLayout = new VerticalLayout();

        ComboBox<String> selectionComboBox = new ComboBox<>("Select View");
        selectionComboBox.setItems("Graph", "Table");

        VerticalLayout contentContainer = new VerticalLayout();
        
        // Set default content (can be empty initially)
        contentContainer.add(createFinancialGraph());

        // Add listener to ComboBox to handle selection
        selectionComboBox.addValueChangeListener(event -> {
            // Clear the content container
            contentContainer.removeAll();

            // Show graph or list based on selection
            if ("Graph".equals(event.getValue())) {
                contentContainer.add(createFinancialGraph());
            } else if ("List".equals(event.getValue())) {
                contentContainer.add(createList());
            }
        });

        analysisLayout.add(selectionComboBox, contentContainer);

        mainLayout.add(highlightsLayout);
        add(mainLayout, analysisLayout);

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

    private Component createFinancialGraph() {
        // Header
        IntegerField year = new IntegerField();
        year.addClassName("user-financial-graph");
        year.setValue(2024);
        year.setStepButtonsVisible(true);
        year.setMin(2020);
        year.setMax(2024);

        HorizontalLayout header = createHeader("Yearly Financial Analysis", "");
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
        yAxis.setCategories("20", "40", "60", "80", "100", "120", "140", "160", "180", "200");
        conf.addyAxis(yAxis);

        PlotOptionsSpline plotOptions = new PlotOptionsSpline();
        plotOptions.setPointPlacement(PointPlacement.ON);
        plotOptions.setMarker(new Marker(false));
        conf.addPlotOptions(plotOptions);

        conf.addSeries(new ListSeries("Profit", 109, 141, 91, 126));
        conf.addSeries(new ListSeries("Income", 138, 190, 109, 150));
        conf.addSeries(new ListSeries("Expenses", 65, 61, 66, 71));

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

    private Grid<String> createList() {
        Grid<String> grid = new Grid<>();
        grid.setItems("Item 1", "Item 2", "Item 3");
        grid.addColumn(item -> item).setHeader("Items");

        return grid;
    }
    
}
