-- 1. Shopping Lists for Each Store
SELECT
    s.StoreID,
    s.Location,
    sl.ListID,
    sl.Name AS ShoppingListName,
    sl.DateCreated
FROM
    Stores s
JOIN
    ShoppingLists sl ON s.StoreID = sl.StoreID
ORDER BY
    s.StoreID, sl.DateCreated DESC;


-- 2. Items in a Shopping List
SELECT
    sl.ListID,
    sl.Name AS ShoppingListName,
    p.Name AS ProductName,
    lp.RequiredQty
FROM
    ShoppingLists sl
JOIN
    ListProducts lp ON sl.ListID = lp.ListID
JOIN
    Products p ON lp.ProductID = p.ProductID
WHERE
    sl.ListID = 3;

-- 3. Quantity Remaining of Inventory Items
SELECT
    ii.StoreID,
    p.Name AS ProductName,
    ii.Quantity AS QuantityRemaining
FROM
    InventoryItems ii
JOIN
    Products p ON ii.ProductID = p.ProductID;

-- 4. Percentage of Capacity of Products in a Store
SELECT
    ii.StoreID,
    p.Name AS ProductName,
    CONCAT(ROUND((ii.Quantity / ii.Capacity) * 100, 2), '%') AS PercentageOfCapacity
FROM
    InventoryItems ii
JOIN
    Products p ON ii.ProductID = p.ProductID;


 -- 5. Inventory overview of a store
 SELECT
    s.Location,
    p.Name AS ProductName,
    ii.Quantity AS QuantityInStock,
    ii.Capacity,
    CONCAT(ROUND((ii.Quantity / ii.Capacity) * 100, 2), '%') AS UtilizationPercentage
FROM
    Stores s
JOIN
   InventoryItems ii ON s.StoreID = ii.StoreID
JOIN
   Products p ON ii.ProductID = p.ProductID;



-- 6. Suppliers for eachg product
  SELECT
   p.Name AS ProductName,
   GROUP_CONCAT(sup.Name SEPARATOR ', ') AS SuppliersList
FROM
   Products p JOIN ProductSuppliers ps ON p.ProductID = ps.ProductID JOIN Suppliers sup ON ps.SupplierID = sup.SupplierID GROUP BY p.ProductID;


  -- Best (cheapest) suppliers for each product
  SELECT
    p.ProductID,
    p.Name AS ProductName,
    s.SupplierID,
    s.Name AS SupplierName,
    ps.CostPrice AS BestPrice
FROM
    ProductSuppliers ps
JOIN
    Products p ON ps.ProductID = p.ProductID
JOIN
    Suppliers s ON ps.SupplierID = s.SupplierID
INNER JOIN
    (SELECT
        ProductID, MIN(CostPrice) AS MinCostPrice
     FROM
        ProductSuppliers
     GROUP BY
        ProductID) AS min_prices ON ps.ProductID = min_prices.ProductID AND ps.CostPrice = min_prices.MinCostPrice;