package com.business.application.views.suppliers;

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.business.application.views.inventory.ProductFrontend;
import com.vaadin.flow.component.textfield.IntegerField;



public class SupplierForm extends Dialog {
    private TextField supplierField = new TextField("Supplier");
    private IntegerField salePriceField = new IntegerField("Sale Price");
    private IntegerField qtyField = new IntegerField("Quantity");

    // Fields for the ProductFrontend
    private IntegerField itemIdField = new IntegerField("Item ID");
    private TextField nameField = new TextField("Name");
    private TextField categoryField = new TextField("Category");
    private IntegerField quantityField = new IntegerField("Quantity");
    private IntegerField capacityField = new IntegerField("Capacity");

    private Button saveButton = new Button("Save");

    public SupplierForm() {
        VerticalLayout layout = new VerticalLayout();
        layout.add(supplierField, salePriceField, qtyField, itemIdField, nameField, categoryField, quantityField, capacityField, saveButton);
        add(layout);
    }

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
}