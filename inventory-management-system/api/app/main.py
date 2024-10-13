from fastapi import FastAPI, HTTPException, Header, Depends
from pydantic import BaseModel
from fastapi.responses import HTMLResponse
from fastapi.staticfiles import StaticFiles
from typing import Optional, List, Dict
from enum import Enum
from decimal import Decimal
from datetime import date, datetime
from uuid import UUID, uuid4

app = FastAPI(
    title="Counting and Sorting API",
    description=(
        "This API allows for managing transactions, products, and suppliers within the Counting and Sorting system. "
        "It is divided into sections for Managers (who manage individual stores) and Admins (who oversee all stores)."
    ),
    version="1.3.0",
    contact={
        "name": "Counting and Sorting Support",
        "url": "http://example.com/support",
        "email": "support@example.com"
    },
    license_info={
        "name": "Apache 2.0",
        "url": "http://www.apache.org/licenses/LICENSE-2.0.html",
    },
    docs_url="/docs",
    redoc_url="/redoc",
)

# Mock database of valid API keys
VALID_API_KEYS = ["your_api_key_here"]  # Replace with your valid API keys

# Dependency for API key authentication
def get_api_key(x_api_key: str = Header(...)):
    if x_api_key not in VALID_API_KEYS:
        raise HTTPException(status_code=401, detail="Invalid or missing API key")
    return x_api_key


### Manager Operations ###

class TransactionType(str, Enum):
    SALES = "SALES"
    INVENTORY = "INVENTORY"
    EXPENSES = "EXPENSES"
    INITIAL_FUNDS = "INITIAL_FUNDS"
    DISBURSEMENT = "DISBURSEMENT"

class Transaction(BaseModel):
    tx_id: str
    store_id: int
    item: Optional[str]
    amount: Decimal
    type: Optional[TransactionType]
    to_entity: Optional[str]
    date: Optional[date]
    version: int

# In-memory database simulation
transactions_db = []

@app.post("/api/manager/transactions", response_model=Transaction, tags=["Manager Operations"], status_code=201)
async def add_transaction(transaction: Transaction, api_key: str = Depends(get_api_key)):
    """Add a new transaction to the Counting and Sorting system."""
    transactions_db.append(transaction)
    return transaction

@app.get("/api/manager/stores/{store_id}/balance", response_model=dict, tags=["Manager Operations"])
async def get_store_balance(store_id: int, api_key: str = Depends(get_api_key)):
    """Get the balance for a specific store."""
    balance = sum(t.amount for t in transactions_db if t.store_id == store_id)
    return {"store_id": store_id, "balance": balance}

@app.get("/api/manager/stores/{store_id}/total_expenses", response_model=dict, tags=["Manager Operations"])
async def get_total_expenses_for_store(store_id: int, api_key: str = Depends(get_api_key)):
    """Get total expenses for a specific store."""
    total_expenses = sum(t.amount for t in transactions_db if t.store_id == store_id and t.amount < 0)
    return {"store_id": store_id, "total_expenses": total_expenses}

@app.get("/api/manager/stores/{store_id}/total_sales", response_model=dict, tags=["Manager Operations"])
async def get_total_sales_for_store(store_id: int, api_key: str = Depends(get_api_key)):
    """Get total sales for a specific store."""
    total_sales = sum(t.amount for t in transactions_db if t.store_id == store_id and t.type == TransactionType.SALES)
    return {"store_id": store_id, "total_sales": total_sales}

@app.get("/api/manager/stores/{store_id}/cost_breakdown", response_model=Dict[str, Decimal], tags=["Manager Operations"])
async def get_cost_breakdown_for_store(store_id: int, api_key: str = Depends(get_api_key)):
    """Get cost breakdown for a specific store."""
    cost_breakdown = {}
    for t in transactions_db:
        if t.store_id == store_id and t.amount < 0:
            cost_breakdown[t.item] = cost_breakdown.get(t.item, Decimal(0)) + t.amount
    return cost_breakdown

@app.get("/api/manager/stores/{store_id}/revenue_breakdown", response_model=Dict[str, Decimal], tags=["Manager Operations"])
async def get_revenue_breakdown_for_store(store_id: int, api_key: str = Depends(get_api_key)):
    """Get revenue breakdown for a specific store."""
    revenue_breakdown = {}
    for t in transactions_db:
        if t.store_id == store_id and t.amount > 0:
            revenue_breakdown[t.item] = revenue_breakdown.get(t.item, Decimal(0)) + t.amount
    return revenue_breakdown

@app.get("/api/manager/stores/{store_id}/profit", response_model=dict, tags=["Manager Operations"])
async def get_profit_for_store(store_id: int, api_key: str = Depends(get_api_key)):
    """Get profit for a specific store."""
    total_balance = sum(t.amount for t in transactions_db if t.store_id == store_id)
    initial_funds = sum(t.amount for t in transactions_db if t.store_id == store_id and t.type == TransactionType.INITIAL_FUNDS)
    profit = total_balance - initial_funds
    return {"store_id": store_id, "profit": profit}

@app.get("/api/manager/stores/{store_id}/transactions", response_model=List[Transaction], tags=["Manager Operations"])
async def get_transactions_for_store(store_id: int, api_key: str = Depends(get_api_key)):
    """Get all transactions for a specific store."""
    transactions = [t for t in transactions_db if t.store_id == store_id]
    if not transactions:
        raise HTTPException(status_code=404, detail="Store not found")
    return transactions


### Admin Operations ###

class WebscrapedProduct(BaseModel):
    id: Optional[int]
    title: str
    type: Optional[str]
    price: Decimal
    supplier: Optional[str]
    appended_date: Optional[datetime]
    category: Optional[str]
    name: Optional[str]
    qty: Optional[int]
    sale_price: Optional[float]
    quantity: Optional[int]

class Supplier(BaseModel):
    id: Optional[int]
    title: str
    address: str
    supplier: str
    appended_date: Optional[datetime]

# In-memory databases for products and suppliers
products_db = []
suppliers_db = []

@app.post("/api/admin/products", response_model=WebscrapedProduct, tags=["Admin Operations"], status_code=201)
async def add_product(product: WebscrapedProduct, api_key: str = Depends(get_api_key)):
    """Add a new product to the webscraped products table."""
    product.id = len(products_db) + 1
    product.appended_date = datetime.utcnow()
    products_db.append(product)
    return product

@app.get("/api/admin/products", response_model=List[WebscrapedProduct], tags=["Admin Operations"])
async def get_all_products(api_key: str = Depends(get_api_key)):
    """Get all products from the webscraped products table."""
    if not products_db:
        raise HTTPException(status_code=404, detail="No products found")
    return products_db

@app.post("/api/admin/suppliers", response_model=Supplier, tags=["Admin Operations"], status_code=201)
async def add_supplier(supplier: Supplier, api_key: str = Depends(get_api_key)):
    """Add a new supplier to the suppliers table."""
    supplier.id = len(suppliers_db) + 1
    supplier.appended_date = datetime.utcnow()
    suppliers_db.append(supplier)
    return supplier

@app.get("/api/admin/suppliers", response_model=List[Supplier], tags=["Admin Operations"])
async def get_all_suppliers(api_key: str = Depends(get_api_key)):
    """Get all suppliers from the suppliers table."""
    if not suppliers_db:
        raise HTTPException(status_code=404, detail="No suppliers found")
    return suppliers_db


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
