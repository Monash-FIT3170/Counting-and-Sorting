package com.business.application.views.storelocator;

import com.business.application.domain.WebScrapedStore;
import com.business.application.services.WebScrapedStoreService;
import com.business.application.views.MainLayout;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@PageTitle("Supplier Locator")
@Route(value = "store-locator", layout = MainLayout.class)
@RolesAllowed("USER")
@CssImport("./styles/leaflet.css")
public class StoreLocatorView extends VerticalLayout {

    private final WebScrapedStoreService webscrapedStoreService;
    private final ObjectMapper objectMapper;

    @Autowired
    public StoreLocatorView(WebScrapedStoreService webscrapedStoreService, ObjectMapper objectMapper) {
        this.webscrapedStoreService = webscrapedStoreService;
        this.objectMapper = objectMapper;

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
        toolbar.addClassName("search-top-section");
        return toolbar;
    }

    private HorizontalLayout createHeader(String title, String subtitle) {
        H2 h2 = new H2(title);
        h2.addClassName("admin-dashboard-view-h2-1");

        Span span = new Span(subtitle);

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
        loadLeafletLibraryAndInitializeMap();
    }

    // Method to load Leaflet library and initialize the map with store markers
    private void loadLeafletLibraryAndInitializeMap() {
        // Fetch store data
        List<WebScrapedStore> stores = webscrapedStoreService.getAllWebscrapedStores();

        // Serialize store data to JSON using Jackson
        String storeDataJson = serializeStoreDataToJson(stores);

        // Pass store data to JavaScript and initialize the map
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
            "   navigator.geolocation.getCurrentPosition(function(position) {" +
            "       var latitude = position.coords.latitude;" +
            "       var longitude = position.coords.longitude;" +
            "       var map = L.map('map').setView([latitude, longitude], 13);" +
            "       L.tileLayer('https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png', {" +
            "           attribution: '&copy; <a href=\"https://carto.com/attributions\">CARTO</a>'," +
            "           subdomains: 'abcd'," +
            "           maxZoom: 19" +
            "       }).addTo(map);" +
            // Add current location marker
            "       var currentLocationMarker = L.divIcon({" +
            "           className: 'current-location-marker'," +
            "           iconSize: [25, 25]," +
            "           html: '<div class=\"outer-circle\"></div><div class=\"inner-circle\"></div>'" +
            "       });" +
            "       L.marker([latitude, longitude], { icon: currentLocationMarker }).addTo(map);" +
            // Add store markers
            "       var storeData = JSON.parse($0);" + // Parse store data JSON string
            "       storeData.forEach(function(store) {" +
            "           if (store.latitude && store.longitude) {" +
            "               var marker = L.marker([store.latitude, store.longitude]).addTo(map);" +
            "               var popupContent = '<b>' + store.title + '</b><br>' + store.address;" +
            "               marker.bindPopup(popupContent);" +
            "           }" +
            "       });" +
            "   }, function(error) {" +
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
            "       background-color: rgba(0, 123, 255, 0.5);" +
            "       border-radius: 50%;" +
            "       position: relative;" +
            "   }" +
            "   .current-location-marker .inner-circle {" +
            "       width: 10px;" +
            "       height: 10px;" +
            "       background-color: rgb(0, 123, 255);" +
            "       border-radius: 50%;" +
            "       position: absolute;" +
            "       top: 50%;" +
            "       left: 50%;" +
            "       transform: translate(-75%, -75%);" +
            "   }';" +
            "head.appendChild(style);",
            storeDataJson // Pass the serialized JSON string
        );
    }

    // Method to serialize store data to JSON using Jackson
    private String serializeStoreDataToJson(List<WebScrapedStore> stores) {
        try {
            // Create a DTO list to avoid serializing unnecessary fields
            List<StoreDTO> storeDTOs = stores.stream()
                .filter(store -> store.getLatitude() != null && store.getLongitude() != null)
                .map(store -> new StoreDTO(
                    store.getTitle(),
                    store.getAddress(),
                    store.getLatitude(),
                    store.getLongitude()
                ))
                .toList();

            return objectMapper.writeValueAsString(storeDTOs);
        } catch (JsonProcessingException e) {
            // Handle serialization error
            System.err.println("Error serializing store data to JSON: " + e.getMessage());
            return "[]"; // Return empty array on error
        }
    }

    // DTO class for store data
    private static class StoreDTO {
        private String title;
        private String address;
        private Double latitude;
        private Double longitude;

        public StoreDTO(String title, String address, Double latitude, Double longitude) {
            this.title = title;
            this.address = address;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        // Getters
        public String getTitle() {
            return title;
        }

        public String getAddress() {
            return address;
        }

        public Double getLatitude() {
            return latitude;
        }

        public Double getLongitude() {
            return longitude;
        }
    }
}
