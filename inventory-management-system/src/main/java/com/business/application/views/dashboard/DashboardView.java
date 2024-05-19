package com.business.application.views.dashboard;

import java.util.List;
import java.util.stream.Collectors;

import com.business.application.domain.ListOfShoppingList;
import com.business.application.domain.ShoppingList;
import com.business.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.theme.lumo.LumoIcon;
import com.vaadin.flow.theme.lumo.LumoUtility.BoxSizing;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.FontWeight;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("Dashboard")
@Route(value = "dashboard", layout = MainLayout.class)
@RolesAllowed("USER") // Keep only for the user role
public class DashboardView extends Main {

    public DashboardView() {
        addClassName("dashboard-view");

        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setWidthFull();

        VerticalLayout leftColumn = new VerticalLayout();
        leftColumn.setWidth("calc(55% - 16px)");

        HorizontalLayout highlightsLayout = new HorizontalLayout();
        highlightsLayout.setWidthFull();
        highlightsLayout.add(createHighlight("Monthly Revenue", "$213,434.40", 11.0),
                createHighlight("Total Inventory Count", "12,345,340", 11.0));

        leftColumn.add(highlightsLayout);
        leftColumn.add(createViewSales());
        leftColumn.add(createViewStockLevels());

        VerticalLayout rightColumn = new VerticalLayout();
        rightColumn.setWidth("calc(45% - 16px)");
        rightColumn.add(createLowStockItems());
        rightColumn.add(createShoppingListRequests());

        mainLayout.add(leftColumn, rightColumn);
        add(mainLayout);
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
        layout.addClassName("rounded-rectangle");
        layout.addClassName(Padding.LARGE);
        layout.setPadding(false);
        layout.setSpacing(false);
        return layout;
    }

