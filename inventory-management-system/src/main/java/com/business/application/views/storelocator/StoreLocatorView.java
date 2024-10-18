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
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@PageTitle("Supplier Locator")
@Route(value = "store-locator", layout = MainLayout.class)
@RolesAllowed("USER")
@CssImport("./styles/leaflet.css")
public class StoreLocatorView extends VerticalLayout {

    private final WebScrapedStoreService webscrapedStoreService;
    private final ObjectMapper objectMapper;
    private Object mapInstance = null;
    private List<Object> markers = new ArrayList<>(); // To store marker references

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

        // Set eager value change mode for real-time search
        searchBar.setValueChangeMode(ValueChangeMode.EAGER);

        // Add a listener to perform the search on value change
        searchBar.addValueChangeListener(event -> {
            String searchQuery = event.getValue().trim().toLowerCase();
            filterAndDisplayStores(searchQuery); // Filter and update map markers
        });

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

    private void filterAndDisplayStores(String searchQuery) {
        // Fetch the store data
        List<WebScrapedStore> stores = webscrapedStoreService.getAllWebscrapedStores();

        // Filter stores based on the search query (by title)
        List<WebScrapedStore> filteredStores = stores.stream()
                .filter(store -> store.getTitle().toLowerCase().contains(searchQuery))
                .toList();

        // Serialize filtered store data to JSON
        String filteredStoreDataJson = serializeStoreDataToJson(filteredStores);

        // Pass the filtered store data to JavaScript and update the map markers
        getElement().executeJs(
                "if (window.markers && window.markers.length) {" + // Check if markers exist and array is not empty
                        "   window.markers.forEach(function(marker) {" +
                        "       window.map.removeLayer(marker);" + // Remove existing markers
                        "   });" +
                        "   window.markers = [];" + // Clear the marker array
                        "}" +
                        "window.markers = window.markers || [];" + // Ensure markers array is initialized
                        // Add new markers for the filtered stores
                        "var storeData = JSON.parse($0);" +
                        "storeData.forEach(function(store) {" +
                        "   if (store.latitude && store.longitude) {" +
                        "       var markerColor;" +
                        "       if (store.title.toLowerCase().includes('bws')) {" +
                        "           markerColor = 'orange';" + // Set orange color for BWS
                        "       } else if (store.title.toLowerCase().includes('liquorland')) {" +
                        "           markerColor = 'black';" + // Set black color for Liquorland
                        "       } else if (store.title.toLowerCase().includes('dan murphy')) {" +
                        "           markerColor = 'green';" + // Set green color for Dan Murphy's
                        "       } else {" +
                        "           markerColor = 'blue';" + // Default color if no match
                        "       }" +
                        "       var markerIcon = L.divIcon({" +
                        "           className: 'custom-marker', " +
                        "           html: '<div class=\"marker-circle\" style=\"background-color:' + markerColor + '\"></div>',"
                        +
                        "           iconSize: [25, 25]," +
                        "           iconAnchor: [12, 12]," +
                        "           popupAnchor: [0, -12]" +
                        "       });" +
                        "       var marker = L.marker([store.latitude, store.longitude], {icon: markerIcon}).addTo(window.map);"
                        +
                        "       var popupContent = '<b>' + store.title + '</b><br>' + store.address;" +
                        "       marker.bindPopup(popupContent);" +
                        "       window.markers.push(marker);" + // Add new markers to the array
                        "   }" +
                        "});",
                filteredStoreDataJson // Pass the serialized JSON string
        );
    }

    private HorizontalLayout createHeader(String title, String subtitle) {
        // Header container
        HorizontalLayout header = new HorizontalLayout();
        
        header.addClassName("search-top-section");
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.addClassNames(
            LumoUtility.Padding.Left.XLARGE,
            LumoUtility.Padding.Right.XLARGE
            );
        
        H6 title_txt = new H6(title);
        
        title_txt.addClassNames(LumoUtility.TextColor.SECONDARY);

        header.add(title_txt);
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

        // Check if the map is already initialized
        if (mapInstance == null) {
            // Pass store data to JavaScript and initialize the map
            mapInstance = getElement().executeJs(
                    "var head = document.head;" +
                            "var link = document.createElement('link');" +
                            "link.rel = 'stylesheet';" +
                            "link.href = 'https://unpkg.com/leaflet@1.7.1/dist/leaflet.css';" +
                            "head.appendChild(link);" +
                            "var script = document.createElement('script');" +
                            "script.src = 'https://unpkg.com/leaflet@1.7.1/dist/leaflet.js';" +
                            "script.onload = function() {" +
                            "   navigator.geolocation.getCurrentPosition(function(position) {" +
                            "       var latitude = position.coords.latitude;" +
                            "       var longitude = position.coords.longitude;" +
                            "       window.map = L.map('map').setView([latitude, longitude], 13);" +
                            "       L.tileLayer('https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png', {" +
                            "           attribution: '&copy; <a href=\"https://carto.com/attributions\">CARTO</a>'," +
                            "           subdomains: 'abcd'," +
                            "           maxZoom: 19" +
                            "       }).addTo(window.map);" +
                            "       var currentLocationMarker = L.divIcon({" +
                            "           className: 'current-location-marker'," +
                            "           iconSize: [25, 25]," +
                            "           html: '<div class=\"outer-circle\"></div><div class=\"inner-circle\"></div>'" +
                            "       });" +
                            "       window.currentLocation = L.marker([latitude, longitude], { icon: currentLocationMarker }).addTo(window.map);"
                            +
                            "       window.markers = window.markers || [];" +
                            "       var storeData = JSON.parse($0);" +
                            "       storeData.forEach(function(store) {" +
                            "           if (store.latitude && store.longitude) {" +
                            "               var markerColor;" +
                            "               if (store.title.toLowerCase().includes('bws')) {" +
                            "                   markerColor = 'orange';" +
                            "               } else if (store.title.toLowerCase().includes('liquorland')) {" +
                            "                   markerColor = 'black';" +
                            "               } else if (store.title.toLowerCase().includes('dan murphy')) {" +
                            "                   markerColor = 'green';" +
                            "               } else {" +
                            "                   markerColor = 'blue';" +
                            "               }" +
                            "               var markerIcon = L.divIcon({" +
                            "                   className: 'custom-marker', " +
                            "                   html: '<div class=\"marker-circle\" style=\"background-color:' + markerColor + '\"></div>',"
                            +
                            "                   iconSize: [25, 25]," +
                            "                   iconAnchor: [12, 12]," +
                            "                   popupAnchor: [0, -12]" +
                            "               });" +
                            "               var marker = L.marker([store.latitude, store.longitude], {icon: markerIcon}).addTo(window.map);"
                            +
                            "               var popupContent = '<b>' + store.title + '</b><br>' + store.address;" +
                            "               marker.bindPopup(popupContent);" +
                            "               window.markers.push(marker);" +
                            "           }" +
                            "       });" +
                            "   }, function(error) {" +
                            "       alert('Geolocation failed: ' + error.message);" +
                            "   });" +
                            "};" +
                            "head.appendChild(script);" +
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
                            store.getLongitude()))
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
