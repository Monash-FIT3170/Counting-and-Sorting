package com.business.application.views.inventory;

import com.business.application.domain.WebScrapedProduct;
import com.business.application.services.WebScrapedProductService;
import com.business.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.gridpro.GridPro;
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

import jakarta.annotation.security.RolesAllowed;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Role;

import com.business.application.views.inventory.ProductFrontend;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

@PageTitle("Inventory")
@Route(value = "inventory", layout = MainLayout.class)
@RolesAllowed("USER")
public class InventoryView extends Div {
    private List<WebScrapedProduct> suppliers = new ArrayList<>();

    private Grid<WebScrapedProduct> grid;
    private GridListDataView<WebScrapedProduct> gridListDataView;

    private Grid.Column<WebScrapedProduct> itemIDColumn;
    private Grid.Column<WebScrapedProduct> nameColumn;
    private Grid.Column<WebScrapedProduct> categoryColumn;
    private Grid.Column<WebScrapedProduct> quantityColumn;
    private Grid.Column<WebScrapedProduct> stockLevelColumn;

    private WebScrapedProductService webScrapedProductService;

    @Autowired
    public InventoryView(WebScrapedProductService webScrapedProductService) {
        this.webScrapedProductService = webScrapedProductService;
        addClassName("inventory-view");
        setSizeFull();
        createGrid();
        createInventory();
        add(grid);

        
    }

    private void createInventory() {
        suppliers = webScrapedProductService.getAllWebscrapedProducts();
        gridListDataView = grid.setItems(suppliers);
    }

    private void createGrid() {
        createGridComponent();
        addColumnsToGrid();
        addFiltersToGrid();
    }

    private void createGridComponent() {
        grid = new GridPro<>();
        grid.setSelectionMode(SelectionMode.SINGLE);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_COLUMN_BORDERS);
        grid.setHeight("100%");
        gridListDataView = grid.setItems(suppliers);
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
                .addColumn(WebScrapedProduct::getId)
                .setHeader("Item ID")
                .setSortable(true);
    }

    private void createNameColumn() {
        nameColumn = grid
                .addColumn(WebScrapedProduct::getName)
                .setAutoWidth(true)
                .setHeader("Item Name")
                .setSortable(true);
    }

    private void createCategoryColumn() {
        categoryColumn = grid
                .addColumn(new ComponentRenderer<>(supplier -> {
                    Span categorySpan = new Span();
                    String category = supplier.getCategory();
                    categorySpan.setText(category);
                    if ("Wine".equals(category)) {
                        categorySpan.getElement().setAttribute("class", "badge-wine");
                    } else if ("Beer".equals(category)) {
                        categorySpan.getElement().setAttribute("class", "badge-beer");
                    } else if ("Spirits".equals(category)) {
                        categorySpan.getElement().setAttribute("class", "badge-spirit");
                    } else if ("Cider".equals(category)) {
                        categorySpan.getElement().setAttribute("class", "badge-cider");
                    } else {
                        categorySpan.getElement().setAttribute("class", "badge-misc");
                    }
                    return categorySpan;
                }))
                .setHeader("Category");
    }

    private void createQuantityColumn() {
        quantityColumn = grid
                .addColumn(WebScrapedProduct::getQuantity)
                .setHeader("Current Quantity")
                .setSortable(true);
    }

    private void createStockLevelColumn() {
        stockLevelColumn = grid
                .addColumn(new ComponentRenderer<>(product -> {
                    Span stockLevel = new Span();
                    int stockQuantity = product.getQuantity();
                    String stockStatus = "High";
                    stockLevel.setText(stockStatus);
                    if (stockQuantity >= 500) {
                        stockLevel.setText("High");
                        stockLevel.getElement().setAttribute("theme", "badge success");
                    } else if (stockQuantity >= 100) {
                        stockLevel.setText("Medium");
                        stockLevel.getElement().setAttribute("theme", "badge warning");
                    } else {
                        stockLevel.setText("Low");
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
        itemIDFilter.addValueChangeListener(event -> gridListDataView.addFilter(product -> StringUtils.containsIgnoreCase(Integer.toString(product.getId().intValue()), itemIDFilter.getValue())));
        filterRow.getCell(itemIDColumn).setComponent(itemIDFilter);

        TextField nameFilter = new TextField();
        nameFilter.setPlaceholder("Filter");
        nameFilter.setClearButtonVisible(true);
        nameFilter.setWidth("100%");
        nameFilter.setValueChangeMode(ValueChangeMode.EAGER);
        nameFilter.addValueChangeListener(event -> gridListDataView.addFilter(product -> StringUtils.containsIgnoreCase(product.getName(), nameFilter.getValue())));
        filterRow.getCell(nameColumn).setComponent(nameFilter);

        ComboBox<String> categoryFilter = new ComboBox<>();
        categoryFilter.setItems("Beer", "Wine", "Spirit", "Premix", "Misc");
        categoryFilter.setPlaceholder("Filter");
        categoryFilter.setClearButtonVisible(true); // This is not actually making button clear and im not sure why
        categoryFilter.setWidth("100%");
        categoryFilter.addValueChangeListener(event -> gridListDataView.addFilter(product -> {
            String filterValue = categoryFilter.getValue();
            if (filterValue != null) {
                return filterValue.equalsIgnoreCase(product.getCategory());
            }
            return true;
        }));
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
                return filterValue.equalsIgnoreCase(getStockLevel(product.getQuantity()));
            }
            return true;
        }));
        filterRow.getCell(stockLevelColumn).setComponent(stockLevelFilter);
    }

    private String getStockLevel(int quantity) {
        if (quantity >= 500) {
            return "High";
        } else if (quantity >= 100) {
            return "Medium";
        } else {
            return "Low";
        }
    }

    

}
