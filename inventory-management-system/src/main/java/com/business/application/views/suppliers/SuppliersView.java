package com.business.application.views.suppliers;

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

@PageTitle("Suppliers")
@Route(value = "data-grid2", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class SuppliersView extends Div {
    private List<Supplier> suppliers = new ArrayList<>();
    private GridPro<Supplier> grid;
    private GridListDataView<Supplier> gridListDataView;
    private Grid.Column<Supplier> IdColumn;
    private Grid.Column<Supplier> ItemNameColumn;
    private Grid.Column<Supplier> CategoryColumn;
    private Grid.Column<Supplier> QtyColumn;
    private Grid.Column<Supplier> SalePriceColumn;
    private Grid.Column<Supplier> SupplierColumn;

    public SuppliersView() {
        addClassName("suppliers-view");
        setSizeFull();
        createGrid();
        createSuppliers();
        add(createToolbar(), grid);
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
        createQtyColumn();
        createSalePriceColumn();
        createSupplierColumn();
    }

    private void createIdColumn() {
        IdColumn = grid.addColumn(Supplier::getItemID)
                .setHeader("Item ID")
                .setSortable(true);
    }

    private void createItemNameColumn() {
        ItemNameColumn = grid
                .addColumn(Supplier::getName)
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
                    } else if ("Spirit".equals(category)) {
                        categorySpan.getElement().setAttribute("class", "badge-spirit");
                    } else if ("Premix".equals(category)) {
                        categorySpan.getElement().setAttribute("class", "badge-premix");
                    } else {
                        categorySpan.getElement().setAttribute("class", "badge-misc");
                    }
                    return categorySpan;
                }))
                .setHeader("Category");
    }

    private void createQtyColumn() {
        QtyColumn = grid
                .addColumn(Supplier::getQuantity)
                .setHeader("Qty Per Order")
                .setSortable(true);
    }

    private void createSalePriceColumn() {
        SalePriceColumn = grid
                .addColumn(Supplier::getSalePrice)
                .setHeader("Sale Price")
                .setSortable(true);
            
    }

    private void createSupplierColumn() {
        SupplierColumn = grid
                .addColumn(Supplier::getSupplier)
                .setHeader("Supplier")
                .setSortable(true);
    }

    
    private HorizontalLayout createToolbar() {
        TextField searchField = new TextField();
        searchField.setPlaceholder("Search Items");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setWidth("300px");

        Button filterButton = new Button("Filter", VaadinIcon.FILTER.create());
        Button addButton = new Button("Add", VaadinIcon.PLUS.create());
        Button deleteButton = new Button("Delete", VaadinIcon.TRASH.create());
        Button editButton = new Button("Edit", VaadinIcon.EDIT.create());
editButton.addClickListener(event -> {
    // Get the selected item from the grid
    Supplier selectedSupplier = grid.asSingleSelect().getValue();

    if (selectedSupplier != null) {
        // Open the SupplierForm with the data of the selected supplier
        SupplierForm supplierForm = new SupplierForm();
        supplierForm.setSupplier(selectedSupplier);
        supplierForm.open();

        // Add a click listener to the save button in the SupplierForm
        supplierForm.getSaveButton().addClickListener(e -> {
            // Get the updated supplier from the form
            Supplier updatedSupplier = supplierForm.getSupplier();

            // Update the supplier in your data source
            // This depends on how your data source is implemented
            // For example, if your data source is a List<Supplier>, you could do:
            int index = suppliers.indexOf(selectedSupplier);
            if (index != -1) {
                suppliers.set(index, updatedSupplier);
            }

            // Update the grid
            grid.getDataProvider().refreshAll();

            // Close the form
            supplierForm.close();
        });
    } else {
        Notification.show("No supplier selected");
    }
});
        deleteButton.addClickListener(event -> {
            // Get the selected items from the grid
            Set<Supplier> selectedItems = grid.getSelectedItems();

            // Remove the selected items from the suppliers list
            suppliers.removeAll(selectedItems);

            // Update the grid data view
            gridListDataView.refreshAll();
        });

        addButton.addClickListener(event -> {
            // Add new supplier I need this to create a new page to add a supplier
            SupplierForm form = new SupplierForm();
            form.open();
        // Add a listener to the form's 'save' button
        form.getSaveButton().addClickListener(e -> {
            //gridListDataView.refreshAll();
            try {
                // Get the supplier from the form
                Supplier supplier = form.getSupplier();
        
                // Add the supplier to your data source
                suppliers.add(supplier);
                 // Update the grid data view
                gridListDataView.refreshAll();
                // Close the form
                form.close();
            } catch (Exception ex) {
                // Handle the exception
                // For example, you could show a notification with the error message
                Notification.show("An error occurred: " + ex.getMessage());
            }
        });
    });
            
           
        

        // Open the form
        

        HorizontalLayout toolbar = new HorizontalLayout(searchField, filterButton, addButton, deleteButton, editButton);
        toolbar.setAlignItems(Alignment.BASELINE);
        toolbar.setWidthFull();
        toolbar.setPadding(true);
        toolbar.setSpacing(true);

        return toolbar;
    }

    private void addFiltersToGrid() {
        HeaderRow filterRow = grid.appendHeaderRow();

        TextField itemIDFilter = new TextField();
        itemIDFilter.setPlaceholder("Filter");
        itemIDFilter.setClearButtonVisible(true);
        itemIDFilter.setWidth("100%");
        itemIDFilter.setValueChangeMode(ValueChangeMode.EAGER);
        itemIDFilter.addValueChangeListener(event -> gridListDataView.addFilter(supplier -> StringUtils.containsIgnoreCase(Integer.toString(supplier.getItemID()), itemIDFilter.getValue())));
        filterRow.getCell(IdColumn).setComponent(itemIDFilter);

        TextField nameFilter = new TextField();
        nameFilter.setPlaceholder("Filter");
        nameFilter.setClearButtonVisible(true);
        nameFilter.setWidth("100%");
        nameFilter.setValueChangeMode(ValueChangeMode.EAGER);
        nameFilter.addValueChangeListener(event -> gridListDataView.addFilter(supplier -> StringUtils.containsIgnoreCase(supplier.getName(), nameFilter.getValue())));
        filterRow.getCell(ItemNameColumn).setComponent(nameFilter);

        ComboBox<String> categoryFilter = new ComboBox<>();
        categoryFilter.setItems("Beer", "Wine", "Spirit", "Premix", "Misc");
        categoryFilter.setPlaceholder("Filter");
        categoryFilter.setClearButtonVisible(true); // This is not actually making button clear and im not sure why
        categoryFilter.setWidth("100%");
        categoryFilter.addValueChangeListener(event -> gridListDataView.addFilter(supplier -> {
            String filterValue = categoryFilter.getValue();
            if (filterValue != null) {
                return filterValue.equalsIgnoreCase(supplier.getCategory());
            }
            return true;
        }));
        filterRow.getCell(CategoryColumn).setComponent(categoryFilter);

        TextField quantityFilter = new TextField();
        quantityFilter.setPlaceholder("Filter");
        quantityFilter.setClearButtonVisible(true);
        quantityFilter.setWidth("100%");
        quantityFilter.setValueChangeMode(ValueChangeMode.EAGER);
        quantityFilter.addValueChangeListener(event -> gridListDataView.addFilter(supplier -> StringUtils.containsIgnoreCase(Integer.toString(supplier.getQuantity()), quantityFilter.getValue())));
        filterRow.getCell(QtyColumn).setComponent(quantityFilter);

        TextField salePriceFilter = new TextField();
        salePriceFilter.setPlaceholder("Filter");
        salePriceFilter.setClearButtonVisible(true);
        salePriceFilter.setWidth("100%");
        salePriceFilter.setValueChangeMode(ValueChangeMode.EAGER);
        salePriceFilter.addValueChangeListener(event -> gridListDataView.addFilter(supplier -> StringUtils.containsIgnoreCase(Integer.toString(supplier.getSalePrice()), salePriceFilter.getValue())));
        filterRow.getCell(SalePriceColumn).setComponent(salePriceFilter);

        TextField supplierFilter = new TextField();
        supplierFilter.setPlaceholder("Filter");
        supplierFilter.setClearButtonVisible(true); // This is not actually making button clear and im not sure why
        supplierFilter.setWidth("100%");
        supplierFilter.setValueChangeMode(ValueChangeMode.EAGER);
        supplierFilter.addValueChangeListener(event -> gridListDataView.addFilter(supplier -> StringUtils.containsIgnoreCase(supplier.getSupplier(), supplierFilter.getValue())));
        filterRow.getCell(SupplierColumn).setComponent(supplierFilter);
    }

    private void createSuppliers() {
        // iterate through getProducts() and create a Supplier object for each product
        List<ProductFrontend> products = getProducts();
        // return Arrays.asList(
        //         createSupplier("Dan Murphy", 375, 600, products.get(0)),
        //         createSupplier("Dan Murphy", 24, 42, products.get(1)),
        //         createSupplier("Dan Murphy", 24, 42, products.get(2)),
        //         createSupplier("Dan Murphy", 24, 42, products.get(3)),
        //         createSupplier("Dan Murphy", 24, 42, products.get(4))
        // );
        suppliers.add(createSupplier("Dan Murphy", 375, 600, products.get(0)));
        suppliers.add(createSupplier("Dan Murphy", 24, 42, products.get(1)));
        suppliers.add(createSupplier("Dan Murphy", 24, 42, products.get(2)));
        suppliers.add(createSupplier("Dan Murphy", 24, 42, products.get(3)));
        suppliers.add(createSupplier("Dan Murphy", 24, 42, products.get(4)));
    }

    private Supplier createSupplier(String Supplier, int Qty, int SalePrice, ProductFrontend p) {
        Supplier s = new Supplier();
        s.setProduct(p);
        s.setQty(Qty);
        s.setSalePrice(SalePrice);
        s.setSupplier(Supplier);

        return s;
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
};
