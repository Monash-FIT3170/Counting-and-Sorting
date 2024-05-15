package com.business.application.views.inventory;

import com.business.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.apache.commons.lang3.StringUtils;
import com.business.application.views.inventory.ProductFrontend;

import java.util.Arrays;
import java.util.List;

@PageTitle("Inventory")
@Route(value = "inventory", layout = MainLayout.class)
@AnonymousAllowed
public class InventoryView extends Div {

    private Grid<ProductFrontend> grid;
    private GridListDataView<ProductFrontend> gridListDataView;

    private Grid.Column<ProductFrontend> itemIDColumn;
    private Grid.Column<ProductFrontend> nameColumn;
    private Grid.Column<ProductFrontend> categoryColumn;
    private Grid.Column<ProductFrontend> quantityColumn;
    private Grid.Column<ProductFrontend> stockLevelColumn;

    public InventoryView() {
        addClassName("inventory-view");
        setSizeFull();
        createGrid();
        add(createToolbar(), grid);
    }

    private void createGrid() {
        createGridComponent();
        addColumnsToGrid();
        addFiltersToGrid();
    }

    private void createGridComponent() {
        grid = new Grid<>();
        grid.setSelectionMode(SelectionMode.NONE);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_COLUMN_BORDERS);
        grid.setHeight("100%");

