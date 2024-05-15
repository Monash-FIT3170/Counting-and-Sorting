package com.business.application.views.adminteam;

import com.business.application.data.Role;
import com.business.application.data.User;
import com.business.application.services.UserService;
import com.business.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.data.domain.PageRequest;

import jakarta.annotation.security.RolesAllowed;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;


@PageTitle("User Management")
@Route(value = "user-management", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class UserManagementView extends VerticalLayout {

    private Grid<User> grid;
    private final UserService userService;
    private final TextField usernameFilter = new TextField();
    private PasswordField usernamePattern;

    @Autowired
    public UserManagementView(UserService userService) {
        this.userService = userService;
        setSizeFull();
        configureGrid();
        configureFilter();
        add(createToolbar(), usernameFilter, grid);
    }

    private void configureGrid() {
        grid = new Grid<>(User.class, false);
        grid.addColumn(User::getUsername).setHeader("Username").setAutoWidth(true);
        grid.addColumn(User::getName).setHeader("Name").setAutoWidth(true);

        // Adding a column to display roles
        grid.addColumn(user -> user.getRoles().stream()
            .map(Role::name)  // Assuming Role is an enum and you want to display the name of the enum
            .sorted()
            .collect(Collectors.joining(", ")))
            .setHeader("Roles")
            .setAutoWidth(true);

        grid.addComponentColumn(user -> createRemoveButton(grid, user)).setHeader("Actions");

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);
        grid.setHeightFull();
        updateList();
    }

    private Button createRemoveButton(Grid<User> grid, User user) {
        Button button = new Button(new Icon(VaadinIcon.TRASH), clickEvent -> {
            Dialog confirmationDialog = new Dialog();
            confirmationDialog.add(new Text("Are you sure you want to delete " + user.getName() + "?"));

            Button confirmButton = new Button("Confirm", event -> {
                userService.delete(user.getId());
                updateList();
                confirmationDialog.close();
                Notification.show("User deleted");
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

    private void configureFilter() {
        usernameFilter.setPlaceholder("Filter by username");
        usernameFilter.addValueChangeListener(e -> updateList());
        usernameFilter.setClearButtonVisible(true);
    }

    private void updateList() {
        grid.setItems(query -> userService.list(
            PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)),
            (Specification<User>) (root, q, criteriaBuilder) -> {
                if (usernameFilter.isEmpty()) {
                    return null;
                }
                String filter = "%" + usernamePattern.getValue().toLowerCase() + "%";
                return criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), filter);
            }).stream());
    }

    private Component createToolbar() {
        Button addButton = new Button("Add User", click -> addUser());
        addButton.setIcon(new Icon("lumo", "plus"));
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout toolbar = new HorizontalLayout(addButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }
    private void addUser() {
        // Create a new dialog
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Add New User");
    
        // Form layout to add components
        FormLayout formLayout = new FormLayout();
    
        // Field for Username
        TextField usernameField = new TextField("Username");
        usernameField.setRequiredIndicatorVisible(true);
    
        // Field for Name
        TextField nameField = new TextField("Name");
        nameField.setRequiredIndicatorVisible(true);
    
        // Password field
        PasswordField passwordDisplayField = new PasswordField("Password");
        passwordDisplayField.setRequiredIndicatorVisible(true);
    
        // Role selector
        CheckboxGroup<Role> rolesGroup = new CheckboxGroup<>();
        rolesGroup.setLabel("Roles");
        rolesGroup.setItems(Role.values()); // Set roles from the Role enum
    
        // Save button with click listener to save the new user
        Button saveButton = new Button("Save", event -> {
            if (!usernameField.isEmpty() && !nameField.isEmpty() && !passwordDisplayField.isEmpty() && !rolesGroup.isEmpty()) {
                User newUser = new User();
                newUser.setUsername(usernameField.getValue());
                newUser.setName(nameField.getValue());
                newUser.setHashedPassword(new BCryptPasswordEncoder().encode(passwordDisplayField.getValue()));
                newUser.setRoles(new HashSet<>(rolesGroup.getValue())); // Directly use EnumSet
                userService.update(newUser);
                updateList();
                dialog.close();
            } else {
                Notification.show("Please fill out all required fields", 3000, Notification.Position.BOTTOM_CENTER);
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    
        // Cancel button to close the dialog
        Button cancelButton = new Button("Cancel", event -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
    
        // Adding components to the form layout
        formLayout.add(usernameField, nameField, passwordDisplayField, rolesGroup);
    
        // Add save and cancel buttons to a horizontal layout
        HorizontalLayout buttonsLayout = new HorizontalLayout(saveButton, cancelButton);
        buttonsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
    
        // Add components to the dialog
        dialog.add(formLayout, buttonsLayout);
        dialog.setModal(true);
        dialog.setDraggable(true);
        dialog.setResizable(false);
    
        // Open the dialog
        dialog.open();
    }
    


    private void removeUser() {
        User selected = grid.asSingleSelect().getValue();
        if (selected != null) {
            userService.delete(selected.getId());
            updateList();
        }
    }
}
