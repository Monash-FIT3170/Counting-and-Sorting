from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import time
import csv


driver = webdriver.Chrome() 


driver.get("https://www.google.com/maps")


search_box = WebDriverWait(driver, 10).until(
    EC.presence_of_element_located((By.ID, "searchboxinput"))
)
search_box.send_keys("BWS Victoria")
search_box.send_keys(Keys.ENTER)


time.sleep(5)


def scroll_results():
    scrollable_div = WebDriverWait(driver, 10).until(
        EC.presence_of_element_located((By.CSS_SELECTOR, 'div[aria-label="Results for BWS Victoria"]'))
    )
    last_height = driver.execute_script("return arguments[0].scrollHeight", scrollable_div)
    
    while True:
        driver.execute_script("arguments[0].scrollTo(0, arguments[0].scrollHeight);", scrollable_div)
        time.sleep(2)
        new_height = driver.execute_script("return arguments[0].scrollHeight", scrollable_div)
        if new_height == last_height:
            break
        last_height = new_height


scroll_results()

stores = driver.find_elements(By.CSS_SELECTOR, 'div.Nv2PK')
print(f"Found {len(stores)} potential store elements")


store_data = []


for store in stores:
    try:
        name = store.find_element(By.CSS_SELECTOR, 'h3.fontHeadlineSmall').text.strip()
        address_elements = store.find_elements(By.CSS_SELECTOR, 'div.W4Efsd')
        address = ''
        for elem in address_elements:
            if 'VIC' in elem.text:
                address = elem.text.strip()
                break
        
        if name and address:
            store_data.append({
                'name': name,
                'address': address
            })
            print(f"Added store: {name} at {address}")
    except:
        # Suppress all errors silently
        pass


driver.quit()

# Save the data to a CSV file
with open('bws_victoria_stores_google_maps.csv', 'w', newline='', encoding='utf-8') as csvfile:
    fieldnames = ['name', 'address']
    writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
    
    writer.writeheader()
    for store in store_data:
        writer.writerow(store)


