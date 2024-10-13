package com.business.application.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "webscraped_stores")
public class WebScrapedStore extends AbstractEntity {

    private String title;
    private String address;
    private String supplier;

    @Column
    private String geolocation;

    @Column(nullable = true)
    private Double latitude;

    @Column(nullable = true)
    private Double longitude;

    // Getters and Setters

    public String getName() {
        return title;
    }

    public void setName(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getGeolocation() {
        return geolocation;
    }

    public void setGeolocation(String geolocation) {
        this.geolocation = geolocation;
        parseGeolocation();
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
        updateGeolocation();
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
        updateGeolocation();
    }

    // Helper methods to parse and update geolocation

    private void parseGeolocation() {
        if (this.geolocation != null && this.geolocation.startsWith("POINT")) {
            String point = geolocation.substring(6, geolocation.length() - 1);
            String[] coords = point.split(" ");
            if (coords.length == 2) {
                try {
                    this.longitude = Double.parseDouble(coords[0]);
                    this.latitude = Double.parseDouble(coords[1]);
                } catch (NumberFormatException e) {
                    // Handle parse error
                }
            }
        }
    }

    private void updateGeolocation() {
        if (this.latitude != null && this.longitude != null) {
            this.geolocation = String.format("POINT (%s %s)", this.longitude, this.latitude);
        }
    }

    @PrePersist
    public void prePersist() {
        if (this.geolocation == null && this.latitude != null && this.longitude != null) {
            updateGeolocation();
        }
    }
}