        List<ProductFrontend> products = getProducts();
        gridListDataView = grid.setItems(products);
    }

    private void addColumnsToGrid() {
        createItemIDColumn();
        createNameColumn();
        createCategoryColumn();
        createQuantityColumn();
        createStockLevelColumn();
    }

    private void createItemIDColumn() {
        itemIDColumn = grid
                .addColumn(ProductFrontend::getItemID)
                .setHeader("Item ID")
                .setSortable(true);
    }

    private void createNameColumn() {
        nameColumn = grid
                .addColumn(ProductFrontend::getName)
                .setAutoWidth(true)
                .setHeader("Item Name")
                .setSortable(true);
    }

    private void createCategoryColumn() {
        categoryColumn = grid
                .addColumn(ProductFrontend::getCategory)
                .setHeader("Category")
                .setSortable(true);
    }

    private void createQuantityColumn() {
        quantityColumn = grid
                .addColumn(ProductFrontend::getQuantity)
                .setHeader("Current Quantity")
                .setSortable(true);
    }

    private void createStockLevelColumn() {
        stockLevelColumn = grid
                .addColumn(new ComponentRenderer<>(product -> {
                    Span stockLevel = new Span();
                    String stockStatus = product.getStockStatus();
                    stockLevel.setText(stockStatus);
                    if ("High".equals(stockStatus)) {
                        stockLevel.getElement().setAttribute("theme", "badge success");
                    } else if ("Medium".equals(stockStatus)) {
                        stockLevel.getElement().setAttribute("theme", "badge warning");
                    } else if ("Low".equals(stockStatus)) {
                        stockLevel.getElement().setAttribute("theme", "badge error");
                    }
                    return stockLevel;
                }))
                .setHeader("Stock Level");
    }

    private void addFiltersToGrid() {
        HeaderRow filterRow = grid.appendHeaderRow();

        TextField itemIDFilter = new TextField();
        itemIDFilter.setPlaceholder("Filter");
        itemIDFilter.setClearButtonVisible(true);
        itemIDFilter.setWidth("100%");
        itemIDFilter.setValueChangeMode(ValueChangeMode.EAGER);
        itemIDFilter.addValueChangeListener(event -> gridListDataView.addFilter(product -> StringUtils.containsIgnoreCase(Integer.toString(product.getItemID()), itemIDFilter.getValue())));
        filterRow.getCell(itemIDColumn).setComponent(itemIDFilter);

        TextField nameFilter = new TextField();
        nameFilter.setPlaceholder("Filter");
        nameFilter.setClearButtonVisible(true);
        nameFilter.setWidth("100%");
        nameFilter.setValueChangeMode(ValueChangeMode.EAGER);
        nameFilter.addValueChangeListener(event -> gridListDataView.addFilter(product -> StringUtils.containsIgnoreCase(product.getName(), nameFilter.getValue())));
        filterRow.getCell(nameColumn).setComponent(nameFilter);

        TextField categoryFilter = new TextField();
        categoryFilter.setPlaceholder("Filter");
        categoryFilter.setClearButtonVisible(true);
        categoryFilter.setWidth("100%");
        categoryFilter.setValueChangeMode(ValueChangeMode.EAGER);
        categoryFilter.addValueChangeListener(event -> gridListDataView.addFilter(product -> StringUtils.containsIgnoreCase(product.getCategory(), categoryFilter.getValue())));
        filterRow.getCell(categoryColumn).setComponent(categoryFilter);

        TextField quantityFilter = new TextField();
        quantityFilter.setPlaceholder("Filter");
        quantityFilter.setClearButtonVisible(true);
        quantityFilter.setWidth("100%");
        quantityFilter.setValueChangeMode(ValueChangeMode.EAGER);
        quantityFilter.addValueChangeListener(event -> gridListDataView.addFilter(product -> StringUtils.containsIgnoreCase(Integer.toString(product.getQuantity()), quantityFilter.getValue())));
        filterRow.getCell(quantityColumn).setComponent(quantityFilter);

        ComboBox<String> stockLevelFilter = new ComboBox<>();
        stockLevelFilter.setItems("Low", "Medium", "High");
        stockLevelFilter.setPlaceholder("Filter");
        stockLevelFilter.setClearButtonVisible(true); // This is not actually making button clear and im not sure why
        stockLevelFilter.setWidth("100%");
        stockLevelFilter.addValueChangeListener(event -> gridListDataView.addFilter(product -> {
            String filterValue = stockLevelFilter.getValue();
            if (filterValue != null) {
                return filterValue.equalsIgnoreCase(product.getStockStatus());
            }
            return true;
        }));
        filterRow.getCell(stockLevelColumn).setComponent(stockLevelFilter);
    }

    private HorizontalLayout createToolbar() {
        TextField searchField = new TextField();
        searchField.setPlaceholder("Search Items");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setWidth("300px");

        Button filterButton = new Button("Filter", VaadinIcon.FILTER.create());

        HorizontalLayout toolbar = new HorizontalLayout(searchField, filterButton);
        toolbar.setAlignItems(Alignment.BASELINE);
        toolbar.setWidthFull();
        toolbar.setPadding(true);
        toolbar.setSpacing(true);

        return toolbar;
    }

    private List<ProductFrontend> getProducts() {
        return Arrays.asList(
                new ProductFrontend(174926328, "Vodka Cruiser: Wild Raspberry 275mL", "Premix", 375, 600),
                new ProductFrontend(174036988, "Suntory: -196 Double Lemon 10 Pack Cans 330mL", "Wine", 802, 1000),
                new ProductFrontend(846302592, "Smirnoff: Ice Double Black Cans 10 Pack 375mL", "Premix", 3079296, 5000000),
                new ProductFrontend(769035037, "Good Day: Watermelon Soju", "Misc", 3514346, 5000000),
                new ProductFrontend(185035836, "Absolut: Vodka 1L", "Beer", 542669, 1000000),
                new ProductFrontend(562784657, "Fireball: Cinnamon Flavoured Whisky 1.14L", "Spirit", 458, 2000),
                new ProductFrontend(186538594, "Brookvale Union: Vodka Lemon Squash Cans 330mL", "Premix", 997, 1000),
                new ProductFrontend(879467856, "Moët & Chandon: Impérial Brut", "Wine", 1700250, 2000000),
                new ProductFrontend(108767894, "Moët & Chandon: Rosé Impérial", "Wine", 1429048, 2000000),
                new ProductFrontend(265743940, "Vodka Cruiser: Lush Guava 275mL", "Premix", 472648, 5000000),
                new ProductFrontend(123454352, "Vodka Cruiser: Juicy Watermelon 275mL", "Misc", 833, 1500),
                new ProductFrontend(456374567, "Fireball: Cinnamon Flavoured Whisky 1.14L", "Spirit", 222, 1000),
                new ProductFrontend(867584756, "Smirnoff: Ice Double Black Cans 10 Pack 375mL", "Premix", 438, 1000),
                new ProductFrontend(347453482, "Absolut: Vodka 1L", "Beer", 1913750, 2000000),
                new ProductFrontend(956836417, "Suntory: -196 Double Lemon 10 Pack Cans 330mL", "Wine", 528950, 600000),
                new ProductFrontend(958403584, "Fireball: Cinnamon Flavoured Whisky 1.14L", "Spirit", 3750, 8000),
                new ProductFrontend(239563895, "Good Day: Watermelon Soju", "Spirit", 290600, 500000),
                new ProductFrontend(375845219, "Smirnoff: Ice Double Black Cans 10 Pack 375mL", "Misc", 4933400, 5000000),
                new ProductFrontend(384926414, "Vodka Cruiser: Lush Guava 275mL", "Premix", 2266200, 3000000),
                new ProductFrontend(194637894, "Fireball: Cinnamon Flavoured Whisky 1.14L", "Beer", 1563450, 2000000)
        );
    }

}
