package com.business.application.views.admindashboard;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;
import com.business.application.domain.ListOfShoppingList;
import com.business.application.domain.ShoppingList;
import com.business.application.domain.WebScrapedProduct;
import com.business.application.services.TransactionService;
import com.business.application.services.WebScrapedProductService;
import com.business.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ScrollOptions.Alignment;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.board.Row;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.dom.Style.Display;
import com.vaadin.flow.dom.Style.FlexBasis;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoIcon;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.theme.lumo.LumoUtility.BoxSizing;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.FontWeight;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("Admin Dashboard")
@Route(value = "admin-dashboard", layout = MainLayout.class)
@RolesAllowed("ADMIN")

public class AdminDashboardView extends Main {

    private final TransactionService transactionService;
    private WebScrapedProductService webScrapedProductService;
    private List<WebScrapedProduct> webScrapedProducts;

    public AdminDashboardView(TransactionService transactionService, WebScrapedProductService webScrapedProductService) {
        addClassName("admin-dashboard-view");
        this.transactionService = transactionService;
        this.webScrapedProductService = webScrapedProductService;
        this.webScrapedProducts = webScrapedProductService.getAllWebscrapedProducts();

        Board board = new Board();
        
        Row mainRow = new Row();

        Board leftBoard = new Board();
        leftBoard.addRow(
            createHighlight(
                "Average Store Monthly Revenue", 
                formatCurrency(getTotalRevenue().divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_EVEN)), 
                9.0
                ), 
            createHighlight(
                "Total Inventory Count", 
                "37,345,340", 
                8.0
                )).addClassName("board-top-left");
        leftBoard.addRow(
            createHighlight(
                "Total Store Profit", 
                formatCurrency(getTotalProfit()), 
                0.0
                ),
            createHighlight(
                "Total Store Revenue", 
                formatCurrency(getTotalRevenue()), 
                0.0
                )).addClassName("board-top-left");
        leftBoard.addRow(createViewSalesQty());
        leftBoard.addRow(createStockLevelsByCategory()).addClassName("stock-levels-row");

        Board rightBoard = new Board();
        rightBoard.addRow(createLowStockItemsGrid());
        rightBoard.addRow(createShoppingListRequests());
        
        mainRow.add(leftBoard);
        mainRow.add(rightBoard);

        board.add(mainRow);

