package com.business.application.views.storelocator;

import com.business.application.views.MainLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;
import jakarta.annotation.security.RolesAllowed;
import com.vaadin.flow.component.AttachEvent;

@PageTitle("Supplier Locator")
@Route(value = "store-locator", layout = MainLayout.class)
@RolesAllowed("USER")
@CssImport("./styles/leaflet.css")  // Updated to point to local CSS file
public class StoreLocatorView extends VerticalLayout {

    public StoreLocatorView() {
        // Create the toolbar for search and filter options
        HorizontalLayout toolbar = createToolbar();
        addClassName("store-locator-view");

        // Main layout
        VerticalLayout mainLayout = new VerticalLayout(toolbar);
        mainLayout.setSizeFull();
        mainLayout.setPadding(false);
        mainLayout.setSpacing(false);

        // Add the map container
        Div mapContainer = new Div();
        mapContainer.setId("map"); // Set the id to reference in JavaScript
        mapContainer.setWidthFull();
        mapContainer.setHeight("500px"); // Adjust height as needed
        mapContainer.getElement().getStyle().set("margin-top", "16px");

        // Add the map below the toolbar
        mainLayout.add(mapContainer);

        // Add everything to the view
        add(mainLayout);
    }

    private HorizontalLayout createToolbar() {
        // Create the search bar
        TextField searchBar = new TextField();
        searchBar.addClassName("toolbar-search-bar");
        searchBar.setPlaceholder("Search Suppliers");
        searchBar.setSuffixComponent(VaadinIcon.SEARCH.create());
        searchBar.setWidth("300px");

        // Add filters to search bar
        searchBar.setValueChangeMode(ValueChangeMode.EAGER);

        // Create the layout for the label and search field
        HorizontalLayout toolbar = createHeader("SUPPLIER LOCATOR", "");
        toolbar.add(searchBar);
        toolbar.setWidthFull();
        toolbar.setHeight("50px");
        toolbar.setAlignItems(FlexComponent.Alignment.CENTER);
        toolbar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        toolbar.addClassName(Padding.LARGE);
        toolbar.addClassName("search-top-section");
        return toolbar;
    }

    private HorizontalLayout createHeader(String title, String subtitle) {
        H2 h2 = new H2(title);
        h2.addClassName("admin-dashboard-view-h2-1");
        h2.addClassNames(FontSize.XLARGE, Margin.NONE);

        Span span = new Span(subtitle);
        span.addClassNames(TextColor.SECONDARY, FontSize.XSMALL);

        VerticalLayout column = new VerticalLayout(h2, span);
        column.setPadding(false);
        column.setSpacing(false);

        HorizontalLayout header = new HorizontalLayout(column);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setSpacing(false);
        header.setWidthFull();
        return header;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        loadLeafletLibrary(); // Load Leaflet library first
    }

    // Method to load Leaflet CSS and JS
    private void loadLeafletLibrary() {
        getElement().executeJs(
            "var head = document.head;" +
            // Load Leaflet CSS
            "var link = document.createElement('link');" +
            "link.rel = 'stylesheet';" +
            "link.href = 'https://unpkg.com/leaflet@1.7.1/dist/leaflet.css';" +
            "head.appendChild(link);" +
            // Load Leaflet JS
            "var script = document.createElement('script');" +
            "script.src = 'https://unpkg.com/leaflet@1.7.1/dist/leaflet.js';" +
            "script.onload = function() {" +
            "   navigator.geolocation.getCurrentPosition(function(position) {" + // Get user's current location
            "       var latitude = position.coords.latitude;" +
            "       var longitude = position.coords.longitude;" +
            "       var map = L.map('map').setView([latitude, longitude], 13);" + // Center map on user's location
            "       L.tileLayer('https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png', {" +
            "           attribution: '&copy; <a href=\"https://carto.com/attributions\">CARTO</a>'," +
            "           subdomains: 'abcd'," +
            "           maxZoom: 19" +
            "       }).addTo(map);" +
            // Create a custom divIcon for the marker
            "       var currentLocationMarker = L.divIcon({" +
            "           className: 'current-location-marker'," +
            "           iconSize: [25, 25]," + // Adjust the size of the marker
            "           html: '<div class=\"outer-circle\"></div><div class=\"inner-circle\"></div>'" +
            "       });" +
            // Add the custom marker to the map
            "       L.marker([latitude, longitude], { icon: currentLocationMarker }).addTo(map);" +
            "   }, function(error) {" + // Handle error case for geolocation
            "       alert('Geolocation failed: ' + error.message);" +
            "   });" +
            "};" +
            "head.appendChild(script);" +
            // Add custom CSS for the marker
            "var style = document.createElement('style');" +
            "style.innerHTML = " +
            "   '.current-location-marker .outer-circle {" +
            "       width: 20px;" +
            "       height: 20px;" +
            "       background-color: rgba(0, 123, 255, 0.5);" + // Blue outer circle with some transparency
            "       border-radius: 50%;" +
            "       position: relative;" +
            "   }" +
            "   .current-location-marker .inner-circle {" +
            "       width: 10px;" +
            "       height: 10px;" +
            "       background-color: rgb(0, 123, 255);" + // Solid blue inner dot
            "       border-radius: 50%;" +
            "       position: absolute;" +
            "       top: 50%;" +
            "       left: 50%;" +
            "       transform: translate(-75%, -75%);" +
            "   }';" +
            "head.appendChild(style);"
        );
    }
    
    
    
}
