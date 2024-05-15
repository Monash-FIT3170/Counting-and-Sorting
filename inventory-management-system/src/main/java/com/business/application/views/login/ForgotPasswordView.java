package com.business.application.views.login;

import com.business.application.domain.User;
import com.business.application.services.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@AnonymousAllowed
@PageTitle("Forgot Password")
@Route(value = "forgot-password")
public class ForgotPasswordView extends VerticalLayout {

    private final UserService userService;

    @Autowired
    public ForgotPasswordView(UserService userService) {
        this.userService = userService;

        H1 title = new H1("Forgot Password");
        TextField usernameField = new TextField("Username");
        usernameField.setRequired(true);

        TextField nameField = new TextField("Name");
        nameField.setRequired(true);

        PasswordField newPasswordField = new PasswordField("New Password");
        newPasswordField.setRequired(true);

        Button resetButton = new Button("Reset Password", event -> {
            String username = usernameField.getValue();
            String name = nameField.getValue();
            String newPassword = newPasswordField.getValue();

            if (username.isEmpty() || name.isEmpty() || newPassword.isEmpty()) {
                Notification.show("All fields are required", 3000, Notification.Position.MIDDLE);
                return;
            }

            Optional<User> userOpt = userService.validateUserDetails(name, username);
            if (userOpt.isPresent()) {
                userService.resetPassword(userOpt.get(), newPassword);
                Notification.show("Password reset successfully", 3000, Notification.Position.MIDDLE);
                getUI().ifPresent(ui -> ui.navigate("login"));
            } else {
                Notification.show("Invalid details", 3000, Notification.Position.MIDDLE);
            }
        });
        resetButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        add(title, usernameField, nameField, newPasswordField, resetButton);
        setAlignItems(Alignment.CENTER);
    }
}
