from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from bs4 import BeautifulSoup
import json
import time

# Setup Selenium WebDriver
chrome_driver_path = 'webscraping/chromedriver'  # Update this path
service = Service(chrome_driver_path)
driver = webdriver.Chrome(service=service)

# List of URLs to scrape
urls = [
    'https://www.liquorland.com.au/spirits',
    'https://www.liquorland.com.au/beer',
    'https://www.liquorland.com.au/red-wine'
]

# Function to scrape a given URL
def scrape_page(url):
    driver.get(url)
    print(f"Opened the website: {url}")

    # Wait for the popup to appear and close it
    try:
        WebDriverWait(driver, 10).until(
            EC.presence_of_element_located((By.CSS_SELECTOR, 'div#setLocationModal'))
        )
        close_button = driver.find_element(By.CSS_SELECTOR, 'button.react-responsive-modal-closeButton')
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
            EC.presence_of_element_located((By.CSS_SELECTOR, 'div.ProductTileV2'))
        )
        print("Product tiles loaded")
    except Exception as e:
        print(f"Page took too long to load or failed to find product tiles: {e}")
        return []

    # Parse the page source with BeautifulSoup
    soup = BeautifulSoup(driver.page_source, 'html.parser')

    # Extract information of all products
    products = soup.select('div.ProductTileV2')
    product_list = []

    for product in products:
        try:
            name_brand = product.select_one('div.product-brand').text.strip()
            name_product = product.select_one('div.product-name').text.strip()
            name = f"{name_brand} {name_product}"
            cost = product.select_one('span.PriceTag .dollarAmount').text.strip() + '.' + product.select_one('span.PriceTag .centsAmount').text.strip()
            link = 'https://www.liquorland.com.au' + product.select_one('a.thumbnail')['href']
            
            product_list.append({
                'name': name,
                'cost': cost,
                'link': link
            })
            print(f"Extracted product: {name}")
        except Exception as e:
            print(f"Error extracting product: {e}")

    return product_list

# List to hold all products
all_products = []

# Scrape each URL and collect products
for url in urls:
    products = scrape_page(url)
    all_products.extend(products)

# Save to JSON file
with open('products.json', 'w', encoding='utf-8') as f:
    json.dump(all_products, f, ensure_ascii=False, indent=4)

# Close the browser
driver.quit()
print("Finished scraping")
