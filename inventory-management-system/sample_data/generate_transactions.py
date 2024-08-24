# Sample data generator for the liquor store
import mysql.connector
from mysql.connector import connect
from mysql.connector import errorcode
import uuid
from datetime import datetime, timedelta
import random
import json

# Database connection configuration
config = {
    'host': 'counting-sorting-1.cpy86y0c89jt.ap-southeast-2.rds.amazonaws.com',
    'user': 'admin',
    'password': 'countingSorting100%',
    'database': 'liquor_store',
    'port': 3306
}

# Sample data settings
number_of_stores = 4
number_of_sales_per_month = 300
number_of_inventory_purchases_per_month = 8
initial_funds = 100000
export_as_json = False  # Toggle between exporting as JSON or SQL database

# Generate a random UUID
def generate_tx_id():
    return str(uuid.uuid4())[:12]

# Generate a random date within a specific month
def generate_random_date_within_month(year, month):
    start_date = datetime(year, month, 1)
    days_in_month = (start_date.replace(month=month % 12 + 1, day=1) - timedelta(days=1)).day
    return start_date + timedelta(days=random.randint(0, days_in_month - 1))

# Function to create a transaction entry
def create_transaction(tx_id, store_id, item, amount, tx_type, to, date):
    return (
        tx_id, store_id, item, amount, tx_type, to, date.strftime('%Y-%m-%d')
    )

# Function to export the transactions as JSON
def export_transactions_as_json(transactions):
    with open('inventory-management-system/sample_data/transactions.json', 'w') as file:
        json.dump([dict(zip(['TxId', 'StoreId', 'Item', 'Amount', 'Type', 'To', 'Date'], transaction)) for transaction in transactions], file, indent=4)
    print("Sample data exported as JSON successfully!")

# Function to export the transactions as Excel
def export_transactions_as_excel(transactions):
    import pandas as pd
    df = pd.DataFrame(transactions, columns=['TxId', 'StoreId', 'Item', 'Amount', 'Type', 'To', 'Date'])
    df.to_excel('inventory-management-system/sample_data/transactions.xlsx', index=False)
    print("Sample data exported as Excel successfully!")

# Main function to generate sample data
def generate_sample_data():
    transactions = []

    for store_id in range(1, number_of_stores + 1):
        # Initial funds
        transactions.append(create_transaction(generate_tx_id(), store_id, 'Initial Funds', initial_funds, 'INITIAL_FUNDS', 'Store', datetime(2024, 1, 1)))

        total_inventory_cost = 0
        total_sales = 0
        quarterly_sales = 0
        sales_multiplier = random.uniform(1.4, 4.5) # Sales should be roughly 2-3x the inventory cost

        for month in range(1, 13):  # Loop over each month in the year
            year = 2024

            # Inventory Purchases
            monthly_inventory_cost = 0
            for _ in range(number_of_inventory_purchases_per_month):
                item = random.choice(['Beer', 'Wine', 'Whiskey'])
                amount = -round(random.uniform(50, 800), 2)  # Random inventory cost
                monthly_inventory_cost += abs(amount)
                supplier = random.choice(['BWS', 'Liquorland', 'Dan Murphy'])
                date = generate_random_date_within_month(year, month)
                transactions.append(create_transaction(generate_tx_id(), store_id, item, amount, 'Inventory', supplier, date))

            total_inventory_cost += monthly_inventory_cost

            # Sales
            monthly_sales = monthly_inventory_cost * sales_multiplier
            sales_per_transaction = round(monthly_sales / number_of_sales_per_month, 2)
            for _ in range((round(number_of_sales_per_month * random.uniform(0.8, 1.5)))):
                item = random.choice(['Beer', 'Wine', 'Whiskey'])
                date = generate_random_date_within_month(year, month)
                transactions.append(create_transaction(generate_tx_id(), store_id, item, round(sales_per_transaction * random.uniform(0.5, 1.5), 2), 'Sales', 'Store', date))

            total_sales += monthly_sales
            quarterly_sales  += monthly_sales

            # Expenses (same every month for simplicity)
            expenses = [
                ('Wages', -200),
                ('Rent', -1000),
                ('Electricity', -300),
                ('Insurance', -200),
                ('Damaged goods', -100),
            ]
            for item, amount in expenses:
                date = generate_random_date_within_month(year, month)
                transactions.append(create_transaction(generate_tx_id(), store_id, item, amount, 'Expenses', '', date))

            # Quarterly Disbursements (30% of total sales for the quarter)
            if month % 3 == 0:
                disbursement_amount = -0.30 * quarterly_sales
                quarterly_sales = 0
                date = generate_random_date_within_month(year, month)
                transactions.append(create_transaction(generate_tx_id(), store_id, 'Royalty Fee', disbursement_amount, 'Disbursement', 'Head office', date))

    return transactions

# Run the data generation and export
try:
    transactions = generate_sample_data()
    
    if export_as_json:
        export_transactions_as_json(transactions)
        export_transactions_as_excel(transactions)
    else:
        cnx = mysql.connector.connect(**config)
        cursor = cnx.cursor()

        # Creating the transactions table
        cursor.execute("""
        CREATE TABLE IF NOT EXISTS store_transactions (
            tx_id VARCHAR(12) NOT NULL,
            store_id INT NOT NULL,
            item VARCHAR(50),
            amount DECIMAL(10,2),
            type ENUM('SALES', 'INVENTORY', 'EXPENSES', 'INITIAL_FUNDS', 'DISBURSEMENT'),
            to_entity VARCHAR(50),
            date DATE,
            id INT AUTO_INCREMENT PRIMARY KEY
        );
        """)

        # Clear the transactions table
        cursor.execute("DELETE FROM store_transactions")

        # Bulk insert all transactions at once
        insert_query = """
        INSERT INTO store_transactions (tx_id, store_id, item, amount, type, to_entity, date)
        VALUES (%s, %s, %s, %s, %s, %s, %s)
        """
        cursor.executemany(insert_query, transactions)

        cnx.commit()
        print("Sample data inserted into SQL database successfully!")

except mysql.connector.Error as err:
    if err.errno == errorcode.ER_ACCESS_DENIED_ERROR:
        print("Something is wrong with your user name or password")
    elif err.errno == errorcode.ER_BAD_DB_ERROR:
        print("Database does not exist")
    else:
        print(err)
finally:
    if not export_as_json and 'cursor' in locals():
        cursor.close()
    if not export_as_json and 'cnx' in locals():
        cnx.close()
