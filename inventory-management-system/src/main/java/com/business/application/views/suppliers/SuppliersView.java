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
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoIcon;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;

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
        this.webscrapedProductService = webscrapedProductService;
        addClassName("suppliers-view");
        // Create the toolbar
        HorizontalLayout toolbar = createToolbar();

        // Create the grid
        createGrid();
        createSuppliers();

        // Layout
        VerticalLayout mainLayout = new VerticalLayout(toolbar, grid);
        mainLayout.setSizeFull();
        mainLayout.setPadding(false);
        mainLayout.setSpacing(false);
        grid.getElement().getStyle().set("margin-top", "16px");

        // Add to the view
        add(mainLayout);
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
        // Create the search bar
        TextField searchBar = new TextField();
        searchBar.addClassName("toolbar-search-bar");
        searchBar.setPlaceholder("Search Items");
        searchBar.setSuffixComponent(LumoIcon.SEARCH.create());
        searchBar.setWidth("300px");

        // Add filters to search bar
        searchBar.setValueChangeMode(ValueChangeMode.EAGER);
        searchBar.addValueChangeListener(event -> {
            String filterText = event.getValue().trim();
            if (filterText.isEmpty()) {
                // Clear all fsilters if the search field is empty
                gridListDataView.removeFilters();
            } else {
                gridListDataView.removeFilters();
                gridListDataView.addFilter(supplier -> {
                    // Filter by name, caetegory, sale price,m and supplier
                    boolean matchesName = StringUtils.containsIgnoreCase(supplier.getName(), filterText);
                    boolean matchesCategory = StringUtils.containsIgnoreCase(supplier.getCategory(), filterText);
                    boolean matchesPrice = StringUtils.containsIgnoreCase(Double.toString(supplier.getPrice()),
                            filterText);
                    boolean matchesSupplier = StringUtils.containsIgnoreCase(supplier.getSupplier(), filterText);
                    return matchesName || matchesCategory || matchesPrice || matchesSupplier;
                });
            }
        });

        // Create the layout for the label and search field
        HorizontalLayout toolbar = createHeader("Suppliers");
        toolbar.add(searchBar);
        return toolbar;
    }

    private HorizontalLayout createHeader(String title) {
        // Header container
        HorizontalLayout header = new HorizontalLayout();
        
        header.addClassName("search-top-section");
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.addClassNames(
            LumoUtility.Padding.Left.XLARGE,
            LumoUtility.Padding.Right.XLARGE
            );
        
        H6 title_txt = new H6(title);
        
        title_txt.addClassNames(LumoUtility.TextColor.SECONDARY);

        header.add(title_txt);
        return header;
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
        nameFilter.setPlaceholder("Search");
        nameFilter.setClearButtonVisible(true);
        nameFilter.setWidth("100%");
        nameFilter.setValueChangeMode(ValueChangeMode.EAGER);
        nameFilter.addValueChangeListener(event -> {
            // Clear existing filters before adding the new one
            gridListDataView.removeFilters();
            gridListDataView
                    .addFilter(supplier -> StringUtils.containsIgnoreCase(supplier.getName(), nameFilter.getValue()));
        });
        filterRow.getCell(ItemNameColumn).setComponent(nameFilter);

        ComboBox<String> categoryFilter = new ComboBox<>();
        categoryFilter.setItems("Beer", "Wine", "Spirits", "Cider", "Misc");
        categoryFilter.setPlaceholder("Filter");
        categoryFilter.setClearButtonVisible(true);
        categoryFilter.setWidth("100%");
        categoryFilter.addValueChangeListener(event -> {
            // Clear existing filters before adding the new one
            gridListDataView.removeFilters();
            gridListDataView.addFilter(supplier -> {
                String filterValue = categoryFilter.getValue();
                if (filterValue != null) {
                    return filterValue.equalsIgnoreCase(supplier.getCategory());
                }
                return true;
            });
        });
        filterRow.getCell(CategoryColumn).setComponent(categoryFilter);

        TextField salePriceFilter = new TextField();
        salePriceFilter.setPlaceholder("Search");
        salePriceFilter.setClearButtonVisible(true);
        salePriceFilter.setWidth("100%");
        salePriceFilter.setValueChangeMode(ValueChangeMode.EAGER);
        salePriceFilter.addValueChangeListener(event -> {
            // Clear existing filters before adding the new one
            gridListDataView.removeFilters();
            gridListDataView.addFilter(supplier -> StringUtils
                    .containsIgnoreCase(Double.toString(supplier.getPrice()), salePriceFilter.getValue()));
        });
        filterRow.getCell(SalePriceColumn).setComponent(salePriceFilter);

        TextField supplierFilter = new TextField();
        supplierFilter.setPlaceholder("Search");
        supplierFilter.setClearButtonVisible(true);
        supplierFilter.setWidth("100%");
        supplierFilter.setValueChangeMode(ValueChangeMode.EAGER);
        supplierFilter.addValueChangeListener(event -> {
            // Clear existing filters before adding the new one
            gridListDataView.removeFilters();
            gridListDataView.addFilter(
                    supplier -> StringUtils.containsIgnoreCase(supplier.getSupplier(), supplierFilter.getValue()));
        });
        filterRow.getCell(SupplierColumn).setComponent(supplierFilter);
    }

};
