from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import TimeoutException, NoSuchElementException
from webdriver_manager.chrome import ChromeDriverManager
from bs4 import BeautifulSoup
import json

store_list = []

def get_stats(urls, store_list):
    ID = 0
    options = webdriver.ChromeOptions()
    service = Service(executable_path='chromedriver.exe')
    # options.add_argument('--headless') 

    driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=options)

    for url in urls:
        driver.get(url)

        shadow_root = driver.find_element(By.CLASS_NAME, "bws-aem-content").shadow_root
        shadow_text = shadow_root.find_element(By.CLASS_NAME, 'bws-page bws-page--full global-style__storelocator').text
        print(shadow_text)
        # 
        # shadow_root = driver.execute_script('return arguments[0].shadowRoot', shadow_host)
        # shadow_content = shadow_root.find_element(By.CSS_SELECTOR, 'bws-page > section > aem-page > aem-model-provider:nth-child(2) > aem-responsivegrid > div:nth-child(2) > bws-store-locator > div > bws-store-finder-widget > div > div > bws-store-locator-sidebar > div > div.bws-store-locator__stores-list')
        # content = shadow_content.get_attribute('innerHTML')
        # soup = BeautifulSoup(content, "lxml")
   
        # stats = soup.findAll("div", class_="bws-store-locator__store-container")
        # print(content)
        
        
        # for stat in stats:
            
            # a = stat.findAll('div', class_='bws-content-wrapper bws-content-wrapper--rootgrid')
            # print(a)
            # location = stat.find('a', class_='link-item').text.strip()
            # print(f'{ID} | Location: {location}')

            # product_data = {
            #     'location': location,
            # }
        
            # store_list.append(product_data)
    
    driver.quit()


store_urls = ["https://bws.com.au/storelocator"]
get_stats(store_urls, store_list)

json_data = json.dumps(store_list, indent=4, ensure_ascii=False)

with open('bws_stores.json', 'w', encoding='utf-8') as json_file:
    json_file.write(json_data)

print(json_data)