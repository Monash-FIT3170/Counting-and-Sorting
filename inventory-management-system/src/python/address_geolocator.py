import mysql.connector
from mysql.connector import errorcode
import requests
import time

# Database connection configuration
config = {
    'host': 'counting-sorting-1.cpy86y0c89jt.ap-southeast-2.rds.amazonaws.com',
    'user': 'admin',
    'password': 'countingSorting100%',
    'database': 'liquor_store',
    'port': 3306
}

# Geocoding API configuration
GEOCODING_API_URL = "https://nominatim.openstreetmap.org/search"
USER_AGENT = "MyGeocodingScript/1.0"  # Replace with your application's name/version

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
        raise

def get_geolocation(address):
    """
    Retrieves geolocation (latitude and longitude) for a given address using Nominatim API.
    """
    params = {
        'q': address,
        'format': 'json',
        'limit': 1
    }
    headers = {
        'User-Agent': USER_AGENT
    }
    try:
        response = requests.get(GEOCODING_API_URL, params=params, headers=headers, timeout=10)
        response.raise_for_status()
        data = response.json()
        if data:
            lat = float(data[0]['lat'])
            lon = float(data[0]['lon'])
            return lat, lon
        else:
            print(f"No geolocation found for address: {address}")
            return None, None
    except requests.RequestException as e:
        print(f"Request error for address '{address}': {e}")
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
        print(f"Found {len(stores)} stores without geolocation data.")

        for index, (store_id, address) in enumerate(stores, start=1):
            print(f"[{index}/{len(stores)}] Processing Store ID {store_id}: {address}")
            lat, lon = get_geolocation(address)
            if lat is not None and lon is not None:
                update_geolocation(cursor, store_id, lat, lon)
                cnx.commit()
                print(f"Updated Store ID {store_id} with geolocation: ({lat}, {lon})")
            else:
                print(f"Skipping Store ID {store_id} due to missing geolocation.")

            # Respect Nominatim's usage policy: maximum 1 request per second
            time.sleep(1)

    except mysql.connector.Error as err:
        if err.errno == errorcode.ER_ACCESS_DENIED_ERROR:
            print("Something is wrong with your user name or password")
        elif err.errno == errorcode.ER_BAD_DB_ERROR:
            print("Database does not exist")
        else:
            print(err)
    except Exception as e:
        print(f"An unexpected error occurred: {e}")
    finally:
        # Close cursor and connection
        if 'cursor' in locals() and cursor:
            cursor.close()
        if 'cnx' in locals() and cnx.is_connected():
            cnx.close()
        print("Database connection closed.")

if __name__ == "__main__":
    main()
