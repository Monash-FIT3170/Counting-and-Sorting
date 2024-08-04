from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from bs4 import BeautifulSoup
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
def danMurphysWineScraper(url:str):
    # Navigate to the page
    driver.get(url)

    # Wait for the dynamic content to load
    wait = WebDriverWait(driver, 10)
    wait.until(EC.presence_of_element_located((By.CLASS_NAME, 'ng-star-inserted')))

    # Now you can parse the page source with BeautifulSoup
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
            full_title = "No title found"
        
        # Extract price
        value_spans = card.select('span.value')
        prices = [value_span.get_text(strip=True) for value_span in value_spans]
        
        # Print the extracted information
        print(f"Title: {full_title}")
        for price in prices:
            print(f"Price: {price}")


    
    

# Your processing logic here
def main():
    danMurphysWineScraper("https://www.danmurphys.com.au/beer/all")
    print("\n")
    danMurphysWineScraper("https://www.danmurphys.com.au/spirits/all")
    print("\n")
    danMurphysWineScraper("https://www.danmurphys.com.au/red-wine/availability-delivery/range-1")

    driver.quit()

if __name__ == "__main__":
    main()
