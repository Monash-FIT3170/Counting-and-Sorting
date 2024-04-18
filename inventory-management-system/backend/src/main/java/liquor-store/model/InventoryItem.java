package model;

import javax.persistence.*;
import java.util.Date;

@Entity
public class InventoryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int inventoryItemID;

    @ManyToOne
    @JoinColumn(name = "storeID")
    private Store store;

    @ManyToOne
    @JoinColumn(name = "productID")
    private Product product;

    private int quantity;
    private Date manufactureDate;
    private Date expirationDate;
    private int capacity;

    // Constructors, getters, and setters
}