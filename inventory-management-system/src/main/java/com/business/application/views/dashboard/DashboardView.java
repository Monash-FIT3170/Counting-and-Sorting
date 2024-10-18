package com.business.application.views.dashboard;


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

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import com.business.application.domain.Store;
import com.business.application.domain.ListOfShoppingList;
import com.business.application.domain.ShoppingList;
import com.business.application.domain.User;
import com.business.application.domain.WebScrapedProduct;
import com.business.application.security.AuthenticatedUser;
import com.business.application.services.StoreService;
import com.business.application.services.TransactionService;
import com.business.application.services.WebScrapedProductService;
import com.business.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.board.Row;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
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
private AuthenticatedUser authenticatedUser;
private StoreService storeService;
private final TransactionService transactionService;
private Store store; 
private WebScrapedProductService webScrapedProductService;
private List<WebScrapedProduct> products;

    @Autowired
    public DashboardView( AuthenticatedUser authenticatedUser,StoreService storeService, TransactionService transactionService, WebScrapedProductService webScrapedProductService) {
        addClassName("dashboard-view");
        this.authenticatedUser = authenticatedUser;
        this.storeService = storeService;
        this.transactionService = transactionService;
        this.webScrapedProductService = webScrapedProductService;
        this.products = webScrapedProductService.getAllWebscrapedProducts();

        int storeId = 1;

        BigDecimal totalSales = transactionService.getTotalSalesForStore(storeId);
        BigDecimal profit = transactionService.getProfitForStore(storeId);

        Optional<User> maybeUser = authenticatedUser.get(); // Assuming authenticatedUser is an Optional<User>
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            String userName = user.getName(); 
            user.getId();
            this.store = storeService.getStoreByManagerId(user.getId());
            System.out.println("User name: " + userName);
            } else {
            
            System.out.println("No user present");
        }

                Board board = new Board();
        
        Row mainRow = new Row();

        Board leftBoard = new Board();
        leftBoard.addRow(
            createHighlight(
                "Monthly Revenue",
                formatCurrency(totalSales.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_EVEN)), 
                11.0
                ),
            createHighlight(
                "Inventory Count", 
                "12,345,340", 
                11.0
                )).addClassName("board-top-left");
        leftBoard.addRow(
            createHighlight(
                "Profit", 
                formatCurrency(profit), 
                0.0
                ),
            createHighlight(
                "Total Sales", 
                formatCurrency(totalSales), 
                0.0
                )).addClassName("board-top-left");
        leftBoard.addRow(createViewSalesQty());
        leftBoard.addRow(createStockLevelsByCategory()).addClassName("stock-levels-row");

        Board rightBoard = new Board();
        rightBoard.addRow(createLowStockItemsGrid());
        rightBoard.addRow(createNotifications());
        
        mainRow.add(leftBoard);
        mainRow.add(rightBoard);

        board.add(mainRow);

        add(board);
    }

    private String formatCurrency(BigDecimal accountBalance) {
        return String.format("$%,.2f", accountBalance);
    }

    private Component createNotifications() {
        // Header
        H6 h6 = new H6("Requests");
        h6.addClassNames(TextColor.SECONDARY);

        // Add it all together
        VerticalLayout request_wrapper = new VerticalLayout();
        request_wrapper.addClassName("notification-wrapper");

        for (String notificationText : new String[]{"Request #219 Edited", "Request #224 Approved",
        "Request #256 Declined", "Request #214 Approved"}) {

            Button viewButton = new Button("View");
            viewButton.addClickListener(clickEvent -> {
                Notification notification = new Notification();
                notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
                notification.setText(notificationText);

                Button closeButton = new Button("Close", e -> notification.close());
                HorizontalLayout notification_modal = new HorizontalLayout(new Span(notificationText), closeButton);
                notification_modal.addClassName("notification-modal");
                notification.add(notification_modal);
                notification.setDuration(5000);
                notification.open();
            });

            HorizontalLayout notificationItem = new HorizontalLayout(new Span(notificationText), viewButton);
            notificationItem.addClassName("notification-item");
            notificationItem.setWidthFull();
            notificationItem.setAlignItems(FlexComponent.Alignment.CENTER);
            request_wrapper.add(notificationItem);
        }

        // Add it all together
        VerticalLayout layout = new VerticalLayout(h6, request_wrapper);
        layout.addClassNames("rounded-rectangle", "requests");

        VerticalLayout layout_wrapper = new VerticalLayout(layout);
        layout_wrapper.addClassName("rounded-rectangle-wrapper");
        
        return layout_wrapper;
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
        layout.addClassNames("rounded-rectangle", "stock-levels");

        VerticalLayout layout_wrapper = new VerticalLayout(layout);
        layout_wrapper.addClassNames("rounded-rectangle-wrapper", "stock-levels-wrapper");
        return layout_wrapper;

    }

    private Component createLowStockItemsGrid() {
        Grid<WebScrapedProduct> grid = new Grid<>(WebScrapedProduct.class, false);
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
        products.stream().filter(product -> product.getQuantity() < 100).collect(Collectors.toList())
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