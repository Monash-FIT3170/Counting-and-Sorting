package com.business.application.views.inventory;

import com.business.application.views.MainLayout;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.gridpro.GridPro;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;

@PageTitle("Inventory")
@Route(value = "data-grid", layout = MainLayout.class)
@AnonymousAllowed
public class InventoryView extends Div {

    private GridPro<ProductFrontend> grid;
    private GridListDataView<ProductFrontend> gridListDataView;

    private Grid.Column<ProductFrontend> itemIDColumn;
    private Grid.Column<ProductFrontend> nameColumn;
    private Grid.Column<ProductFrontend> categoryColumn;
    private Grid.Column<ProductFrontend> quantityColumn;
    private Grid.Column<ProductFrontend> capacityColumn;


    public InventoryView() {
        addClassName("inventory-view");
        setSizeFull();
        createGrid();
        add(grid);
    }

    private void createGrid() {
        createGridComponent();
        addColumnsToGrid();
        addFiltersToGrid();
    }

    private void createGridComponent() {
        grid = new GridPro<>();
        grid.setSelectionMode(SelectionMode.MULTI);
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
        createCapacityColumn();
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

    private void createCapacityColumn() {
        capacityColumn = grid
                .addColumn(ProductFrontend::getCapacity)
                .setHeader("Capacity")
                .setSortable(true);
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
    
        TextField capacityFilter = new TextField();
        capacityFilter.setPlaceholder("Filter");
        capacityFilter.setClearButtonVisible(true);
        capacityFilter.setWidth("100%");
        capacityFilter.setValueChangeMode(ValueChangeMode.EAGER);
        capacityFilter.addValueChangeListener(event -> gridListDataView.addFilter(product -> StringUtils.containsIgnoreCase(Integer.toString(product.getCapacity()), capacityFilter.getValue())));
        filterRow.getCell(capacityColumn).setComponent(capacityFilter);
    }
    private List<ProductFrontend> getProducts() {
        return Arrays.asList(
                new ProductFrontend(1, "Product A", "Category A", 10, 100),
                new ProductFrontend(2, "Product B", "Category B", 20, 200),
                new ProductFrontend(3, "Product C", "Category C", 30, 300)
        );
    }
};
