import mysql.connector
from mysql.connector import errorcode
import requests
import time
import logging
import re
import os
from dotenv import load_dotenv

# Load environment variables from .env file
load_dotenv()

# Configure logging
logging.basicConfig(
    filename='geocoding_errors.log',
    filemode='a',
    format='%(asctime)s - %(levelname)s - %(message)s',
    level=logging.INFO
)

# Database connection configuration
config = {
    'host': 'counting-sorting-1.cpy86y0c89jt.ap-southeast-2.rds.amazonaws.com',
    'user': 'admin',
    'password': 'countingSorting100%',
    'database': 'liquor_store',
    'port': 3306
}

# Geoapify API configuration
GEOCODING_API_URL = "https://api.geoapify.com/v1/geocode/search"
API_KEY = os.getenv("ADDRESS_API_KEY")  

if not API_KEY:
    raise ValueError("Geoapify API key not found. Please set ADDRESS_API_KEY in your .env file.")

# Geocoding rate limit (Geoapify allows up to 3 requests per second on the free plan)
REQUEST_DELAY = 0.35  # Approximately 3 requests per second

def clean_address(address):
    """
    Cleans and standardizes the address to improve geocoding accuracy.
    """
    address = address.strip()
    # Replace common abbreviations
    address = re.sub(r'\bCnr\b\.?', 'Corner', address, flags=re.IGNORECASE)
    address = re.sub(r'\bSt\b\.?', 'Street', address, flags=re.IGNORECASE)
    address = re.sub(r'\bHwy\b\.?', 'Highway', address, flags=re.IGNORECASE)
    address = re.sub(r'\bAnd\b', '&', address, flags=re.IGNORECASE)
    address = re.sub(r'\bShop\b\.?', 'Shop', address, flags=re.IGNORECASE)
    # Remove multiple spaces
    address = re.sub(r'\s+', ' ', address)
    return address

def add_geolocation_column(cursor):
    """
    Adds a POINT type geolocation column to the webscraped_stores table if it doesn't exist.
    """
    try:
        # Check if 'geolocation' column exists
        cursor.execute("""
            SELECT COLUMN_NAME 
            FROM INFORMATION_SCHEMA.COLUMNS 
            WHERE TABLE_SCHEMA = %s 
              AND TABLE_NAME = 'webscraped_stores' 
              AND COLUMN_NAME = 'geolocation';
        """, (config['database'],))
        result = cursor.fetchone()
        if not result:
            print("Adding 'geolocation' column to 'webscraped_stores' table.")
            cursor.execute("""
                ALTER TABLE webscraped_stores
                ADD COLUMN geolocation POINT NULL;
            """)
    except mysql.connector.Error as err:
        print(f"Error adding 'geolocation' column: {err}")
        logging.error(f"Error adding 'geolocation' column: {err}")
        raise

def fetch_stores_without_geolocation(cursor):
    """
    Fetches all stores that do not have geolocation data.
    """
    try:
        cursor.execute("""
            SELECT id, address 
            FROM webscraped_stores
            WHERE geolocation IS NULL;
        """)
        return cursor.fetchall()
    except mysql.connector.Error as err:
        print(f"Error fetching stores: {err}")
        logging.error(f"Error fetching stores: {err}")
        raise

def get_geolocation(address):
    """
    Retrieves geolocation (latitude and longitude) for a given address using Geoapify API.
    """
    cleaned_address = clean_address(address)
    params = {
        'text': cleaned_address,
        'format': 'json',
        'limit': 1,
        'apiKey': API_KEY
    }
    try:
        response = requests.get(GEOCODING_API_URL, params=params, timeout=10)
        response.raise_for_status()
        data = response.json()
        print(data)  # Debugging: print raw response

        # Case 1: Check for 'features' in response (Geoapify format)
        if 'features' in data and data['features']:
            geometry = data['features'][0]['geometry']
            lon, lat = geometry['coordinates']
            return lat, lon

        # Case 2: Check for 'results' in response (alternative format)
        elif 'results' in data and data['results']:
            lon = data['results'][0]['lon']
            lat = data['results'][0]['lat']
            return lat, lon

        # No valid geolocation found
        else:
            print(f"No geolocation found for address: {address}")
            logging.info(f"No geolocation found for address: {address}")
            return None, None
    except requests.HTTPError as http_err:
        if response.status_code == 401:
            print(f"Unauthorized access - check your API key.")
            logging.error(f"Unauthorized access for address '{address}': {http_err}")
        else:
            print(f"HTTP error occurred for address '{address}': {http_err}")
            logging.error(f"HTTP error for address '{address}': {http_err}")
        return None, None
    except requests.RequestException as e:
        print(f"Request error for address '{address}': {e}")
        logging.error(f"Request error for address '{address}': {e}")
        return None, None
    except (KeyError, IndexError, ValueError) as e:
        print(f"Error parsing geocoding response for address '{address}': {e}")
        logging.error(f"Error parsing geocoding response for address '{address}': {e}")
        return None, None

def update_geolocation(cursor, store_id, lat, lon):
    """
    Updates the geolocation column for a specific store.
    """
    try:
        cursor.execute("""
            UPDATE webscraped_stores
            SET geolocation = ST_GeomFromText(%s)
            WHERE id = %s;
        """, (f'POINT({lon} {lat})', store_id))
    except mysql.connector.Error as err:
        print(f"Error updating geolocation for store ID {store_id}: {err}")
        logging.error(f"Error updating geolocation for store ID {store_id}: {err}")
        raise

def main():
    try:
        # Connect to the database
        cnx = mysql.connector.connect(**config)
        cursor = cnx.cursor()

        # Add geolocation column if it doesn't exist
        add_geolocation_column(cursor)
        cnx.commit()

        # Fetch stores without geolocation
        stores = fetch_stores_without_geolocation(cursor)
        total_stores = len(stores)
        print(f"Found {total_stores} stores without geolocation data.")

        for index, (store_id, address) in enumerate(stores, start=1):
            print(f"[{index}/{total_stores}] Processing Store ID {store_id}: {address}")
            lat, lon = get_geolocation(address)
            if lat is not None and lon is not None:
                update_geolocation(cursor, store_id, lat, lon)
                cnx.commit()
                print(f"Updated Store ID {store_id} with geolocation: ({lat}, {lon})")
            else:
                print(f"Skipping Store ID {store_id} due to missing geolocation.")

            # Respect Geoapify's usage policy: up to 3 requests per second on free plan
            time.sleep(REQUEST_DELAY)

    except mysql.connector.Error as err:
        if err.errno == errorcode.ER_ACCESS_DENIED_ERROR:
            print("Something is wrong with your username or password")
            logging.error("Access denied: Check username or password.")
        elif err.errno == errorcode.ER_BAD_DB_ERROR:
            print("Database does not exist")
            logging.error("Database does not exist.")
        else:
            print(err)
            logging.error(f"MySQL Error: {err}")
    except Exception as e:
        print(f"An unexpected error occurred: {e}")
        logging.error(f"Unexpected error: {e}")
    finally:
        # Close cursor and connection
        if 'cursor' in locals() and cursor:
            cursor.close()
        if 'cnx' in locals() and cnx.is_connected():
            cnx.close()
        print("Database connection closed.")

if __name__ == "__main__":
    main()