    private Component createViewSales() {
        // Header
        IntegerField year = new IntegerField();
        year.addClassName("dashboard-view-year-1");
        year.setValue(2024);
        year.setStepButtonsVisible(true);
        year.setMin(1970);
        year.setMax(2024);

        HorizontalLayout header = createHeader("View Sales Qty", "");
        header.add(year);
        header.setAlignItems(FlexComponent.Alignment.CENTER);

        // Chart
        Chart chart = new Chart(ChartType.AREASPLINE);
        Configuration conf = chart.getConfiguration();
        conf.getChart().setStyledMode(true);

        XAxis xAxis = new XAxis();
        xAxis.setCategories("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
        conf.addxAxis(xAxis);

        conf.getyAxis().setTitle("Qty (thousand pcs.)");

        PlotOptionsAreaspline plotOptions = new PlotOptionsAreaspline();
        plotOptions.setPointPlacement(PointPlacement.ON);
        plotOptions.setMarker(new Marker(false));
        conf.addPlotOptions(plotOptions);

        conf.addSeries(new ListSeries("Beer", 189, 191, 291, 396, 501, 403, 609, 712, 729, 942, 1044, 1247));
        conf.addSeries(new ListSeries("Wine", 138, 246, 248, 348, 352, 353, 463, 573, 778, 779, 885, 887));
        conf.addSeries(new ListSeries("Spirits", 65, 65, 166, 171, 293, 302, 308, 317, 427, 429, 535, 636));
        conf.addSeries(new ListSeries("Premix", 0, 11, 17, 123, 130, 142, 248, 349, 452, 454, 458, 462));
        conf.addSeries(new ListSeries("Misc", 15, 50, 17, 100, 110, 142, 200, 273, 230, 260, 300, 390));

        // Add it all together
        VerticalLayout viewEvents = new VerticalLayout(header, chart);
        viewEvents.addClassName(Padding.LARGE);
        viewEvents.setPadding(false);
        viewEvents.setSpacing(false);
        viewEvents.getElement().getThemeList().add("spacing-l");
        viewEvents.addClassName("rounded-rectangle");
        return viewEvents;
    }

    private Component createViewStockLevels() {
        // Chart
        Chart chart = new Chart(ChartType.BAR); // Changed to BAR type

        Configuration conf = chart.getConfiguration();
        conf.setTitle("Stock Levels by Category");
        conf.getChart().setStyledMode(true);
        conf.addSeries(new ListSeries("Beer", 5));
        conf.addSeries(new ListSeries("Wine", 85));
        conf.addSeries(new ListSeries("Spirits", 25));
        conf.addSeries(new ListSeries("Premix", 99));
        conf.addSeries(new ListSeries("Misc", 52));

        XAxis x = new XAxis();
        x.setCategories("Stock Levels");
        conf.addxAxis(x);

        YAxis y = new YAxis();
        y.setMin(0);
        y.setMax(100);
        AxisTitle yTitle = new AxisTitle();
        yTitle.setText("Percentage of Stock Remaining");
        yTitle.setAlign(VerticalAlign.HIGH);
        y.setTitle(yTitle);
        conf.addyAxis(y);

        Tooltip tooltip = new Tooltip();
        tooltip.setValueSuffix(" %");
        conf.setTooltip(tooltip);
        
        PlotOptionsBar plotOptions = new PlotOptionsBar();
        DataLabels dataLabels = new DataLabels();
        dataLabels.setEnabled(true);
        plotOptions.setDataLabels(dataLabels);
        conf.setPlotOptions(plotOptions);

        // Add it all together
        VerticalLayout viewStockLevels = new VerticalLayout(chart);
        viewStockLevels.addClassName(Padding.LARGE);
        viewStockLevels.setPadding(false);
        viewStockLevels.addClassName("rounded-rectangle");
        viewStockLevels.setHeight("30%");
        viewStockLevels.setWidth("100%");
        return viewStockLevels;
    }
    
    private Component createLowStockItems() {
        // Header
        // HorizontalLayout header = createHeader("Low Stock Items", "Store");
    
        // Create a grid bound to the StockItem type
        Grid<ServiceStockItem> grid = new Grid<>(ServiceStockItem.class, false);
        grid.addClassName("dashboard-view-grid-1");
        // grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        
        // Customize the 'Status' column to show a colored circle based on status

        grid.addColumn(new ComponentRenderer<>(serviceStockItem -> {
            Span status = new Span();
            //String statusText = getStatusDisplayName(serviceStockItem);
            //status.getElement().setAttribute("aria-label", "Status: " + statusText);
            status.getElement().getStyle().set("display", "inline-block");
            status.getElement().getStyle().set("width", "10px");
            status.getElement().getStyle().set("height", "10px");
            status.getElement().getStyle().set("border-radius", "50%");

            if (serviceStockItem.getQtyRemaining() > 100) {
                status.getElement().getStyle().set("background-color", "green");
            } else if (serviceStockItem.getQtyRemaining() > 50) {
                status.getElement().getStyle().set("background-color", "yellow");
            } else {
                status.getElement().getStyle().set("background-color", "red");
            }

            return status;
        })).setHeader("Status");

        grid.addColumn(ServiceStockItem::getStockName).setHeader("Item Name");
        grid.addColumn(ServiceStockItem::getQtyRemaining).setHeader("Qty Remaining");
    
        grid.addThemeVariants(GridVariant.LUMO_COMPACT);
        // Sample data
        grid.setItems(
            new ServiceStockItem(" ", "Smirnoff: Ice Double Black", 296),
            new ServiceStockItem(" ", "Vodka Cruiser: Wild Raspberry", 97),
            new ServiceStockItem(" ", "Suntory: -196 Double Lemon", 156),
            new ServiceStockItem(" ", "Good Day: Watermelon", 46),
            new ServiceStockItem(" ", "Absolut: Vodka 1L", 9),
            new ServiceStockItem(" ", "Fireball: Cinnamon Flavoured Whisky", 60),
            new ServiceStockItem(" ", "Brookvale Union: Vodka Ginger Beer", 302),
            new ServiceStockItem(" ", "Moët & Chandon: Impérial", 250),
            new ServiceStockItem(" ", "Moët & Chandon: Rosé Impérial", 48),
            new ServiceStockItem(" ", "Vodka Cruiser: Lush Guava", 32)
    );

        HorizontalLayout head = createHeader("Low Stock Items", "");
        Icon icon = LumoIcon.UNORDERED_LIST.create();
        icon.getElement().getThemeList().add("badge pill cir");

        // Add it all together
        head.add(icon);
        head.setAlignItems(FlexComponent.Alignment.CENTER);
        VerticalLayout layout = new VerticalLayout(head, grid);
        layout.addClassName(Padding.LARGE);
        layout.setPadding(false);
        layout.addClassName("rounded-rectangle");
        return layout;
    }

        private Component createShoppingListRequests() {
        // Header
        HorizontalLayout header = createHeader("PENDING SHOPPING LIST REQUESTS", "Approve or Deny Requests");

        // Grid
        Grid<ShoppingList> grid = new Grid<>(ShoppingList.class, false);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setAllRowsVisible(true);

        grid.addColumn(ShoppingList::getListId).setHeader("List ID").setSortable(true);
        grid.addColumn(ShoppingList::getName).setHeader("List Name").setSortable(true);
        grid.addColumn(ShoppingList::getDateString).setHeader("Date").setSortable(true);

        List<ShoppingList> pendingShoppingLists = getPendingShoppingLists();
        grid.setItems(pendingShoppingLists);

        // Add click listener to navigate to requests tab
        grid.addItemClickListener(event -> {
            // Navigate to requests tab
            getUI().ifPresent(ui -> ui.navigate("requests"));
        });

        // Add it all together
        VerticalLayout layout = new VerticalLayout(header, grid);
        layout.addClassName(Padding.LARGE);
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.addClassName("rounded-rectangle");
        return layout;
    }

    private List<ShoppingList> getPendingShoppingLists() {
        return ListOfShoppingList.getInstance().getShoppingLists().stream()
            .filter(list -> "Pending".equals(list.getStatus()))
            .collect(Collectors.toList());
    }

    private HorizontalLayout createHeader(String title, String subtitle) {
        H2 h2 = new H2(title);
        h2.addClassName("dashboard-view-h2-1");
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

    public static class StockItem {
        private String status;
        private String itemName;
        private int qtyRemaining;

        public StockItem(String status, String itemName, int qtyRemaining) {
            this.status = status;
            this.itemName = itemName;
            this.qtyRemaining = qtyRemaining;
        }

        public String getStatus() {
            return status;
        }

        public String getItemName() {
            return itemName;
        }

        public int getQtyRemaining() {
            return qtyRemaining;
        }
    }}

    