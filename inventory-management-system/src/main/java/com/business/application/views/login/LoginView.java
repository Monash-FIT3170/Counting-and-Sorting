package com.business.application.views.login;

import com.business.application.security.AuthenticatedUser;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.internal.RouteUtil;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@AnonymousAllowed
@PageTitle("Login")
@Route(value = "login")
public class LoginView extends LoginOverlay implements BeforeEnterObserver {

    private final AuthenticatedUser authenticatedUser;

    public LoginView(AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
        setAction(RouteUtil.getRoutePath(VaadinService.getCurrent().getContext(), getClass()));

        LoginI18n i18n = LoginI18n.createDefault();
        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("Counting & Sorting");
        i18n.getHeader().setDescription("Login using user or admin");
        i18n.setAdditionalInformation(null);
        setI18n(i18n);

        setForgotPasswordButtonVisible(true);
        setOpened(true);

        // Add forgot password listener to show email prompt dialog
        addForgotPasswordListener(e -> openForgotPasswordDialog());
    }

    private void openForgotPasswordDialog() {
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);

        EmailField emailField = new EmailField("Enter your email");
        Button submitButton = new Button("Submit", event -> {
            String email = emailField.getValue();
            if (email.isEmpty()) {
                Notification.show("Please enter a valid email address");
            } else {
                // Implement your password reset logic here
                // For example, send a password reset email
                dialog.close();
                Notification.show("You have been sent an email, please check your inbox to reset your password");
            }
        });

        Button cancelButton = new Button("Cancel", event -> dialog.close());

        VerticalLayout dialogLayout = new VerticalLayout(emailField, submitButton, cancelButton);
        dialog.add(dialogLayout);

        dialog.open();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authenticatedUser.get().isPresent()) {
            // Already logged in
            setOpened(false);
            event.forwardTo("");
        }

        setError(event.getLocation().getQueryParameters().getParameters().containsKey("error"));
    }
}