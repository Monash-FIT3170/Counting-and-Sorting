import mysql.connector
from mysql.connector import errorcode
import uuid
from datetime import datetime, timedelta
import random
import json

# Database connection configuration
config = {
    'user': 'username',
    'password': 'password',
    'host': 'localhost',
    'database': 'database',
}

# Sample data settings
number_of_stores = 4
number_of_sales_per_month = 100
number_of_inventory_purchases_per_month = 10
initial_funds = 100000
export_as_json = True  # Toggle between exporting as JSON or SQL database

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
    return {
        "TxId": tx_id,
        "StoreId": store_id,
        "Item": item,
        "Amount": amount,
        "Type": tx_type,
        "To": to,
        "Date": date.strftime('%Y-%m-%d')
    }

# Function to execute the transaction on the database
def execute_transactions(transactions, cursor):
    for transaction in transactions:
        insert_query = """
        INSERT INTO transactions (TxId, StoreId, Item, Amount, Type, `To`, Date)
        VALUES (%s, %s, %s, %s, %s, %s, %s)
        """
        cursor.execute(insert_query, (
            transaction['TxId'], 
            transaction['StoreId'], 
            transaction['Item'], 
            transaction['Amount'], 
            transaction['Type'], 
            transaction['To'], 
            transaction['Date']
        ))

# Function to export the transactions as JSON
def export_transactions_as_json(transactions):
    with open('inventory-management-system/sample_data/transactions.json', 'w') as file:
        json.dump(transactions, file, indent=4)
    print("Sample data exported as JSON successfully!")

# Function to export the transactions as Excel
def export_transactions_as_excel(transactions):
    import pandas as pd
    df = pd.DataFrame(transactions)
    df.to_excel('inventory-management-system/sample_data/transactions.xlsx', index=False)
    print("Sample data exported as Excel successfully!")

# Main function to generate sample data
def generate_sample_data():
    transactions = []

    for store_id in range(1, number_of_stores + 1):
        # Initial funds
        transactions.append(create_transaction(generate_tx_id(), store_id, 'Initial funds', initial_funds, 'Initial funds', 'Store', datetime(2024, 1, 1)))

        total_inventory_cost = 0
        total_sales = 0
        sales_multiplier = 2.0  # Sales should be roughly double the inventory cost

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
            sales_per_transaction = round( monthly_sales / number_of_sales_per_month, 2)
            for _ in range(number_of_sales_per_month):
                item = random.choice(['Beer', 'Wine', 'Whiskey'])
                date = generate_random_date_within_month(year, month)
                transactions.append(create_transaction(generate_tx_id(), store_id, item, round(sales_per_transaction * random.uniform(0.5, 1.5), 2), 'Sales', 'Store', date))

            total_sales += monthly_sales

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

            # Quarterly Disbursements (30% of total sales)
            if month % 3 == 0:
                disbursement_amount = -0.30 * (total_sales)
                date = generate_random_date_within_month(year, month)
                transactions.append(create_transaction(generate_tx_id(), store_id, 'Royalty Fee', disbursement_amount, 'Disbursement', 'Head office', date))

    return transactions

# Run the data generation and export
try:
    if export_as_json:
        transactions = generate_sample_data()
        export_transactions_as_json(transactions)
        #save as excel
        export_transactions_as_excel(transactions)

    else:
        cnx = mysql.connector.connect(**config)
        cursor = cnx.cursor()

        # Creating the transactions table
        cursor.execute("""
        CREATE TABLE IF NOT EXISTS transactions (
            TxId VARCHAR(12) NOT NULL,
            StoreId INT NOT NULL,
            Item VARCHAR(50),
            Amount DECIMAL(10,2),
            Type VARCHAR(20),
            `To` VARCHAR(50),
            Date DATE,
            PRIMARY KEY (TxId)
        );
        """)

        transactions = generate_sample_data()
        execute_transactions(transactions, cursor)

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
    if not export_as_json:
        cursor.close()
        cnx.close()
