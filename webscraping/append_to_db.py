import os
import json
import mysql.connector
from mysql.connector import Error
from datetime import datetime

def create_connection():
    """ Create a database connection to the MySQL database """
    conn = None
    try:
        conn = mysql.connector.connect(
            host='counting-sorting-1.cpy86y0c89jt.ap-southeast-2.rds.amazonaws.com',
            user='admin',
            password='countingSorting100%',
            database='liquor_store',
            port=3306
        )
        if conn.is_connected():
            print("Connected to MySQL database")
    except Error as e:
        print(f"Error: {e}")
    return conn

def create_tables(conn):
    """ Create the webscraped_products and webscraped_stores tables """
    try:
        cursor = conn.cursor()

        # Table for products
        sql_create_products_table = """
        CREATE TABLE IF NOT EXISTS webscraped_products (
            id INT AUTO_INCREMENT PRIMARY KEY,
            title VARCHAR(255) NOT NULL,
            type VARCHAR(50) NOT NULL,
            price DECIMAL(10, 2) NOT NULL,
            supplier VARCHAR(100) NOT NULL,
            appended_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
            UNIQUE(title, type, supplier)
        );
        """
        cursor.execute(sql_create_products_table)
        
        # Table for stores
        sql_create_stores_table = """
        CREATE TABLE IF NOT EXISTS webscraped_stores (
            id INT AUTO_INCREMENT PRIMARY KEY,
            title VARCHAR(255) NOT NULL,
            address VARCHAR(255) NOT NULL,
            supplier VARCHAR(100) NOT NULL,
            appended_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
            UNIQUE(title, address, supplier)
        );
        """
        cursor.execute(sql_create_stores_table)

        print("Tables created successfully")
    except Error as e:
        print(f"Error: {e}")

def insert_product(conn, product):
    """ Insert or update a product in the webscraped_products table """
    sql_insert = """
    INSERT INTO webscraped_products (title, type, price, supplier)
    VALUES (%s, %s, %s, %s)
    ON DUPLICATE KEY UPDATE
        price = VALUES(price),
        appended_date = CURRENT_TIMESTAMP;
    """
    cursor = conn.cursor()
    try:
        cursor.execute(sql_insert, (product['title'], product['type'], product['price'], product['supplier']))
        conn.commit()
    except Error as e:
        print(f"Error inserting/updating: {product['title']} from {product['supplier']} - {e}")

def insert_store(conn, store, supplier):
    """ Insert or update a store in the webscraped_stores table """
    sql_insert = """
    INSERT INTO webscraped_stores (title, address, supplier)
    VALUES (%s, %s, %s)
    ON DUPLICATE KEY UPDATE
        appended_date = CURRENT_TIMESTAMP;
    """
    cursor = conn.cursor()
    try:
        cursor.execute(sql_insert, (store['title'], store['address'], supplier))
        conn.commit()
    except Error as e:
        print(f"Error inserting/updating: {store['title']} from {supplier} - {e}")

def load_json_and_insert(conn, json_files, is_store=False):
    """ Load data from JSON files and insert into the appropriate database table """
    for file, supplier in json_files:
        with open(file, 'r') as f:
            data = json.load(f)
            for item in data:
                if is_store:
                    insert_store(conn, item, supplier)
                else:
                    insert_product(conn, item)
        print(f"Data from {file} inserted/updated successfully")
        

def main():
    # JSON files for products
    product_files = [
        ('webscraping/bws_products.json', 'BWS'),
        ('webscraping/liquorland_products.json', 'Liquorland')
    ]

    # JSON files for stores
    store_files = [
        ('webscraping/bws_stores.json', 'BWS'),
        ('webscraping/liquorland_stores.json', 'Liquorland')
    ]

    # Create a database connection
    conn = create_connection()
    if conn is not None and conn.is_connected():
        # Create tables
        create_tables(conn)

        # Load JSON files and insert data into products table
        load_json_and_insert(conn, product_files)

        # Load JSON files and insert data into stores table
        load_json_and_insert(conn, store_files, is_store=True)

        # Close connection
        conn.close()
    else:
        print("Error! Cannot create the database connection.")

if __name__ == '__main__':
    main()
