-- Insert Suppliers
INSERT INTO Suppliers (Name, ContactInfo) VALUES ('BWS', 'bws_contact@example.com');
INSERT INTO Suppliers (Name, ContactInfo) VALUES ('Dan Murphys', 'danmurphys_contact@example.com');

-- Insert Users (Managers and one Admin)
INSERT INTO Users (Username, Password, Email, Role) VALUES ('ManagerOne', 'hashed_password1', 'manager1@example.com', 'Manager');
INSERT INTO Users (Username, Password, Email, Role) VALUES ('ManagerTwo', 'hashed_password2', 'manager2@example.com', 'Manager');
INSERT INTO Users (Username, Password, Email, Role) VALUES ('ManagerThree', 'hashed_password3', 'manager3@example.com', 'Manager');
INSERT INTO Users (Username, Password, Email, Role) VALUES ('AdminUser', 'hashed_password4', 'admin@example.com', 'Admin');

-- Insert Stores with Managers
INSERT INTO Stores (Location, ManagerID) VALUES ('Location1', (SELECT UserID FROM Users WHERE Username = 'ManagerOne'));
INSERT INTO Stores (Location, ManagerID) VALUES ('Location2', (SELECT UserID FROM Users WHERE Username = 'ManagerTwo'));
INSERT INTO Stores (Location, ManagerID) VALUES ('Location3', (SELECT UserID FROM Users WHERE Username = 'ManagerThree'));

-- Insert Products from the image and some additional products
-- Please hash the passwords appropriately in a real scenario
INSERT INTO Products (Name, SalePrice, Category, Description) VALUES ('-196 Can 10 x 330ml', 19.99, 'Beer', 'Pack of 10, 330ml each');
INSERT INTO Products (Name, SalePrice, Category, Description) VALUES ('Smirnoff Vodka 700ml', 29.99, 'Vodka', '700ml Bottle');
INSERT INTO Products (Name, SalePrice, Category, Description) VALUES ('Great Northern', 24.99, 'Beer', 'Supreme taste beer');
-- Additional products
INSERT INTO Products (Name, SalePrice, Category, Description) VALUES ('Johnnie Walker Black Label', 45.99, 'Whiskey', '700ml Bottle');
INSERT INTO Products (Name, SalePrice, Category, Description) VALUES ('Jack Daniels 700ml', 39.99, 'Whiskey', '700ml Bottle');
INSERT INTO Products (Name, SalePrice, Category, Description) VALUES ('Bacardi White Rum 700ml', 22.99, 'Rum', '700ml Bottle');

-- Insert Inventory Items for Store 1
INSERT INTO InventoryItems (StoreID, ProductID, Quantity, ManufactureDate, Capacity) VALUES
(1, (SELECT ProductID FROM Products WHERE Name = '-196 Can 10 x 330ml'), 12, '2023-01-01', 3300),
(1, (SELECT ProductID FROM Products WHERE Name = 'Smirnoff Vodka 700ml'), 26, '2023-01-15', 18200),
(1, (SELECT ProductID FROM Products WHERE Name = 'Jack Daniels 700ml'), 10, '2023-02-01', 7000),
(1, (SELECT ProductID FROM Products WHERE Name = 'Bacardi White Rum 700ml'), 18, '2023-02-15', 12600);

-- Insert Inventory Items for Store 2
INSERT INTO InventoryItems (StoreID, ProductID, Quantity, ManufactureDate, Capacity) VALUES
(2, (SELECT ProductID FROM Products WHERE Name = 'Great Northern'), 21, '2023-01-10', 21000),
(2, (SELECT ProductID FROM Products WHERE Name = '-196 Can 10 x 330ml'), 12, '2023-01-20', 3300);

-- Insert Inventory Items for Store 3 with some additional items
INSERT INTO InventoryItems (StoreID, ProductID, Quantity, ManufactureDate, Capacity) VALUES
(3, (SELECT ProductID FROM Products WHERE Name = 'Johnnie Walker Black Label'), 15, '2023-03-01', 10500),
(3, (SELECT ProductID FROM Products WHERE Name = 'Jack Daniels 700ml'), 10, '2023-03-15', 7000),
(3, (SELECT ProductID FROM Products WHERE Name = 'Bacardi White Rum 700ml'), 18, '2023-04-01', 12600);

-- Insert ProductSuppliers
-- Assuming BWS SupplierID = 1, Dan Murphys SupplierID = 2

-- Supplier 1 (BWS) provides these products
INSERT INTO ProductSuppliers (ProductID, SupplierID, CostPrice) VALUES
((SELECT ProductID FROM Products WHERE Name = '-196 Can 10 x 330ml'), 1, 15.99),
((SELECT ProductID FROM Products WHERE Name = 'Smirnoff Vodka 700ml'), 1, 25.99),
((SELECT ProductID FROM Products WHERE Name = 'Jack Daniels 700ml'), 1, 34.99),
((SELECT ProductID FROM Products WHERE Name = 'Bacardi White Rum 700ml'), 1, 19.99),
((SELECT ProductID FROM Products WHERE Name = 'Johnnie Walker Black Label'), 1, 39.99),
((SELECT ProductID FROM Products WHERE Name = 'Great Northern'), 1, 20.99);