        add(createStoreInfoLayout(), board);
    }

    private BigDecimal getTotalRevenue() {
        return transactionService.getTotalSalesForAllStores().values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal getTotalProfit() {
        return transactionService.getProfitsForAllStores().values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String formatCurrency(BigDecimal accountBalance) {
        return String.format("$%,.2f", accountBalance);
    }

    private Component createShoppingListRequests() {
        // Header
        H6 h6 = new H6("Requests");
        h6.addClassNames(TextColor.SECONDARY);

        // Grid
        Grid<ShoppingList> grid = new Grid<>(ShoppingList.class, false);
        grid.addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_NO_BORDER);

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

        grid.setHeight("auto");

        // Add it all together
        VerticalLayout layout = new VerticalLayout(h6, grid);
        layout.addClassName("rounded-rectangle");

        VerticalLayout layout_wrapper = new VerticalLayout(layout);
        layout_wrapper.addClassName("rounded-rectangle-wrapper");
        return layout_wrapper;
    }

    private List<ShoppingList> getPendingShoppingLists() {
        return ListOfShoppingList.getInstance().getShoppingLists().stream()
            .filter(list -> "Pending".equals(list.getStatus()))
            .collect(Collectors.toList());
    }

    private HorizontalLayout createStoreInfoLayout() {
        // Header container
        HorizontalLayout layout = new HorizontalLayout();
        
        layout.addClassName("search-top-section");
        layout.setWidthFull();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        layout.addClassNames(
            LumoUtility.Padding.Left.XLARGE,
            LumoUtility.Padding.Right.XLARGE
            );
        
        // TODO: make store location dynamic
        // Store location
        H6 location = new H6("Clayton");
        
        location.addClassNames(LumoUtility.TextColor.SECONDARY);
        
        // Search bar
        TextField searchBar = new TextField();
        searchBar.addClassName("toolbar-search-bar");
        searchBar.setPlaceholder("Select Store");
        searchBar.setSuffixComponent(LumoIcon.SEARCH.create());
        searchBar.setWidth("300px"); 

        layout.add(location, searchBar);
        return layout;
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

        H6 h6 = new H6(title);
        h6.addClassNames(TextColor.SECONDARY);

        Icon i = icon.create();
        i.addClassNames(BoxSizing.BORDER, Padding.XSMALL);

        Span badge = new Span(i, new Span(prefix + percentage.toString()));
        badge.getElement().getThemeList().add(theme);

        HorizontalLayout header = new HorizontalLayout(h6, badge);
        header.setPadding(false);

        H2 h2 = new H2(value);

        VerticalLayout layout = new VerticalLayout(header, h2);
        layout.addClassName("rounded-rectangle");

        VerticalLayout layout_wrapper = new VerticalLayout(layout);
        layout_wrapper.addClassName("rounded-rectangle-wrapper");
        return layout_wrapper;
    }

    private Component createViewSalesQty() {
        // Header
        IntegerField year = new IntegerField();
        year.addClassName("admin-dashboard-view-year-1");
        year.setValue(2024);
        year.setStepButtonsVisible(true);
        year.setMin(1970);
        year.setMax(2024);

        H6 h6 = new H6("View Sales Qty");
        h6.addClassNames(TextColor.SECONDARY);

        HorizontalLayout header = new HorizontalLayout(h6, year);
        header.setPadding(false);

        // Chart
        Chart chart = new Chart(ChartType.SPLINE);
        Configuration conf = chart.getConfiguration();
        conf.getChart().setStyledMode(true);

        XAxis xAxis = new XAxis();
        xAxis.setCategories("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
        conf.addxAxis(xAxis);

        conf.getyAxis().setTitle("Quantity");

        PlotOptionsSpline plotOptions = new PlotOptionsSpline();
        plotOptions.setPointPlacement(PointPlacement.ON);
        plotOptions.setMarker(new Marker(false));
        conf.addPlotOptions(plotOptions);

        conf.addSeries(new ListSeries("Beer", 189, 191, 291, 396, 501, 403, 609, 712, 729, 942, 1044, 1247));
        conf.addSeries(new ListSeries("Wine", 138, 246, 248, 348, 352, 353, 463, 573, 778, 779, 885, 887));
        conf.addSeries(new ListSeries("Spirits", 65, 65, 166, 171, 293, 302, 308, 317, 427, 429, 535, 636));

        // Add it all together
        VerticalLayout layout = new VerticalLayout(header, chart);
        layout.addClassName("rounded-rectangle");
        
        VerticalLayout layout_wrapper = new VerticalLayout(layout);
        layout_wrapper.addClassName("rounded-rectangle-wrapper");
        return layout_wrapper;
    }

    private Component createStockLevelsByCategory(){
        Chart chart = new Chart(ChartType.BAR);

        Configuration configuration = chart.getConfiguration();
        configuration.getChart().setStyledMode(true);
        configuration.addSeries(new ListSeries("Beer", 5));
        configuration.addSeries(new ListSeries("Wine", 85));
        configuration.addSeries(new ListSeries("Spirits", 25));
        configuration.addSeries(new ListSeries("Premix", 99));
        configuration.addSeries(new ListSeries("Misc", 52));

        XAxis x = new XAxis();
        x.setCategories("Stock Level");
        x.setVisible(false);
        configuration.addxAxis(x);

        YAxis y = new YAxis();
        y.setMin(0);
        y.setMax(101);
        y.setVisible(false);
        configuration.addyAxis(y);

        Tooltip tooltip = new Tooltip();
        tooltip.setValueSuffix("%");
        configuration.setTooltip(tooltip);
        
        DataLabels dataLabels = new DataLabels();
        dataLabels.setEnabled(true);
        dataLabels.setFormat("{y}%");
        
        PlotOptionsBar plotOptions = new PlotOptionsBar();
        plotOptions.setPointWidth(10);
        plotOptions.setDataLabels(dataLabels);
       
        configuration.setPlotOptions(plotOptions);

        chart.setHeight("auto");

        H6 h6 = new H6("Stock Levels by Category");
        h6.addClassNames(TextColor.SECONDARY);

        VerticalLayout layout = new VerticalLayout(h6, chart);
        // layout.addClassName(Padding.LARGE);
        // layout.setPadding(false);
        layout.addClassNames("rounded-rectangle", "stock-levels");
        // layout.setHeight("30%");
        // layout.setWidth("100%");

        VerticalLayout layout_wrapper = new VerticalLayout(layout);
        layout_wrapper.addClassNames("rounded-rectangle-wrapper", "stock-levels-wrapper");
        return layout_wrapper;

    }

    private Component createLowStockItemsGrid() {
    Grid<WebScrapedProduct> grid = new Grid<>(WebScrapedProduct.class, false);
    // <theme-editor-local-classname>
    grid.addClassName("admin-dashboard-view-grid-1");

        // Custom renderer for status column
        grid.addColumn(new ComponentRenderer<>(stockItem -> {
            Span statusCircle = new Span();
            statusCircle.getElement().getStyle().set("display", "inline-block");
            statusCircle.getElement().getStyle().set("width", "14px");
            statusCircle.getElement().getStyle().set("height", "14px");
            statusCircle.getElement().getStyle().set("border-radius", "50%");
            statusCircle.getElement().getStyle().set("display", "flex");
            statusCircle.getElement().getStyle().set("justify-content", "center");
            statusCircle.getElement().getStyle().set("align-items", "center");

            Span statusInnerCircle = new Span();
            statusInnerCircle.getElement().getStyle().set("display", "inline-block");
            statusInnerCircle.getElement().getStyle().set("width", "6px");
            statusInnerCircle.getElement().getStyle().set("height", "6px");
            statusInnerCircle.getElement().getStyle().set("border-radius", "50%");

            statusCircle.add(statusInnerCircle);
            
            if (stockItem.getQuantity() > 100) {
                statusCircle.getElement().getStyle().set("background-color", "#1688464D");
                statusInnerCircle.getElement().getStyle().set("background-color", "var(--lumo-success-color)");
            } else if (stockItem.getQuantity() > 50) {
                statusCircle.getElement().getStyle().set("background-color", "var(--lumo-warning-color-10pct)");
                statusInnerCircle.getElement().getStyle().set("background-color", "var(--lumo-warning-color)");
            } else {
                statusCircle.getElement().getStyle().set("background-color", "#E71D134D");
                statusInnerCircle.getElement().getStyle().set("background-color", "var(--lumo-error-color)");
            }

            return statusCircle;
        })).setHeader("Status").setTextAlign(ColumnTextAlign.CENTER).setAutoWidth(true).setFlexGrow(0).addClassName("status-column");

    grid.addColumn(WebScrapedProduct::getName).setHeader("Item Name").setTextAlign(ColumnTextAlign.START);
    grid.addColumn(WebScrapedProduct::getQuantity).setHeader("Qty Remaining").setTextAlign(ColumnTextAlign.END).setAutoWidth(true).setFlexGrow(0);

    grid.addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_NO_BORDER);
    grid.setItems(
        webScrapedProducts.stream().filter(product -> product.getQuantity() < 100).collect(Collectors.toList())
    );

    grid.setHeight("auto");

    H6 h6 = new H6("Low Stock Items");
    h6.addClassNames(TextColor.SECONDARY);
    
    VerticalLayout layout = new VerticalLayout(h6, grid);
    layout.addClassName("rounded-rectangle");

    VerticalLayout layout_wrapper = new VerticalLayout(layout);
    layout_wrapper.addClassName("rounded-rectangle-wrapper");

    return layout_wrapper;
    }

    private HorizontalLayout createHeader(String title, String subtitle) {
        H2 h2 = new H2(title);
        h2.addClassName("admin-dashboard-view-h2-1");
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
    }
}