package com.business.application.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "webscraped_stores")
public class WebScrapedStore extends AbstractEntity {

    private String title;
    private String address;
    private String supplier;

    @Column(nullable = true, length = 255)
    private String appendedDate;

    @Column(nullable = true)
    private Double latitude;

    @Column(nullable = true)
    private Double longitude;

    // Getters and Setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
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

    public String getAppendedDate() {
        return appendedDate;
    }

    public void setAppendedDate(String appendedDate) {
        this.appendedDate = appendedDate;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    // Optional: Override toString for better debugging
    @Override
    public String toString() {
        return "WebScrapedStore{" +
                "id=" + getId() +
                ", title='" + title + '\'' +
                ", address='" + address + '\'' +
                ", supplier='" + supplier + '\'' +
                ", appendedDate='" + appendedDate + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
