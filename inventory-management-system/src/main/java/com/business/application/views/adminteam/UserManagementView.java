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
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
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
import java.util.Arrays;
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
        add(createToolbar(), grid);
    }

    private void configureGrid() {
        grid = new Grid<>(User.class, false);

        grid.addColumn(new ComponentRenderer<>(user -> {
            HorizontalLayout hl = new HorizontalLayout();
            Icon profileIcon;

            if (user.getRoles().contains(Role.ADMIN)){
            profileIcon = new Icon(VaadinIcon.USER_STAR);
            }
            else {profileIcon = new Icon(VaadinIcon.USER);}
            profileIcon.setSize("20px");

            Span span = new Span(user.getName());

            hl.add(profileIcon, span);
            return hl;
        }))
            .setHeader("User")
            .setSortable(true)
            .setComparator(User::getName);

        grid.addColumn(User::getUsername)
            .setHeader("Username")
            .setAutoWidth(true)
            .setSortable(true)
            .setComparator(User::getUsername);


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
        Icon searchIcon = VaadinIcon.SEARCH.create();
        usernameFilter.setSuffixComponent(searchIcon);
        usernameFilter.setPlaceholder("Search Members");
        usernameFilter.addValueChangeListener(e -> updateList());
        usernameFilter.setClearButtonVisible(true);
    }

    private void updateList() {
        grid.setItems(query -> userService.list(
            PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)),
            (Specification<User>) (root, criteriaQuery, criteriaBuilder) -> {
                if (usernameFilter.getValue() == null || usernameFilter.getValue().isEmpty()) {
                    return null; // No filter applied if the input is empty.
                }
                String filter = "%" + usernameFilter.getValue().toLowerCase() + "%";
                return criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), filter);
            }).stream());
    }

    private Component createToolbar() {
        Button addButton = new Button("Add User", click -> addUser());
        addButton.setIcon(new Icon("lumo", "plus"));
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout toolbar = new HorizontalLayout(usernameFilter, addButton);
        toolbar.addClassName("toolbar");
        toolbar.setAlignItems(FlexComponent.Alignment.END);
        return toolbar;
    }

    private void addUser() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Add New User");

        FormLayout formLayout = new FormLayout();

        // Email field for Username with validation
        EmailField usernameField = new EmailField("Email (@countingsorting.com)");
        usernameField.setRequiredIndicatorVisible(true);
        usernameField.setErrorMessage("Invalid email address");
        usernameField.setPattern(".+@countingsorting\\.com$");  // Regex pattern to ensure @cs.com
        usernameField.setClearButtonVisible(true);
        usernameField.addValueChangeListener(event -> {
            if (!usernameField.isInvalid()) {
                usernameField.setInvalid(!event.getValue().endsWith("@countingsorting.com"));
            }
        });

        TextField nameField = new TextField("Name");
        nameField.setRequiredIndicatorVisible(true);

        PasswordField passwordField = new PasswordField("Password");
        passwordField.setRequiredIndicatorVisible(true);

        // Role selector using Radio Buttons
        RadioButtonGroup<Role> roleGroup = new RadioButtonGroup<>();
        roleGroup.setLabel("Role");
        roleGroup.setItems(Role.values());
        roleGroup.setRequired(true);

        Button saveButton = new Button("Save", event -> {
            if (!usernameField.isInvalid() && !nameField.isEmpty() && !passwordField.isEmpty() && roleGroup.getValue() != null) {
                User newUser = new User();
                newUser.setUsername(usernameField.getValue());
                newUser.setName(nameField.getValue());
                newUser.setHashedPassword(new BCryptPasswordEncoder().encode(passwordField.getValue()));
                newUser.setRoles(new HashSet<>(Arrays.asList(roleGroup.getValue())));
                userService.update(newUser);
                updateList();
                dialog.close();
            } else {
                Notification.show("Please ensure all fields are correctly filled", 3000, Notification.Position.BOTTOM_CENTER);
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancel", event -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        formLayout.add(usernameField, nameField, passwordField, roleGroup);
        HorizontalLayout buttonsLayout = new HorizontalLayout(saveButton, cancelButton);
        buttonsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        dialog.add(formLayout, buttonsLayout);
        dialog.setModal(true);
        dialog.setDraggable(true);
        dialog.setResizable(false);

        dialog.open();
    }

}
