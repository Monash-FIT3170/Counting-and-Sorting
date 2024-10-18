package com.business.application.views.adminstore;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.router.Route;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.annotation.security.RolesAllowed;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.business.application.repository.*;
import com.business.application.services.*;
import com.business.application.domain.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.business.application.views.MainLayout;

@PageTitle("Stores")
@Route(value = "store-view", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class StoreView extends VerticalLayout {

    private final StoreService storeService;
    private final Grid<Store> storeGrid;
    private final UserService userService;

    @Autowired
    public StoreView(StoreService storeService,UserService userService) {
        addClassName("stores-view");
        
        this.storeService = storeService;
        this.userService = userService;
        this.storeGrid = new Grid<>(Store.class);

        add(createGrid());
        add(createToolbar(), storeGrid);
        updateStoreList();

    }

    private Grid<Store> createGrid() {
        storeGrid.setColumns("storeId", "location", "managerId");
        storeGrid.getColumnByKey("storeId").setHeader("Store ID");
        storeGrid.getColumnByKey("location").setHeader("Location");
        storeGrid.getColumnByKey("managerId").setHeader("Manager ID");
        

        return storeGrid;
    }

    private void updateStoreList() {
        storeGrid.setItems(storeService.findAllStores());

    }

    private void addStore() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Add New Store");

        FormLayout formLayout = new FormLayout();

    
        

        TextField locationField = new TextField("Location");
        locationField.setRequiredIndicatorVisible(true);

       

        
        RadioButtonGroup<Role> roleGroup = new RadioButtonGroup<>();
        roleGroup.setLabel("Role");
        roleGroup.setItems(Role.values());
        roleGroup.setRequired(true);

        Button saveButton = new Button("Save", event -> {
            if (!locationField.isEmpty()) {
                Store newStore = new Store();
                newStore.setLocation(locationField.getValue());
                storeService.saveStore(newStore);
                updateStoreList();
                dialog.close();
            } else {
                Notification.show("Please ensure all fields are correctly filled", 3000, Notification.Position.BOTTOM_CENTER);
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.getStyle().set("cursor", "pointer");

        Button cancelButton = new Button("Cancel", event -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.getStyle().set("cursor", "pointer");

        formLayout.add(locationField);
        HorizontalLayout buttonsLayout = new HorizontalLayout(saveButton, cancelButton);
        buttonsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        dialog.add(formLayout, buttonsLayout);
        dialog.setModal(true);
        dialog.setDraggable(true);
        dialog.setResizable(false);

        dialog.open();

    }
    private Component createToolbar() {
        Button addButton = new Button("Add New Store", click -> addStore());
        addButton.setIcon(new Icon("lumo", "plus"));
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout toolbar = new HorizontalLayout(addButton);
        toolbar.addClassName("toolbar");
        toolbar.setAlignItems(FlexComponent.Alignment.END);
        return toolbar;
    }


    
    

    
    
}