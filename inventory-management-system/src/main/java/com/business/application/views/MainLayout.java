package com.business.application.views;

import com.business.application.domain.User;
import com.business.application.security.AuthenticatedUser;
import com.business.application.views.admindashboard.AdminDashboardView;
import com.business.application.views.adminforecast.AdminForecastView;
import com.business.application.views.adminstock.AdminStockView;
import com.business.application.views.adminteam.UserManagementView;
import com.business.application.views.dashboard.DashboardView;
import com.business.application.views.forecast.ForecastView;
import com.business.application.views.inventory.InventoryView;
import com.business.application.views.requests.RequestsView;
import com.business.application.views.shoppinglists.ShoppingListsView;
import com.business.application.views.suppliers.SuppliersView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.theme.lumo.LumoIcon;
import com.vaadin.flow.theme.lumo.LumoUtility;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import org.vaadin.lineawesome.LineAwesomeIcon;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    private H1 viewTitle;
    private Image logoImage;

    private AuthenticatedUser authenticatedUser;
    private AccessAnnotationChecker accessChecker;

    public MainLayout(AuthenticatedUser authenticatedUser, AccessAnnotationChecker accessChecker) {
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;

        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
        
    }

    private void addHeaderContent() {
        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setWidthFull();
        mainLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        mainLayout.setAlignItems(Alignment.CENTER);
    
        // Left-aligned components
        HorizontalLayout leftLayout = new HorizontalLayout();
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");
    
        viewTitle = new H1();  // Set a title or leave it dynamic as needed
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        leftLayout.add(toggle, viewTitle);
        leftLayout.setAlignItems(Alignment.CENTER);
    
        // Right-aligned components
        HorizontalLayout rightLayout = new HorizontalLayout();
        rightLayout.add(createThemeToggleButton(), createUserMenu());
        // rightLayout.setSpacing(true);
        // rightLayout.getStyle().set("padding-right", "40px"); 

    
        mainLayout.add(leftLayout, rightLayout);
        mainLayout.expand(leftLayout);  

        addToNavbar(true, mainLayout);
    }

    private Component createUserMenu() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(true);
        layout.setAlignItems(Alignment.CENTER);

        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            Avatar avatar = new Avatar(user.getName());
            avatar.setThemeName("xsmall");

            MenuBar userMenu = new MenuBar();
            userMenu.setThemeName("tertiary-inline");

            MenuItem userDetails = userMenu.addItem(user.getName(), e -> {});
            userDetails.getSubMenu().addItem("Sign out", e -> authenticatedUser.logout());

            layout.add(avatar, userMenu);
        } else {
            Button loginButton = new Button("Sign in", VaadinIcon.SIGN_IN.create());
            loginButton.addClickListener(e -> UI.getCurrent().navigate("login"));
            loginButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
            layout.add(loginButton);
        }

        return layout;
}

    private Component createThemeToggleButton() {
        Icon themeIcon = new Icon("vaadin", "moon");
        themeIcon.getStyle().set("cursor", "pointer");

        Button themeToggleButton = new Button();
        themeToggleButton.setIcon(themeIcon);
        themeToggleButton.addClickListener(e -> toggleTheme(themeIcon));
        themeToggleButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        // Set button margin to align with user menu
        // Set button size to be 40 pixels to match the user menu
        themeToggleButton.getElement().getStyle().set("width", "40px").set("height", "40px");
        themeToggleButton.getElement().getStyle().set("margin-right", "20px");

        return themeToggleButton;
    }


    private void addDrawerContent() {
        // updateLogo(UI.getCurrent().getElement().getThemeList().contains(Lumo.DARK));
        
        Image logoImage = new Image("VAADIN/themes/liquor-store/img/logo-full.svg", "logo");
        logoImage.addClassName("logo");

        Header header = new Header(logoImage);

        Scroller scroller = new Scroller(createNavigation());
        
        addToDrawer(header, scroller);

        // Set Drawer content to dark mode
        header.getElement().setAttribute("theme", Lumo.DARK);

        if (this.hasClassName("overlay")) {
            scroller.getElement().setAttribute("theme", Lumo.DARK);
        }

        Page page = UI.getCurrent().getPage();

        page.retrieveExtendedClientDetails(details -> {
            if (details.getWindowInnerWidth() < 800) {
                scroller.getElement().setAttribute("theme", Lumo.LIGHT);
            } else {
                scroller.getElement().setAttribute("theme", Lumo.DARK); 
            }
        });

        page.addBrowserWindowResizeListener(
        event -> {
            if (event.getWidth() < 800) {
                scroller.getElement().setAttribute("theme", Lumo.LIGHT);
            } else {
                scroller.getElement().setAttribute("theme", Lumo.DARK); 
            }          
        });
    }

    private void updateLogo(boolean darkTheme) {
        String logoPath = darkTheme ? 
            "inventory-management-system/src/main/resources/META-INF/resources/img/CSLogoLight.png" : 
            "inventory-management-system/src/main/resources/META-INF/resources/img/CSLogoDark.png";
        StreamResource logoResource = new StreamResource(darkTheme ? "CSLogoLight.png" : "CSLogoDark.png",
            () -> {
                try {
                    return new ByteArrayInputStream(Files.readAllBytes(Paths.get(logoPath)));
                } catch (IOException e) {
                    return new ByteArrayInputStream(new byte[0]); // Fallback to empty content on error
                }
            });

        if (logoImage == null) {
            logoImage = new Image(logoResource, "Theme Logo");
        } else {
            logoImage.setSrc(logoResource);
        }
    }

    private Component createNavigation() {
        SideNav nav = new SideNav();
        
        // Manager - Menu
        if (accessChecker.hasAccess(DashboardView.class)) {
            nav.addItem(new SideNavItem("Dashboard", DashboardView.class, VaadinIcon.GRID_SMALL.create()));

        }

        if (accessChecker.hasAccess(InventoryView.class)) {
            nav.addItem(new SideNavItem("Inventory", InventoryView.class, VaadinIcon.LIST_UL.create()));

        }
        if (accessChecker.hasAccess(ShoppingListsView.class)) {
            nav.addItem(
                    new SideNavItem("Shopping Lists", ShoppingListsView.class, VaadinIcon.CART.create()));

        }
        if (accessChecker.hasAccess(ForecastView.class)) {
            nav.addItem(
                    new SideNavItem("Forecasts", ForecastView.class, VaadinIcon.BAR_CHART_H.create()));

        }


        // Admin - Menu
        if (accessChecker.hasAccess(AdminDashboardView.class)) {
            nav.addItem(new SideNavItem("Dashboard", AdminDashboardView.class,
            VaadinIcon.GRID_SMALL.create()));
        }
        if (accessChecker.hasAccess(SuppliersView.class)) {
            nav.addItem(new SideNavItem("Suppliers", SuppliersView.class, VaadinIcon.CLIPBOARD_USER.create()));

        }
        if (accessChecker.hasAccess(RequestsView.class)) {
            SideNavItem notificationNavItem = new SideNavItem("Requests", RequestsView.class, LumoIcon.BELL.create());

            // ADD A SUFFIX TO NOTIFICATION NAV ITEM
            int numRequests = 6;
            Span notificationCounter = new Span(String.valueOf(numRequests));
            notificationCounter.getElement().getThemeList().add("badge contrast pill");
            notificationCounter.getElement().setAttribute("aria-label", numRequests + " unread messages");

            notificationNavItem.setSuffixComponent(notificationCounter);
            
            nav.addItem(notificationNavItem);

        }
        if (accessChecker.hasAccess(AdminForecastView.class)) {
            nav.addItem(new SideNavItem("Forecasts", AdminForecastView.class,
            VaadinIcon.BAR_CHART_H.create()));
        }
        if (accessChecker.hasAccess(UserManagementView.class)) {
            nav.addItem(new SideNavItem("Team", UserManagementView.class,
                    VaadinIcon.USERS.create()));
        }
        // ADD LOGOUT SECTION
        SideNav logoutNav = new SideNav();
        
        logoutNav.addItem(new SideNavItem("Logout", "example", VaadinIcon.SIGN_OUT_ALT.create()));
        
        // WRAP NAV SECTIONS IN VERTICAL LAYOUT
        VerticalLayout navWrapper = new VerticalLayout(nav, logoutNav);
        navWrapper.setSpacing(true);
        navWrapper.setSizeUndefined();
        nav.setWidthFull();
        logoutNav.setWidthFull();

        navWrapper.addClassName("nav-wrapper");

        return navWrapper;
    }
    private void toggleTheme(Icon icon) {
        ThemeList themeList = UI.getCurrent().getElement().getThemeList();
        if (themeList.contains(Lumo.DARK)) {
            themeList.remove(Lumo.DARK);
            icon.getElement().setAttribute("icon", "vaadin:sun-o");
        } else {
            themeList.add(Lumo.DARK);
            icon.getElement().setAttribute("icon", "vaadin:moon");
        }
        // Immediately reflect the new theme state on the UI without full page refresh

        // UI.getCurrent().getPage().executeJs("document.documentElement.setAttribute('theme', $0)",
        //                                     themeList.contains(Lumo.DARK) ? "dark" : "light");
        //                                     updateLogo(themeList.contains(Lumo.DARK));
    }


    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
