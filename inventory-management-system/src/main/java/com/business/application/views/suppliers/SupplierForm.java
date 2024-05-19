package com.business.application.views.suppliers;

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.business.application.views.inventory.ProductFrontend;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;

import java.util.UUID;
import static java.lang.Math.abs;



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
        // Set up the FormLayout
        FormLayout formLayout = new FormLayout();

        itemIdField.setValue(generateItemId());
        itemIdField.setReadOnly(true);
        categoryField.setItems("Beer", "Wine", "Premix", "Spirit", "Misc");

        // Add fields to the form layout
        formLayout.addFormItem(itemIdField, "Item ID");
        formLayout.addFormItem(nameField, "Name");
        formLayout.addFormItem(quantityField, "Quantity");
        formLayout.addFormItem(categoryField, "Category");
        
        formLayout.addFormItem(salePriceField, "Sale Price");
        formLayout.addFormItem(qtyField, "Quantity Per Order");
        formLayout.addFormItem(capacityField, "Capacity");
        formLayout.addFormItem(supplierField, "Supplier");

        // Create a HorizontalLayout for the save button and center it
        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton);
        buttonLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        // Set the form layout to not be too wide
        formLayout.setMaxWidth("900px");
        this.add(formLayout);

    
        // Add FormLayout and button layout to the dialog
        this.add(formLayout, buttonLayout);
    }

    private int generateItemId() {
        return abs(UUID.randomUUID().hashCode());
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