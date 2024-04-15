CREATE DATABASE IF NOT EXISTS liquor_store;
USE liquor_store;

-- Creating Users Table,
CREATE TABLE IF NOT EXISTS Users (
    UserID INT AUTO_INCREMENT PRIMARY KEY,
    Username VARCHAR(255) NOT NULL UNIQUE,
    Password VARCHAR(255) NOT NULL, -- Passwords must be stored hashed
    Email VARCHAR(255) NOT NULL UNIQUE,
    Role ENUM('Admin', 'Manager') NOT NULL
);

-- Creating Store Table
CREATE TABLE IF NOT EXISTS Stores (
    StoreID INT AUTO_INCREMENT PRIMARY KEY,
    Location VARCHAR(255) NOT NULL,
    ManagerID INT UNIQUE,
    FOREIGN KEY (ManagerID) REFERENCES Users(UserID)
);

-- Creating Product Table
CREATE TABLE IF NOT EXISTS Products (
    ProductID INT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(255) NOT NULL,
    SalePrice DECIMAL(10, 2) NOT NULL,
    Category VARCHAR(255) NOT NULL,
    Description TEXT
);

-- Creating Supplier Table
CREATE TABLE IF NOT EXISTS Suppliers (
    SupplierID INT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(255) NOT NULL,
    ContactInfo VARCHAR(255)
);

-- Creating InventoryItem Table
CREATE TABLE IF NOT EXISTS InventoryItems (
    InventoryItemID INT AUTO_INCREMENT PRIMARY KEY,
    StoreID INT NOT NULL,
    ProductID INT NOT NULL,
    Quantity INT NOT NULL,
    ManufactureDate DATE,
    ExpirationDate DATE,
    Capacity INT NOT NULL,
    FOREIGN KEY (StoreID) REFERENCES Stores(StoreID),
    FOREIGN KEY (ProductID) REFERENCES Products(ProductID)
);

-- Creating StockEntry Table
CREATE TABLE IF NOT EXISTS StockEntries (
    EntryID INT AUTO_INCREMENT PRIMARY KEY,
    InventoryItemID INT NOT NULL,
    QuantityChange INT NOT NULL,
    DateModified DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (InventoryItemID) REFERENCES InventoryItems(InventoryItemID)
);

-- Creating ShoppingList Table
CREATE TABLE IF NOT EXISTS ShoppingLists (
    ListID INT AUTO_INCREMENT PRIMARY KEY,
    ManagerID INT NOT NULL,
    StoreID INT NOT NULL,
    DateCreated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ManagerID) REFERENCES Users(UserID),
    FOREIGN KEY (StoreID) REFERENCES Stores(StoreID)
);

-- Creating ListProduct Table
CREATE TABLE IF NOT EXISTS ListProducts (
    ListProductID INT AUTO_INCREMENT PRIMARY KEY,
    ListID INT NOT NULL,
    ProductID INT NOT NULL,
    RequiredQty INT NOT NULL,
    FOREIGN KEY (ListID) REFERENCES ShoppingLists(ListID),
    FOREIGN KEY (ProductID) REFERENCES Products(ProductID)
);

-- Creating ProductSupplier Table
CREATE TABLE IF NOT EXISTS ProductSuppliers (
    ProductSupplierID INT AUTO_INCREMENT PRIMARY KEY,
    ProductID INT NOT NULL,
    SupplierID INT NOT NULL,
    CostPrice DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (ProductID) REFERENCES Products(ProductID),
    FOREIGN KEY (SupplierID) REFERENCES Suppliers(SupplierID)
);
