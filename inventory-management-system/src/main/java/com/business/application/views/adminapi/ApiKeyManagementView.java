package com.business.application.views.adminapi;

import com.business.application.domain.ApiKey;
import com.business.application.services.ApiKeyService;
import com.business.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoUtility;

import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@PageTitle("API Key Management")
@Route(value = "api-management", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class ApiKeyManagementView extends VerticalLayout {

    private final ApiKeyService apiKeyService;
    private Grid<ApiKey> grid;
    private final TextField storeIdFilter = new TextField();

    @Autowired
    public ApiKeyManagementView(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
        setSizeFull();
        addClassName("admin-api-view");

        configureGrid();
        configureFilter();

        HorizontalLayout toolbar = createToolbar();
        toolbar.addClassName("admin-api-view-border");

        add(toolbar, grid);
    }

    private HorizontalLayout createToolbar() {
        Button addButton = new Button("Create New API Key", click -> addApiKey());
        addButton.setIcon(new Icon("lumo", "plus"));
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout toolbar = new HorizontalLayout(storeIdFilter, addButton);
        toolbar.setAlignItems(Alignment.END);
        return toolbar;
    }

    private void configureGrid() {
        grid = new Grid<>(ApiKey.class, false);
        grid.addClassNames(LumoUtility.Border.ALL, LumoUtility.BorderColor.CONTRAST_10);
    
        grid.addColumn(ApiKey::getKey)
            .setHeader("API Key")
            .setAutoWidth(true);
    
        grid.addColumn(ApiKey::getStoreId)
            .setHeader("Store ID")
            .setAutoWidth(true)
            .setSortable(true);
    
        grid.addColumn(ApiKey::getDescription)
            .setHeader("Description")
            .setAutoWidth(true);
    
        grid.addColumn(ApiKey::getAccessLevel)
            .setHeader("Access Level")
            .setAutoWidth(true);
    
        // Combine actions into a single horizontal layout
        grid.addComponentColumn(apiKey -> {
            HorizontalLayout actionsLayout = new HorizontalLayout();
            actionsLayout.add(createRemoveButton(grid, apiKey), createCopyButton(apiKey));
            return actionsLayout;
        }).setHeader("Actions").setAutoWidth(true);
    
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();
        updateList();
    }

    private Button createRemoveButton(Grid<ApiKey> grid, ApiKey apiKey) {
        Button button = new Button(new Icon(VaadinIcon.TRASH), clickEvent -> {
            Dialog confirmationDialog = new Dialog();
            confirmationDialog.add(new Text("Are you sure you want to delete this API key?"));

            Button confirmButton = new Button("Confirm", event -> {
                apiKeyService.delete(apiKey.getId());
                updateList();
                confirmationDialog.close();
                Notification.show("API Key deleted");
            });
            confirmButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

            Button cancelButton = new Button("Cancel", event -> confirmationDialog.close());
            cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

            HorizontalLayout dialogButtons = new HorizontalLayout(confirmButton, cancelButton);
            confirmationDialog.add(dialogButtons);
            confirmationDialog.open();
        });
        button.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
        return button;
    }

    private Button createCopyButton(ApiKey apiKey) {
        // Initialize the button with an icon
        Button button = new Button(new Icon(VaadinIcon.CLIPBOARD));
        
        // Set the click listener for the button
        button.addClickListener(clickEvent -> handleCopyButtonClick(button, apiKey));
        
        // Add consistent styling for the button
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
        button.getStyle().set("border", "1px solid var(--lumo-contrast-20pct)");
        button.getStyle().set("padding", "0.5em");
        
        return button;
    }
    
    private void handleCopyButtonClick(Button button, ApiKey apiKey) {
        // Get the API key and encode it in Base64
        String key = apiKey.getKey();
    
        // JavaScript snippet to copy the text to the clipboard
        String script = "navigator.clipboard.writeText('" + key + "').then(function() {" +
                        "   console.log('Copied to clipboard');" +
                        "}, function(err) {" +
                        "   console.error('Failed to copy: ', err);" +
                        "});";
    
        // Get the UI and execute the JavaScript code if the UI is present
        button.getUI().ifPresent(ui -> ui.getPage().executeJs(script));
    
        // Show a notification to the user
        Notification.show("API Key copied to clipboard");
    }
    private void configureFilter() {
        Icon searchIcon = VaadinIcon.SEARCH.create();
        storeIdFilter.setSuffixComponent(searchIcon);
        storeIdFilter.setPlaceholder("Search Store ID");
        storeIdFilter.addValueChangeListener(e -> updateList());
        storeIdFilter.setClearButtonVisible(true);
    }

    private void updateList() {
        grid.setItems(query -> apiKeyService.list(
            PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)),
            (root, criteriaQuery, criteriaBuilder) -> {
                if (storeIdFilter.getValue() == null || storeIdFilter.getValue().isEmpty()) {
                    return null;
                }
                String filter = "%" + storeIdFilter.getValue().toLowerCase() + "%";
                return criteriaBuilder.like(criteriaBuilder.lower(root.get("storeId")), filter);
            }).stream());
    }

    private void addApiKey() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Create New API Key");

        FormLayout formLayout = new FormLayout();

        TextField storeIdField = new TextField("Store ID");
        storeIdField.setRequiredIndicatorVisible(true);

        TextField descriptionField = new TextField("Description");
        descriptionField.setRequiredIndicatorVisible(true);

        RadioButtonGroup<String> accessLevelGroup = new RadioButtonGroup<>();
        accessLevelGroup.setLabel("Access Level");
        accessLevelGroup.setItems("Admin", "Manager");
        accessLevelGroup.setRequired(true);

        Button generateButton = new Button("Generate API Key", event -> {
            if (!storeIdField.isEmpty() && !descriptionField.isEmpty() && accessLevelGroup.getValue() != null) {
                ApiKey newApiKey = new ApiKey();
                newApiKey.setStoreId(storeIdField.getValue());
                newApiKey.setDescription(descriptionField.getValue());
                newApiKey.setAccessLevel(accessLevelGroup.getValue());
                newApiKey.setKey(generateRandomApiKey());
                apiKeyService.save(newApiKey);
                updateList();
                Notification.show("API Key generated: " + newApiKey.getKey(), 3000, Notification.Position.BOTTOM_CENTER);
                dialog.close();
            } else {
                Notification.show("Please ensure all fields are correctly filled", 3000, Notification.Position.BOTTOM_CENTER);
            }
        });
        generateButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancel", event -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        formLayout.add(storeIdField, descriptionField, accessLevelGroup);
        HorizontalLayout buttonsLayout = new HorizontalLayout(generateButton, cancelButton);
        buttonsLayout.setJustifyContentMode(JustifyContentMode.END);

        dialog.add(formLayout, buttonsLayout);
        dialog.setModal(true);
        dialog.setDraggable(true);
        dialog.setResizable(false);

        dialog.open();
    }

    private String generateRandomApiKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[24];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
