package com.business.application.views;

import com.business.application.data.User;
import com.business.application.security.AuthenticatedUser;
import com.business.application.views.admindashboard.AdminDashboardView;
import com.business.application.views.adminstock.AdminStockView;
import com.business.application.views.adminteam.UserManagementView;
import com.business.application.views.dashboard.DashboardView;
import com.business.application.views.forecast.ForecastView;
import com.business.application.views.inventory.InventoryView;
import com.business.application.views.requests.RequestsView;
import com.business.application.views.shoppinglists.ShoppingListsView;
import com.business.application.views.suppliers.SuppliersView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.theme.lumo.LumoUtility;
import java.io.ByteArrayInputStream;
import java.util.Optional;
import org.vaadin.lineawesome.LineAwesomeIcon;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    private H2 viewTitle;

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
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        H1 appName = new H1("Counting & Sorting");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();

        if (accessChecker.hasAccess(DashboardView.class)) {
            nav.addItem(new SideNavItem("Dashboard", DashboardView.class, LineAwesomeIcon.CHART_AREA_SOLID.create()));

        }

        if (accessChecker.hasAccess(InventoryView.class)) {
            nav.addItem(new SideNavItem("Inventory", InventoryView.class, LineAwesomeIcon.TH_SOLID.create()));

        }
        if (accessChecker.hasAccess(ForecastView.class)) {
            nav.addItem(
                    new SideNavItem("Forecast", ForecastView.class, LineAwesomeIcon.COLUMNS_SOLID.create()));

        }
        if (accessChecker.hasAccess(ShoppingListsView.class)) {
            nav.addItem(
                    new SideNavItem("Shopping Lists", ShoppingListsView.class, LineAwesomeIcon.COLUMNS_SOLID.create()));

        }
        if (accessChecker.hasAccess(RequestsView.class)) {
            nav.addItem(new SideNavItem("Requests", RequestsView.class, LineAwesomeIcon.LIST_SOLID.create()));

        }
        if (accessChecker.hasAccess(AdminDashboardView.class)) {
            nav.addItem(new SideNavItem("Admin Dashboard", AdminDashboardView.class,
                    LineAwesomeIcon.CHART_AREA_SOLID.create()));

        }
        if (accessChecker.hasAccess(SuppliersView.class)) {
            nav.addItem(new SideNavItem("Suppliers", SuppliersView.class, LineAwesomeIcon.TH_SOLID.create()));

        }
        if (accessChecker.hasAccess(AdminStockView.class)) {
            nav.addItem(new SideNavItem("Admin Stock", AdminStockView.class, LineAwesomeIcon.FILTER_SOLID.create()));

        }
        if (accessChecker.hasAccess(UserManagementView.class)) {
            nav.addItem(new SideNavItem("Team", UserManagementView.class,
                    LineAwesomeIcon.USERS_SOLID.create()));
        }

        

        return nav;
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
        UI.getCurrent().getPage().executeJs("document.documentElement.setAttribute('theme', $0)",
                                            themeList.contains(Lumo.DARK) ? "dark" : "light");
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();

            Avatar avatar = new Avatar(user.getName());
            avatar.setThemeName("xsmall");
            avatar.getElement().setAttribute("tabindex", "-1");

            MenuBar userMenu = new MenuBar();
            userMenu.setThemeName("tertiary-inline contrast");

            MenuItem userName = userMenu.addItem("");
            Div div = new Div();
            div.add(avatar);
            div.add(user.getName());
            div.add(new Icon("lumo", "dropdown"));
            div.getElement().getStyle().set("display", "flex");
            div.getElement().getStyle().set("align-items", "center");
            div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
            userName.add(div);
            userName.getSubMenu().addItem("Sign out", e -> {
                authenticatedUser.logout();
            });
                    // Icon for light/dark mode toggle
            Icon themeIcon = new Icon("vaadin", "moon");
            themeIcon.addClickListener(e -> {
                toggleTheme(themeIcon);
            });
            
            layout.add(themeIcon);

            layout.add(userMenu);
        } else {
            Anchor loginLink = new Anchor("login", "Sign in");
            layout.add(loginLink);
        }

        return layout;
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
