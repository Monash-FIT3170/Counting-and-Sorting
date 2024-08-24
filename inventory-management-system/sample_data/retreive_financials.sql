-- Total expenses per store
SELECT StoreId, SUM(Amount) AS TotalExpenses
FROM transactions
WHERE Amount < 0
GROUP BY StoreId;

-- Total sales per store
SELECT StoreId, SUM(Amount) AS TotalSales
FROM transactions
WHERE Type = 'Sales'
GROUP BY StoreId;

-- Breakdown of costs store 1
SELECT Item, SUM(Amount) AS TotalCost
FROM transactions
WHERE StoreId = 1 AND Amount < 0
GROUP BY Item
ORDER BY TotalCost;

-- Revenue breakdown store 1
SELECT Item, SUM(Amount) AS TotalRevenue
FROM transactions
WHERE StoreId = 1 AND Amount > 0
GROUP BY Item
ORDER BY TotalRevenue;

-- Account balances of all stores
SELECT StoreId, SUM(Amount) AS AccountBalance
FROM transactions
GROUP BY StoreId;

-- Profits and account balances of each store
SELECT 
    StoreId,
    SUM(Amount) AS AccountBalance,
    SUM(Amount) - 
    (SELECT SUM(Amount) 
     FROM transactions 
     WHERE Type = 'Initial funds' AND StoreId = t.StoreId) AS Profit
FROM 
    transactions t
GROUP BY 
    StoreId;
   
   -- This query returns the total amount to head office
SELECT ABS(SUM(Amount)) AS TotalDisbursements
FROM transactions
WHERE Type = 'Disbursement';
