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


import java.util.Optional;
import com.business.application.domain.Store;
import com.business.application.domain.ListOfShoppingList;
import com.business.application.domain.ShoppingList;
import com.business.application.domain.User;
import com.business.application.security.AuthenticatedUser;
import com.business.application.services.StoreService;
import com.business.application.services.TransactionService;
import com.business.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
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
    public DashboardView( AuthenticatedUser authenticatedUser,StoreService storeService, TransactionService transactionService) {
        addClassName("dashboard-view");
        this.authenticatedUser = authenticatedUser;
        this.storeService = storeService;
        this.transactionService = transactionService;

        int storeId = 1;

        BigDecimal totalSales = transactionService.getTotalSalesForStore(storeId);
        BigDecimal profit = transactionService.getProfitForStore(storeId);

        // HorizontalLayout storeInfoLayout = createStoreInfoLayout();
        // add(storeInfoLayout);
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



        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setWidthFull();

        VerticalLayout leftColumn = new VerticalLayout();
        leftColumn.setWidth("calc(55% - 16px)");

        HorizontalLayout highlightsLayout = new HorizontalLayout();
        highlightsLayout.setWidthFull();
        highlightsLayout.add(createHighlight("Monthly Revenue",formatCurrency(totalSales.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_EVEN)), 11.0),
                createHighlight("Inventory Count", "12,345,340", 11.0));

        HorizontalLayout highlightsLayout2 = new HorizontalLayout();
        highlightsLayout2.setWidthFull();
        highlightsLayout2.add(createHighlight("Profit", formatCurrency(profit), 0.0),
        createHighlight("Total Sales", formatCurrency(totalSales), 0.0));        

        leftColumn.add(highlightsLayout, highlightsLayout2);
        leftColumn.add(createViewSalesQty());
        leftColumn.add(createStockLevelsByCategory());

        VerticalLayout rightColumn = new VerticalLayout();
        rightColumn.setWidth("calc(45% - 16px)");
        rightColumn.add(createLowStockItemsGrid());
        rightColumn.add(createNotifications());

        mainLayout.add(leftColumn, rightColumn);
        add(mainLayout);
    }

    private String formatCurrency(BigDecimal accountBalance) {
        return String.format("$%,.2f", accountBalance);
    }

    private Component createNotifications() {
        HorizontalLayout head = createHeader("NOTIFICATIONS", "");
        head.add(LumoIcon.BELL.create());
        VerticalLayout notificationsLayout = new VerticalLayout(head);
        notificationsLayout.addClassName(Padding.LARGE);
        notificationsLayout.setPadding(false);
        notificationsLayout.setSpacing(false);
        notificationsLayout.addClassName("rounded-rectangle");

        for (String notificationText : new String[]{"Request #219 Edited", "Request #224 Approved",
                "Request #256 Declined", "Request #214 Approved"}) {

            Button viewButton = new Button("View");
            viewButton.addClickListener(clickEvent -> {
                Notification notification = new Notification();
                notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
                notification.setText(notificationText);

                Button closeButton = new Button("Close", e -> notification.close());
                notification.add(new HorizontalLayout(new Span(notificationText), closeButton));
                notification.setDuration(5000);
                notification.open();
            });

            HorizontalLayout notificationItem = new HorizontalLayout(new Span(notificationText), viewButton);
            notificationItem.addClassName("notification-item");
            notificationItem.setWidthFull();
            notificationItem.setAlignItems(FlexComponent.Alignment.CENTER);
            notificationsLayout.add(notificationItem);
        }
        return notificationsLayout;
    }

    // private Component createShoppingListRequests() {
    //     // Header
    //     HorizontalLayout header = createHeader("PENDING SHOPPING LIST REQUESTS", "Approve or Deny Requests");

    //     // Grid
    //     Grid<ShoppingList> grid = new Grid<>(ShoppingList.class, false);
    //     grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
    //     grid.setAllRowsVisible(true);

    //     grid.addColumn(ShoppingList::getListId).setHeader("List ID").setSortable(true);
    //     grid.addColumn(ShoppingList::getName).setHeader("List Name").setSortable(true);
    //     grid.addColumn(ShoppingList::getDateString).setHeader("Date").setSortable(true);

    //     List<ShoppingList> pendingShoppingLists = getPendingShoppingLists();
    //     grid.setItems(pendingShoppingLists);

    //     // Add click listener to navigate to requests tab
    //     grid.addItemClickListener(event -> {
    //         // Navigate to requests tab
    //         getUI().ifPresent(ui -> ui.navigate("requests"));
    //     });

    //     // Add it all together
    //     VerticalLayout layout = new VerticalLayout(header, grid);
    //     layout.addClassName(Padding.LARGE);
    //     layout.setPadding(false);
    //     layout.setSpacing(false);
    //     layout.addClassName("rounded-rectangle");
    //     return layout;
    // }

    // private List<ShoppingList> getPendingShoppingLists() {
    //     return ListOfShoppingList.getInstance().getShoppingLists().stream()
    //         .filter(list -> "Pending".equals(list.getStatus()))
    //         .collect(Collectors.toList());
    // }

    // private HorizontalLayout createStoreInfoLayout() {
    //     // Search bar
    //     // TextField searchBar = new TextField();
    //     // searchBar.addClassName("admin-dashboard-view-store-search");
    //     // searchBar.setPlaceholder("Select Store");
    //     // searchBar.setSuffixComponent(LumoIcon.SEARCH.create());
    //     // searchBar.setWidth("300px");

    //     // Layout for store info and search bar
    //     // HorizontalLayout layout = createHeader("CLAYTON", "");
    //     // layout.add(searchBar);
    //     layout.setWidthFull();
    //     layout.setHeight("50px");
    //     layout.setAlignItems(FlexComponent.Alignment.CENTER);
    //     layout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
    //     layout.addClassName(Padding.LARGE);
    //     layout.addClassName("search-top-section");
    //     return layout;
    // }

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

    private Component createViewSalesQty() {
        // Header
        IntegerField year = new IntegerField();
        year.addClassName("admin-dashboard-view-year-1");
        year.setValue(2024);
        year.setStepButtonsVisible(true);
        year.setMin(1970);
        year.setMax(2024);

        HorizontalLayout header = createHeader("View Sales Qty", "");
        header.add(year);
        header.setAlignItems(FlexComponent.Alignment.CENTER);

        // Chart
        Chart chart = new Chart(ChartType.SPLINE);
        Configuration conf = chart.getConfiguration();
        conf.getChart().setStyledMode(true);

        XAxis xAxis = new XAxis();
        xAxis.setCategories("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
        conf.addxAxis(xAxis);

        conf.getyAxis().setTitle("Values");

        PlotOptionsSpline plotOptions = new PlotOptionsSpline();
        plotOptions.setPointPlacement(PointPlacement.ON);
        plotOptions.setMarker(new Marker(false));
        conf.addPlotOptions(plotOptions);

        conf.addSeries(new ListSeries("Beer", 189, 191, 291, 396, 501, 403, 609, 712, 729, 942, 1044, 1247));
        conf.addSeries(new ListSeries("Wine", 138, 246, 248, 348, 352, 353, 463, 573, 778, 779, 885, 887));
        conf.addSeries(new ListSeries("Spirits", 65, 65, 166, 171, 293, 302, 308, 317, 427, 429, 535, 636));

        // Add it all together
        VerticalLayout viewEvents = new VerticalLayout(header, chart);
        viewEvents.addClassName(Padding.LARGE);
        viewEvents.setPadding(false);
        viewEvents.setSpacing(false);
        viewEvents.getElement().getThemeList().add("spacing-l");
        viewEvents.addClassName("rounded-rectangle");
        return viewEvents;
    }

    private Component createStockLevelsByCategory(){
        Chart chart = new Chart(ChartType.BAR);

        Configuration configuration = chart.getConfiguration();
        configuration.setTitle("Stock Levels by Category");
        configuration.getChart().setStyledMode(true);
        configuration.addSeries(new ListSeries("Beer", 5));
        configuration.addSeries(new ListSeries("Wine", 85));
        configuration.addSeries(new ListSeries("Spirits", 25));
        configuration.addSeries(new ListSeries("Premix", 99));
        configuration.addSeries(new ListSeries("Misc", 52));

        XAxis x = new XAxis();
        x.setCategories("Stock Levels");
        configuration.addxAxis(x);

        YAxis y = new YAxis();
        y.setMin(0);
        y.setMax(100);
        AxisTitle yTitle = new AxisTitle();
        yTitle.setText("Percentage of Stock Remaining");
        yTitle.setAlign(VerticalAlign.HIGH);
        y.setTitle(yTitle);
        configuration.addyAxis(y);

        Tooltip tooltip = new Tooltip();
        tooltip.setValueSuffix(" %");
        configuration.setTooltip(tooltip);
        
        
        PlotOptionsBar plotOptions = new PlotOptionsBar();
        DataLabels dataLabels = new DataLabels();
        dataLabels.setEnabled(true);
        plotOptions.setDataLabels(dataLabels);
        configuration.setPlotOptions(plotOptions);

        VerticalLayout layout = new VerticalLayout(chart);
        layout.addClassName(Padding.LARGE);
        layout.setPadding(false);
        layout.addClassName("rounded-rectangle");
        layout.setHeight("30%");
        layout.setWidth("100%");
        return layout;

    }

    private Component createLowStockItemsGrid() {
    Grid<StockItem> grid = new Grid<>(StockItem.class, false);
    // <theme-editor-local-classname>
    grid.addClassName("admin-dashboard-view-grid-1");

    // Custom renderer for status column
    grid.addColumn(new ComponentRenderer<>(stockItem -> {
        Span statusCircle = new Span();
        statusCircle.getElement().getStyle().set("display", "inline-block");
        statusCircle.getElement().getStyle().set("width", "10px");
        statusCircle.getElement().getStyle().set("height", "10px");
        statusCircle.getElement().getStyle().set("border-radius", "50%");
        
        if (stockItem.getQtyRemaining() > 100) {
            statusCircle.getElement().getStyle().set("background-color", "green");
        } else if (stockItem.getQtyRemaining() > 50) {
            statusCircle.getElement().getStyle().set("background-color", "yellow");
        } else {
            statusCircle.getElement().getStyle().set("background-color", "red");
        }

        return statusCircle;
    })).setHeader("Status");

    grid.addColumn(StockItem::getItemName).setHeader("Item Name");
    grid.addColumn(StockItem::getQtyRemaining).setHeader("Qty Remaining");

    grid.addThemeVariants(GridVariant.LUMO_COMPACT);
    grid.setItems(
            new StockItem(" ", "Smirnoff: Ice Double Black", 296),
            new StockItem(" ", "Vodka Cruiser: Wild Raspberry", 97),
            new StockItem(" ", "Suntory: -196 Double Lemon", 156),
            new StockItem(" ", "Good Day: Watermelon", 46),
            new StockItem(" ", "Absolut: Vodka 1L", 9),
            new StockItem(" ", "Fireball: Cinnamon Flavoured Whisky", 60),
            new StockItem(" ", "Brookvale Union: Vodka Ginger Beer", 302),
            new StockItem(" ", "Moët & Chandon: Impérial", 250),
            new StockItem(" ", "Moët & Chandon: Rosé Impérial", 48),
            new StockItem(" ", "Vodka Cruiser: Lush Guava", 32)
    );

    HorizontalLayout head = createHeader("Low Stock Items", "");
    Icon icon = LumoIcon.UNORDERED_LIST.create();
    icon.getElement().getThemeList().add("badge pill cir");
    
    head.add(icon);
    head.setAlignItems(FlexComponent.Alignment.CENTER);
    VerticalLayout layout = new VerticalLayout(head, grid);
    layout.addClassName(Padding.LARGE);
    layout.setPadding(false);
    layout.addClassName("rounded-rectangle");
    return layout;
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