from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from bs4 import BeautifulSoup
import pandas as pd
import time

# Set up Selenium WebDriver
driver = webdriver.Chrome()  # Or whichever WebDriver you are using

# Visit the BWS store locator page
driver.get('https://bws.com.au/storelocator')

# Wait until the page is fully loaded
WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.ID, "storeLocator")))

# Enter Victoria in the search box
search_box = driver.find_element(By.ID, "store-locator-search-input")
search_box.send_keys("Victoria")

# Wait a moment for results to load
time.sleep(3)

# Grab the HTML content after results have loaded
soup = BeautifulSoup(driver.page_source, 'html.parser')

# Find the container with store results
store_list = soup.find_all('div', class_='store-item')

# Extract store details
store_locations = []
for store in store_list:
    name = store.find('div', class_='store-name').text.strip()
    address = store.find('div', class_='store-address').text.strip()
    store_locations.append({
        'Store Name': name,
        'Address': address
    })

# Save store locations to a CSV file using pandas
df = pd.DataFrame(store_locations)
df.to_csv('bws_victoria_locations.csv', index=False)

# Print confirmation
print(f"Saved {len(store_locations)} store locations to 'bws_victoria_locations.csv'.")

# Close the browser
driver.quit()
