package com.business.application.views.suppliers;

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.business.application.views.inventory.ProductFrontend;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;



public class SupplierForm extends Dialog {
    private TextField supplierField = new TextField("Supplier");
    private IntegerField salePriceField = new IntegerField("Sale Price");
    private IntegerField qtyField = new IntegerField("Quantity Per Order");

    // Fields for the ProductFrontend
    private IntegerField itemIdField = new IntegerField("Item ID");
    private TextField nameField = new TextField("Name");
    private IntegerField quantityField = new IntegerField("Quantity");
    private IntegerField capacityField = new IntegerField("Capacity");
    private ComboBox<String> categoryField = new ComboBox<>("Category");
    private Button saveButton = new Button("Save");

    //public class SupplierForm extends Dialog {
    // ...

    public SupplierForm() {
        categoryField.setItems("Beer", "Wine", "Premix", "Spirit", "Misc");
        setWidth("20%");
        setHeight("80%");
        
        // Create a HorizontalLayout for the fields
        VerticalLayout fieldsLayout = new VerticalLayout();
        fieldsLayout.add(itemIdField, nameField, categoryField, salePriceField, qtyField, quantityField, capacityField, supplierField);
        fieldsLayout.setFlexGrow(1, supplierField, salePriceField, qtyField, itemIdField, nameField, categoryField, quantityField, capacityField);
        fieldsLayout.setAlignItems(Alignment.CENTER);

        
        // Create a HorizontalLayout for the save button and center it
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidth("100%");
        buttonLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        buttonLayout.add(saveButton);
        
        // Create a VerticalLayout for the entire form
        VerticalLayout layout = new VerticalLayout();
        layout.add(fieldsLayout, buttonLayout);
        
        add(layout);
    }

    // ...


    public Button getSaveButton() {
        return saveButton;
    }

    public Supplier getSupplier() {
        // Create a new Supplier object with the values from the fields
        Supplier supplier = new Supplier();
        supplier.setSupplier(supplierField.getValue());
        supplier.setSalePrice(salePriceField.getValue());
        supplier.setQty(qtyField.getValue());

        // Create a new ProductFrontend object with the values from the fields
        ProductFrontend product = new ProductFrontend(itemIdField.getValue(), nameField.getValue(), categoryField.getValue(), quantityField.getValue(), capacityField.getValue());
        supplier.setProduct(product);

        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        // Set the values of the fields based on the Supplier object
        supplierField.setValue(supplier.getSupplier());
        salePriceField.setValue(supplier.getSalePrice());
        qtyField.setValue(supplier.getQty());

        // Set the values of the fields based on the ProductFrontend object
        itemIdField.setValue(supplier.getItemID());
        nameField.setValue(supplier.getName());
        categoryField.setValue(supplier.getCategory());
        quantityField.setValue(supplier.getQuantity());
        capacityField.setValue(supplier.getCapacity());
    }
}