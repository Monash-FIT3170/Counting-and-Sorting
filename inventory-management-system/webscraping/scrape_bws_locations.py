from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from bs4 import BeautifulSoup
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
