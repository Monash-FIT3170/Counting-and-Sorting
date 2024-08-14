from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import TimeoutException, NoSuchElementException
from webdriver_manager.chrome import ChromeDriverManager
from bs4 import BeautifulSoup
import json

product_list = []

def get_stats(urls, type, product_list):
    ID = 0
    options = webdriver.ChromeOptions()
    #options.add_argument('--headless') 

    driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=options)

    for url in urls:
        driver.get(url)
        
        content = driver.page_source.encode('utf-8').strip()
        soup = BeautifulSoup(content, "lxml")
        stats = soup.findAll("div", class_="productTile")
        
        for stat in stats:
            name = stat.find('div', class_='productTile_name ng-binding').text.strip()
            price = float(stat.find('span', class_='productTile_priceDollars ng-binding').text.strip())
            ID += 1
            print(f'{ID} | Product Name: {name} | Product Type: {type} | Product Price: {price}')

            product_data = {
                'title': name,
                'type': type,
                'price': price,
                'supplier': 'BWS'
            }
        
            product_list.append(product_data)
    
    driver.quit()

beer_urls = ["https://bws.com.au/beer/australian-beer", 
             "https://bws.com.au/beer/imported-beer"]
get_stats(beer_urls, "Beer", product_list)

wine_urls = ["https://bws.com.au/wine/red-wine",
             "https://bws.com.au/wine/white-wine",
             "https://bws.com.au/wine/champagne-sparkling"]
get_stats(wine_urls, "Wine", product_list)

cider_urls = ["https://bws.com.au/beer/cider"]
get_stats(cider_urls, "Cider", product_list)

spirit_urls = ["https://bws.com.au/spirits/whisky",
               "https://bws.com.au/spirits/bourbon",
               "https://bws.com.au/spirits/rum",
               "https://bws.com.au/spirits/vodka"]
get_stats(spirit_urls, "Spirits", product_list)

json_data = json.dumps(product_list, indent=4, ensure_ascii=False)

with open('bws_products.json', 'w', encoding='utf-8') as json_file:
    json_file.write(json_data)

print(json_data)