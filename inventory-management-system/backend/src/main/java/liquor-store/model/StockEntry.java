package model;

import javax.persistence.*;
import java.util.Date;

*//**
    * Represents a stock entry in the system.
    *//*
@Entity
@Table(name = "StockEntries")
public class StockEntry {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer entryID;

  @ManyToOne // Not sure if this is right
  @JoinColumn(name = "InventoryItemID", referencedColumnName = "InventoryItemID")
  private InventoryItem inventoryItem;

  private Integer quantityChange;
  private Date dateModified;

  // Default constructor
  public StockEntry() {}

  // constructor with parameters
  public StockEntry(InventoryItem inventoryItem, Integer quantityChange, LocalDateTime dateModified) {
    this.inventoryItem = inventoryItem;
    this.quantityChange = quantityChange;
    this.dateModified = dateModified;
  }

  // Getters
  public Integer getEntryID() {
    return entryID;
  }

  public InventoryItem getInventoryItem() {
    return inventoryItem;
  }

  public Integer getQuantityChange() {
    return quantityChange;
  }

  public LocalDateTime getDateModified() {
    return dateModified;
  }

  // Setters
  public void setEntryID(Integer entryID) {
    this.entryID = entryID;
  }

  public void setInventoryItem(InventoryItem inventoryItem) {
    this.inventoryItem = inventoryItem;
  }

  public void setQuantityChange(Integer quantityChange) {
    this.quantityChange = quantityChange;
  }

  public void setDateModified(LocalDateTime dateModified) {
    this.dateModified = dateModified;
  }
  // toString method
  @Override
  public String toString() {
    return "StockEntry{" +
        "entryID=" + entryID +
        ", inventoryItem=" + (inventoryItem != null ? inventoryItem.getInventoryItemID() : "null") +
        ", quantityChange=" + quantityChange +
        ", dateModified=" + dateModified +
        '}';
  }
}
