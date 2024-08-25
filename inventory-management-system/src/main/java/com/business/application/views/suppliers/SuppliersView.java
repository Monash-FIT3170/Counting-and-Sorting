package com.business.application.views.suppliers;

import com.business.application.domain.WebScrapedProduct;
import com.business.application.services.WebScrapedProductService;
import com.business.application.views.MainLayout;
import com.business.application.views.inventory.ProductFrontend;
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
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import com.vaadin.flow.component.notification.Notification;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;


@PageTitle("Suppliers")
@Route(value = "data-grid2", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class SuppliersView extends Div {
    private List<WebScrapedProduct> suppliers = new ArrayList<>();
    private GridPro<WebScrapedProduct> grid;
    private GridListDataView<WebScrapedProduct> gridListDataView;
    private Grid.Column<WebScrapedProduct> IdColumn;
    private Grid.Column<WebScrapedProduct> ItemNameColumn;
    private Grid.Column<WebScrapedProduct> CategoryColumn;
    private Grid.Column<WebScrapedProduct> QtyColumn;
    private Grid.Column<WebScrapedProduct> SalePriceColumn;
    private Grid.Column<WebScrapedProduct> SupplierColumn;

    private WebScrapedProductService webscrapedProductService;

    @Autowired
    public SuppliersView(WebScrapedProductService webscrapedProductService) {
        this.webscrapedProductService =  webscrapedProductService;
        addClassName("suppliers-view");
        setSizeFull();
        createGrid();
        createSuppliers();
        add(createToolbar(), grid);
    }

    private void createSuppliers() {
        suppliers = webscrapedProductService.getAllWebscrapedProducts();
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
        createIdColumn();
        createItemNameColumn();
        createCategoryColumn();
        createSalePriceColumn();
        createSupplierColumn();
    }

    private void createIdColumn() {
        IdColumn = grid
                .addColumn(WebScrapedProduct::getId)
                .setHeader("ID")
                .setSortable(true);
    }

    private void createItemNameColumn() {
        ItemNameColumn = grid
                .addColumn(WebScrapedProduct::getName)
                .setAutoWidth(true)
                .setHeader("Item Name")
                .setSortable(true);
    }

    private void createCategoryColumn() {
        CategoryColumn = grid.addColumn(new ComponentRenderer<>(supplier -> {
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

    private void createSalePriceColumn() {
        SalePriceColumn = grid
                .addColumn(WebScrapedProduct::getPrice)
                .setHeader("Sale Price")
                .setSortable(true);
    }

    private void createSupplierColumn() {
        SupplierColumn = grid
                .addColumn(WebScrapedProduct::getSupplier)
                .setHeader("Supplier")
                .setSortable(true);
    }

    private HorizontalLayout createToolbar() {
        TextField searchField = new TextField();
        searchField.setPlaceholder("Search Items");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setWidth("300px");

        Button refreshButton = new Button("Refresh", VaadinIcon.REFRESH.create());
        refreshButton.addClickListener(click -> refreshGridData());

        HorizontalLayout toolbar = new HorizontalLayout(searchField, refreshButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void refreshGridData() {
        // replace this line with a service call the webscraper endpoint 
        suppliers = webscrapedProductService.getAllWebscrapedProducts();
        gridListDataView = grid.setItems(suppliers);
        grid.getDataProvider().refreshAll();
        grid.deselectAll();
        // Wait 5 seconds
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Notification.show("Data refreshed");
    }

    private void addFiltersToGrid() {
        HeaderRow filterRow = grid.appendHeaderRow();

        //Id filter
        TextField idFilter = new TextField();
        idFilter.setPlaceholder("Filter");
        idFilter.setClearButtonVisible(true);
        idFilter.setWidth("100%");
        idFilter.setValueChangeMode(ValueChangeMode.EAGER);
        idFilter.addValueChangeListener(event -> gridListDataView.addFilter(supplier -> StringUtils.containsIgnoreCase(Long.toString(supplier.getId()), idFilter.getValue())));
        filterRow.getCell(IdColumn).setComponent(idFilter);

        TextField nameFilter = new TextField();
        nameFilter.setPlaceholder("Filter");
        nameFilter.setClearButtonVisible(true);
        nameFilter.setWidth("100%");
        nameFilter.setValueChangeMode(ValueChangeMode.EAGER);
        nameFilter.addValueChangeListener(event -> gridListDataView.addFilter(supplier -> StringUtils.containsIgnoreCase(supplier.getName(), nameFilter.getValue())));
        filterRow.getCell(ItemNameColumn).setComponent(nameFilter);

        ComboBox<String> categoryFilter = new ComboBox<>();
        categoryFilter.setItems("Beer", "Wine", "Spirits", "Cider", "Misc");
        categoryFilter.setPlaceholder("Filter");
        categoryFilter.setClearButtonVisible(true);
        categoryFilter.setWidth("100%");
        categoryFilter.addValueChangeListener(event -> gridListDataView.addFilter(supplier -> {
            String filterValue = categoryFilter.getValue();
            if (filterValue != null) {
                return filterValue.equalsIgnoreCase(supplier.getCategory());
            }
            return true;
        }));
        filterRow.getCell(CategoryColumn).setComponent(categoryFilter);

        TextField salePriceFilter = new TextField();
        salePriceFilter.setPlaceholder("Filter");
        salePriceFilter.setClearButtonVisible(true);
        salePriceFilter.setWidth("100%");
        salePriceFilter.setValueChangeMode(ValueChangeMode.EAGER);
        salePriceFilter.addValueChangeListener(event -> gridListDataView.addFilter(supplier -> StringUtils.containsIgnoreCase(Double.toString(supplier.getPrice()), salePriceFilter.getValue())));
        filterRow.getCell(SalePriceColumn).setComponent(salePriceFilter);

        TextField supplierFilter = new TextField();
        supplierFilter.setPlaceholder("Filter");
        supplierFilter.setClearButtonVisible(true);
        supplierFilter.setWidth("100%");
        supplierFilter.setValueChangeMode(ValueChangeMode.EAGER);
        supplierFilter.addValueChangeListener(event -> gridListDataView.addFilter(supplier -> StringUtils.containsIgnoreCase(supplier.getSupplier(), supplierFilter.getValue())));
        filterRow.getCell(SupplierColumn).setComponent(supplierFilter);
    }
}
