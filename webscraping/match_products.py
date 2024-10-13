import json
from fuzzywuzzy import fuzz, process

def load_json(file_path):
    """Load JSON data from a file."""
    with open(file_path, 'r') as file:
        return json.load(file)

def match_products(product, product_list, threshold=80):
    """Matches a product to a list of products based on title similarity."""
    matched_product = process.extractOne(product['title'], [p['title'] for p in product_list], scorer=fuzz.token_set_ratio)
    
    if matched_product[1] >= threshold:  # If the match score is above the threshold
        matched_index = [p['title'] for p in product_list].index(matched_product[0])
        return product_list[matched_index], matched_product[1]
    return None, 0

def match_products_between_suppliers(bws_products, liquorland_products, threshold=80):
    """Match products between BWS and Liquorland."""
    matches = []
    for bws_product in bws_products:
        match, score = match_products(bws_product, liquorland_products, threshold)
        if match:
            matches.append({
                "bws_title": bws_product['title'],
                "bws_price": bws_product['price'],
                "liquorland_title": match['title'],
                "liquorland_price": match['price'],
                "match_score": score
            })
        else:
            print(f"No match found for BWS product: {bws_product['title']}")
    return matches

def main():
    # Load JSON data from files
    bws_products = load_json('webscraping/bws_products.json')
    liquorland_products = load_json('webscraping/liquorland_products.json')

    # Match products
    matched_products = match_products_between_suppliers(bws_products, liquorland_products)

    # Print the matched products
    for match in matched_products:
        print(f"Match found:\n"
              f"  BWS Product: {match['bws_title']} - ${match['bws_price']}\n"
              f"  Liquorland Product: {match['liquorland_title']} - ${match['liquorland_price']}\n"
              f"  Match Score: {match['match_score']}\n")

    # Optionally, save the matches to a file
    with open('matched_products.json', 'w') as outfile:
        json.dump(matched_products, outfile, indent=4)

if __name__ == '__main__':
    main()
