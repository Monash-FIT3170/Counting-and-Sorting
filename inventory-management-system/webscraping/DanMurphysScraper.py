from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from bs4 import BeautifulSoup
import json
# Path to where you have your ChromeDriver
chrome_driver_path = "chromedriver.exe"

# Set up the ChromeDriver options
options = webdriver.ChromeOptions()
options.add_argument('--headless')  # Run Chrome in headless mode (without GUI)
options.add_argument('--disable-gpu')  # Disable GPU hardware acceleration
options.add_argument('--no-sandbox')  # Bypass OS security model

# Initialize the WebDriver
service = Service(executable_path=chrome_driver_path)
driver = webdriver.Chrome(service=service, options=options)


'''
Purpose: This function scrapes the Dan Murphy's website for wine, beer, and spirits. It extracts the title and price information for each product card on the page.
Input: URL of the Dan Murphy's page to scrape
Output:
Date Modified: 04/08/2024
'''
def danMurphysScraper(url:str, type:str,products:list) -> None:
    # Navigate to the page
    driver.get(url)

    # Wait for the dynamic content to load
    wait = WebDriverWait(driver, 10)
    wait.until(EC.presence_of_element_located((By.TAG_NAME, 'shop-product-card')))

    # Parse the page source with BeautifulSoup
    soup = BeautifulSoup(driver.page_source, 'html5lib')

    # Find all product cards
    product_cards = soup.find_all('shop-product-card')
    

    # Iterate through each product card to extract title and price information
    for card in product_cards:
        # Extract title and subtitle
        title_span = card.select_one('span.title')
        subtitle_span = card.select_one('span.subtitle')
        
        # Combine title and subtitle
        if title_span and subtitle_span:
            title = title_span.get_text(strip=True)
            subtitle = subtitle_span.get_text(strip=True)
            full_title = f"{title} {subtitle}"
        elif title_span:
            full_title = title_span.get_text(strip=True)
        else:
            # Skip if title is not found
            continue
        
        # Extract price
        value_spans = card.select('span.value')
        prices = [value_span.get_text(strip=True) for value_span in value_spans]
        
        product_info = {
            'title': full_title,
            'type': type,
            'prices': prices[0]
        }

        products.append(product_info)

        '''
        # Print the title and price
        print(f"Title: {full_title}")
        print(f"{type}") # Print the type of product
        for price in prices:
            print(f"Price: {price}")
        '''
    


    
    

# Processing logic
def main():
    products = [] # List to store the extracted information

    danMurphysScraper("https://www.danmurphys.com.au/beer/all", "Beer",products)
    print("\n")
    danMurphysScraper("https://www.danmurphys.com.au/spirits/all", "Spirits",products)
    print("\n")
    danMurphysScraper("https://www.danmurphys.com.au/red-wine/availability-delivery/range-1","Wine",products)
    print(products)
    driver.quit()

if __name__ == "__main__":
    main()
