from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from webdriver_manager.chrome import ChromeDriverManager
from bs4 import BeautifulSoup
import time

# Set up the webdriver
service = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=service)

# Navigate to the BWS store locator page
driver.get("https://bws.com.au/storelocator")

# Wait for the store list to load
WebDriverWait(driver, 30).until(
    EC.presence_of_element_located((By.CLASS_NAME, "bws-store-locator__store-container"))
)

# Scroll to load all results
last_height = driver.execute_script("return document.body.scrollHeight")
while True:
    driver.execute_script("window.scrollTo(0, document.body.scrollHeight);")
    time.sleep(2)
    new_height = driver.execute_script("return document.body.scrollHeight")
    if new_height == last_height:
        break
    last_height = new_height

# Get the page source and parse with BeautifulSoup
soup = BeautifulSoup(driver.page_source, 'html.parser')

# Find all store containers
store_containers = soup.find_all("div", class_="bws-store-locator__store-container")

# Extract data for each store
stores = []
for container in store_containers:
    info_container = container.find("div", class_="bws-store-locator__store-info-container")
    name = info_container.find("h3", class_="bws-store-locator__store-header").text.strip()
    address = info_container.find("p", class_="bws-store-locator__store-address").text.strip()
    
    
    stores.append({
        "name": name,
        "address": address,
    })

# Print or save the results
for store in stores:
    print(store)

# Close the browser
driver.quit()