-- Supplier 2 (Dan Murphys) also provides these products
INSERT INTO ProductSuppliers (ProductID, SupplierID, CostPrice) VALUES
((SELECT ProductID FROM Products WHERE Name = '-196 Can 10 x 330ml'), 2, 16.49),
((SELECT ProductID FROM Products WHERE Name = 'Smirnoff Vodka 700ml'), 2, 26.49),
((SELECT ProductID FROM Products WHERE Name = 'Jack Daniels 700ml'), 2, 35.49),
((SELECT ProductID FROM Products WHERE Name = 'Bacardi White Rum 700ml'), 2, 20.49),
((SELECT ProductID FROM Products WHERE Name = 'Johnnie Walker Black Label'), 2, 40.99),
((SELECT ProductID FROM Products WHERE Name = 'Great Northern'), 2, 21.49);




-- Change the delimiter for the trigger creation
DELIMITER //

CREATE TRIGGER update_inventory_quantity AFTER INSERT ON StockEntries FOR EACH ROW
BEGIN
    -- Update the InventoryItems table based on the new stock entry
    UPDATE InventoryItems
    SET Quantity = Quantity + NEW.QuantityChange
    WHERE InventoryItemID = NEW.InventoryItemID;
END;
//

-- Reset the delimiter to semicolon
DELIMITER ;

SHOW TRIGGERS;


-- Selling 5 units of product with InventoryItemID 1 (assuming this represents a product in your store)
INSERT INTO StockEntries (InventoryItemID, QuantityChange) VALUES (1, -5);

-- Restocking 20 units of product with InventoryItemID 1
INSERT INTO StockEntries (InventoryItemID, QuantityChange) VALUES (1, 20);

-- Restocking 10 units of product with InventoryItemID 2
INSERT INTO StockEntries (InventoryItemID, QuantityChange) VALUES (2, 10);

-- Selling 10 units of product with InventoryItemID 1
INSERT INTO StockEntries (InventoryItemID, QuantityChange) VALUES (1, -10);

-- Restocking 30 units of product with InventoryItemID 1
INSERT INTO StockEntries (InventoryItemID, QuantityChange) VALUES (1, 30);

-- Inserting Shopping Lists for different Stores
INSERT INTO ShoppingLists (Name, ManagerID, StoreID, DateCreated) VALUES
('Weekly Restock', (SELECT UserID FROM Users WHERE Username = 'ManagerOne'), 1, NOW()),
('Monthly Specials', (SELECT UserID FROM Users WHERE Username = 'ManagerTwo'), 2, NOW()),
('Festive Season', (SELECT UserID FROM Users WHERE Username = 'ManagerThree'), 3, NOW()),
('Summer Specials', (SELECT UserID FROM Users WHERE Username = 'ManagerOne'), 1, NOW()),
('Winter Collection', (SELECT UserID FROM Users WHERE Username = 'ManagerTwo'), 2, NOW());


-- Assuming ListID 1 to 5 correspond to the shopping lists inserted above and ProductIDs are known
-- ListID 1: Weekly Restock for Store 1
INSERT INTO ListProducts (ListID, ProductID, RequiredQty) VALUES
(1, (SELECT ProductID FROM Products WHERE Name = '-196 Can 10 x 330ml'), 10),
(1, (SELECT ProductID FROM Products WHERE Name = 'Smirnoff Vodka 700ml'), 15);

-- ListID 2: Monthly Specials for Store 2
INSERT INTO ListProducts (ListID, ProductID, RequiredQty) VALUES
(2, (SELECT ProductID FROM Products WHERE Name = 'Great Northern'), 20),
(2, (SELECT ProductID FROM Products WHERE Name = 'Jack Daniels 700ml'), 10);

-- ListID 3: Festive Season for Store 3
INSERT INTO ListProducts (ListID, ProductID, RequiredQty) VALUES
(3, (SELECT ProductID FROM Products WHERE Name = 'Johnnie Walker Black Label'), 5),
(3, (SELECT ProductID FROM Products WHERE Name = 'Bacardi White Rum 700ml'), 10);

-- ListID 4: Summer Specials for Store 1
INSERT INTO ListProducts (ListID, ProductID, RequiredQty) VALUES
(4, (SELECT ProductID FROM Products WHERE Name = '-196 Can 10 x 330ml'), 20),
(4, (SELECT ProductID FROM Products WHERE Name = 'Smirnoff Vodka 700ml'), 25),
(4, (SELECT ProductID FROM Products WHERE Name = 'Bacardi White Rum 700ml'), 30);

-- ListID 5: Winter Collection for Store 2
INSERT INTO ListProducts (ListID, ProductID, RequiredQty) VALUES
(5, (SELECT ProductID FROM Products WHERE Name = 'Great Northern'), 15),
(5, (SELECT ProductID FROM Products WHERE Name = 'Jack Daniels 700ml'), 20),
(5, (SELECT ProductID FROM Products WHERE Name = 'Johnnie Walker Black Label'), 10);

