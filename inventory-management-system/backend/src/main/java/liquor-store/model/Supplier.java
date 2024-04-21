package model;

import javax.persistence.*;

*//**
    * Represents a supplier in the system.
    *//*
@Entity
@Table(name = "Suppliers")
public class Supplier {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer supplierID;
  private String name;
  private String contactInfo;

  // Default constructor
  public Supplier() {}

  // constructor with parameters
  public Supplier(String name, String contactInfo) {
    this.name = name;
    this.contactInfo = contactInfo;
  }

  // Getters
  public Integer getSupplierID() {
    return supplierID;
  }

  public String getName() {
    return name;
  }

  public String getContactInfo() {
    return contactInfo;
  }

  // Setters
  public void setSupplierID(Integer supplierID) {
    this.supplierID = supplierID;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setContactInfo(String contactInfo) {
    this.contactInfo = contactInfo;
  }

  // toString method
  @Override
  public String toString() {
    return "Supplier{" +
        "supplierID=" + supplierID +
        ", name='" + name + '\'' +
        ", contactInfo='" + contactInfo + '\'' +
        '}';
  }
}
