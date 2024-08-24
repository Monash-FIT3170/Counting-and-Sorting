package com.business.application.views.login;

import com.business.application.domain.Role;
import com.business.application.security.AuthenticatedUser;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.router.internal.RouteUtil;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;

@AnonymousAllowed
@PageTitle("Login")
@Route(value = "login")
@RouteAlias(value = "")
public class LoginView extends LoginOverlay implements BeforeEnterObserver {

    private final AuthenticatedUser authenticatedUser;

    public LoginView(AuthenticatedUser authenticatedUser) {
        this.getElement().setAttribute("theme", Lumo.DARK);
        
        this.authenticatedUser = authenticatedUser;
        setAction(RouteUtil.getRoutePath(VaadinService.getCurrent().getContext(), getClass()));

        LoginI18n i18n = LoginI18n.createDefault();
        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("Counting & Sorting");
        i18n.getHeader().setDescription("Login using user or admin");
        i18n.setAdditionalInformation(null);
        setI18n(i18n);

        setForgotPasswordButtonVisible(true);
        addForgotPasswordListener(e -> getUI().ifPresent(ui -> ui.navigate("forgot-password")));

        setOpened(true);
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
        ThemeList themeList = UI.getCurrent().getElement().getThemeList();
        if (authenticatedUser.get().isPresent()) {
            // Already logged in
            setOpened(false);
            // If user is ADMIN, navigate to admin page
            if (authenticatedUser.get().get().getRoles().contains(Role.ADMIN)) {
                // Set dark theme for admin
                themeList.add(Lumo.DARK);
                event.forwardTo("admin-dashboard");
            } else {
                event.forwardTo("dashboard");
            }
        }

        setError(event.getLocation().getQueryParameters().getParameters().containsKey("error"));
    }
}