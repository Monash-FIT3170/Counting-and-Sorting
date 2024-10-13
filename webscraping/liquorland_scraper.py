from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from bs4 import BeautifulSoup
import json
import time
import chromedriver_autoinstaller
import re

try:
    # Install the latest version of ChromeDriver
    chromedriver_autoinstaller.install()

    # Set up the ChromeDriver options
    options = webdriver.ChromeOptions()
    # options.add_argument('--headless')  # Run Chrome in headless mode (without GUI)
    options.add_argument('--disable-gpu')  # Disable GPU hardware acceleration
    options.add_argument('--no-sandbox')  # Bypass OS security model

    # Initialize the WebDriver
    driver = webdriver.Chrome(options=options)

except Exception as e:
    print(f"An error occurred: {str(e)}")


# Function to scrape a given URL
def scrape_products(url, type: str, product_list: list) -> list:
    driver.get(url)
    print(f"Opened the website: {url}")

    # Wait for the popup to appear and close it
    try:
        WebDriverWait(driver, 10).until(
            EC.presence_of_element_located(
                (By.CSS_SELECTOR, 'div#setLocationModal'))
        )
        close_button = driver.find_element(
            By.CSS_SELECTOR, 'button.react-responsive-modal-closeButton')
        close_button.click()
        print("Closed the popup")
    except Exception as e:
        print(f"No popup appeared or failed to close the popup: {e}")

    # Scroll down a bit to trigger product loading
    driver.execute_script("window.scrollBy(0, 500);")
    time.sleep(1)  # Wait for products to load

    # Wait for the product tiles to load using a more specific CSS selector
    try:
        WebDriverWait(driver, 10).until(
            EC.presence_of_element_located(
                (By.CSS_SELECTOR, 'div.ProductTileV2'))
        )
        print("Product tiles loaded")
    except Exception as e:
        print(
            f"Page took too long to load or failed to find product tiles: {e}")
        return []

    # Parse the page source with BeautifulSoup
    soup = BeautifulSoup(driver.page_source, 'html.parser')

    # Extract information of all products
    products = soup.select('div.ProductTileV2')

    for product in products:
        try:
            name_brand = product.select_one('div.product-brand').text.strip()
            name_product = product.select_one('div.product-name').text.strip()
            name = f"{name_brand} {name_product}"
            cost = product.select_one('span.PriceTag .dollarAmount').text.strip(
            ) + '.' + product.select_one('span.PriceTag .centsAmount').text.strip()
            link = 'https://www.liquorland.com.au' + \
                product.select_one('a.thumbnail')['href']

            product_list.append({
                'title': name,
                'type': type,
                'price': extract_price(cost),
                'supplier': 'Liquorland',

            })
            print(f"Extracted product: {name}")
        except Exception as e:
            print(f"Error extracting product: {e}")


# Function to scrape store information
def scrape_stores(url):
    driver.get(url)
    print(f"Opened the website: {url}")

    # Wait for the popup to appear and close it
    try:
        WebDriverWait(driver, 10).until(
            EC.presence_of_element_located(
                (By.CSS_SELECTOR, 'div#setLocationModal'))
        )
        close_button = driver.find_element(
            By.CSS_SELECTOR, 'button.react-responsive-modal-closeButton')
        close_button.click()
        print("Closed the popup")
    except Exception as e:
        print(f"No popup appeared or failed to close the popup: {e}")

    # Wait for the store details to load
    try:
        WebDriverWait(driver, 20).until(
            EC.presence_of_element_located(
                (By.CSS_SELECTOR, 'div.StoreDetailsListItem'))
        )
        print("Store details loaded")
    except Exception as e:
        print(
            f"Page took too long to load or failed to find store details: {e}")
        return []

    # Parse the page source with BeautifulSoup
    soup = BeautifulSoup(driver.page_source, 'html.parser')

    # Extract information of all stores
    stores = soup.select('div.StoreDetailsListItem')
    store_list = []

    for store in stores:
        try:
            store_name = store.select_one('div.store-name').text.strip()
            if not store_name.lower().startswith("liquorland"):
                name = "Liquorland " + store_name
            else:
                name = store_name

            address = store.select_one('div.address').text.strip()
            suburb_postcode = store.select_one(
                'div.suburb-postcode').text.strip()
            full_address = f"{address}, {suburb_postcode}"

            store_list.append({
                'name': name,
                'address': full_address
            })
            print(f"Extracted store: {name}")
        except Exception as e:
            print(f"Error extracting store: {e}")

    return store_list

def extract_price(price_str:str) -> float:
    # Extract the price value from the string
    match = re.search(r'(\d+(?:\.\d+)?)', price_str)
    if match:
        return float(match.group(1))
    return None

def main():
    """ # List of URLs to scrape
    urls = [
        'https://www.liquorland.com.au/spirits',
        'https://www.liquorland.com.au/beer',
        'https://www.liquorland.com.au/red-wine'
    ]
    # List to hold all products
    all_products = []

    # Scrape product information from each URL
    scrape_products(urls[0],'Spirits',all_products)
    scrape_products(urls[1],'Beer',all_products)
    scrape_products(urls[2],'Wine',all_products)

    print(all_products)

    # Save products to JSON file
    with open('liquorland_products.json', 'w', encoding='utf-8') as f:
        json.dump(all_products, f, ensure_ascii=False, indent=4) """

    # Scrape store information
    store_url = 'https://www.liquorland.com.au/stores'
    all_stores = scrape_stores(store_url)

    # Save stores to JSON file
    with open('liquorland_stores.json', 'w', encoding='utf-8') as f:
        json.dump(all_stores, f, ensure_ascii=False, indent=4)

    # Close the browser
    driver.quit()
    print("Finished scraping")

if __name__ == "__main__":
    main()
