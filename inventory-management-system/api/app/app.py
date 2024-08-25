from flask import Flask, request, jsonify
from flask_sqlalchemy import SQLAlchemy
from flasgger import Swagger, swag_from
from sqlalchemy import func
from sqlalchemy.orm import sessionmaker
from sqlalchemy.exc import SQLAlchemyError
from datetime import datetime

app = Flask(__name__)

# MySQL Database configuration
app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql+pymysql://username:password@localhost/db_name'  # Replace with your MySQL credentials
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
db = SQLAlchemy(app)

# Swagger configuration
swagger = Swagger(app, template={
    "swagger": "2.0",
    "info": {
        "title": "Counting and Sorting API",
        "description": "This API allows for managing transactions and balances within the Counting and Sorting system. It is divided into sections for Managers (who manage individual stores) and Admins (who oversee all stores).",
        "version": "1.2.0",
        "termsOfService": "http://example.com/terms/",
        "contact": {
            "name": "Counting and Sorting Support",
            "url": "http://example.com/support",
            "email": "support@example.com"
        },
        "license": {
            "name": "Apache 2.0",
            "url": "http://www.apache.org/licenses/LICENSE-2.0.html"
        }
    },
    "host": "localhost:8000",
    "basePath": "/api",
    "schemes": ["http", "https"],
    "securityDefinitions": {
        "ApiKeyAuth": {
            "type": "apiKey",
            "name": "x-api-key",
            "in": "header",
            "description": "API key required for authentication"
        }
    },
    "security": [
        {
            "ApiKeyAuth": []
        }
    ],
    "tags": [
        {
            "name": "Manager Operations",
            "description": "Endpoints for managing transactions and balances for individual stores."
        },
        {
            "name": "Admin Operations",
            "description": "Endpoints for overseeing transactions and balances across all stores."
        }
    ],
})


# API Key model
class APIKey(db.Model):
    __tablename__ = 'api_keys'

    id = db.Column(db.BigInteger, primary_key=True)
    access_level = db.Column(db.String(255), nullable=False)
    description = db.Column(db.String(255), nullable=False)
    key_value = db.Column(db.String(255), nullable=False, unique=True)
    store_id = db.Column(db.String(255), nullable=False)

# Transaction model
class Transaction(db.Model):
    __tablename__ = 'transactions'
    
    id = db.Column(db.BigInteger, primary_key=True)
    tx_id = db.Column(db.String(12), nullable=False, unique=True)
    store_id = db.Column(db.Integer, nullable=False)
    item = db.Column(db.String(50), nullable=True)
    amount = db.Column(db.Numeric(38, 2), nullable=False)
    type = db.Column(db.Enum('SALES', 'INVENTORY', 'EXPENSES', 'INITIAL_FUNDS', 'DISBURSEMENT'), nullable=True)
    to_entity = db.Column(db.String(50), nullable=True)
    date = db.Column(db.Date, nullable=True)
    version = db.Column(db.Integer, nullable=False)

# Webscraped Products model
class WebscrapedProduct(db.Model):
    __tablename__ = 'webscraped_products'

    id = db.Column(db.BigInteger, primary_key=True)
    title = db.Column(db.String(255), nullable=False)
    type = db.Column(db.String(50), nullable=True)
    price = db.Column(db.Numeric(10, 2), nullable=False)
    supplier = db.Column(db.String(100), nullable=True)
    appended_date = db.Column(db.TIMESTAMP, default=datetime.utcnow, nullable=True)
    category = db.Column(db.String(255), nullable=True)
    name = db.Column(db.String(255), nullable=True)
    qty = db.Column(db.Integer, nullable=True)
    sale_price = db.Column(db.Float, nullable=True)
    quantity = db.Column(db.Integer, nullable=True)

# Supplier model
class Supplier(db.Model):
    __tablename__ = 'suppliers'

    id = db.Column(db.Integer, primary_key=True)
    title = db.Column(db.String(255), nullable=False)
    address = db.Column(db.String(255), nullable=False)
    supplier = db.Column(db.String(100), nullable=False)
    appended_date = db.Column(db.TIMESTAMP, default=datetime.utcnow, nullable=True)

# Initialize the database (uncomment if running for the first time)
# db.create_all()



def authenticate_api_key():
    """Middleware to authenticate the API key for each request."""
    api_key = request.headers.get('x-api-key')
    if not api_key:
        return jsonify({'error': 'API key is missing'}), 401

    key_record = APIKey.query.filter_by(key_value=api_key).first()
    if not key_record:
        return jsonify({'error': 'Invalid API key'}), 401

    request.store_id = key_record.store_id
    request.access_level = key_record.access_level


@app.before_request
def before_request():
    # Skip authentication for Swagger docs endpoints
    if request.path.startswith('/apidocs') or request.path.startswith('/swagger') or request.path.startswith('/redoc'):
        return

    auth_response = authenticate_api_key()
    if auth_response:
        return auth_response


### Manager Operations ###

@app.route('/api/manager/stores/<int:store_id>/total_expenses', methods=['GET'])
@swag_from({
    'tags': ['Manager Operations'],
    'summary': 'Get total expenses for a specific store',
    'description': 'This endpoint retrieves the total expenses for a specified store. Expenses are identified as transactions where the amount is negative.',
    'parameters': [
        {
            'name': 'store_id',
            'in': 'path',
            'type': 'integer',
            'required': True,
            'description': 'ID of the store to retrieve total expenses for'
        },
        {
            'name': 'x-api-key',
            'in': 'header',
            'type': 'string',
            'required': True,
            'description': 'API key required for authentication'
        }
    ],
    'security': [
        {
            "ApiKeyAuth": []
        }
    ],
    'responses': {
        '200': {
            'description': 'Total expenses retrieved successfully',
            'schema': {
                'type': 'object',
                'properties': {
                    'store_id': {'type': 'integer', 'description': 'ID of the store'},
                    'total_expenses': {'type': 'number', 'format': 'double', 'description': 'Total expenses for the store'}
                }
            }
        },
        '403': {
            'description': 'Unauthorized to access this store data',
            'schema': {
                'type': 'object',
                'properties': {
                    'error': {'type': 'string', 'example': 'Unauthorized to access this store data'}
                }
            }
        },
        '404': {
            'description': 'Store not found',
            'schema': {
                'type': 'object',
                'properties': {
                    'error': {'type': 'string', 'example': 'Store not found'}
                }
            }
        }
    }
})
def get_total_expenses_for_store(store_id):
    """Get total expenses for a specific store"""
    if request.store_id != str(store_id):
        return jsonify({'error': 'Unauthorized to access this store data'}), 403

    total_expenses = db.session.query(func.sum(Transaction.amount)).filter(
        Transaction.store_id == store_id, 
        Transaction.amount < 0
    ).scalar()
    
    if total_expenses is None:
        return jsonify({'error': 'Store not found'}), 404

    return jsonify({'store_id': store_id, 'total_expenses': float(total_expenses)}), 200



@app.route('/api/manager/stores/<int:store_id>/total_sales', methods=['GET'])
@swag_from({
    'tags': ['Manager Operations'],
    'summary': 'Get total sales for a specific store',
    'description': 'This endpoint retrieves the total sales for a specified store. Sales are identified as transactions where the type is "SALES".',
    'parameters': [
        {
            'name': 'store_id',
            'in': 'path',
            'type': 'integer',
            'required': True,
            'description': 'ID of the store to retrieve total sales for'
        }
    ],
    'responses': {
        '200': {
            'description': 'Total sales retrieved successfully',
            'schema': {
                'type': 'object',
                'properties': {
                    'store_id': {'type': 'integer', 'description': 'ID of the store'},
                    'total_sales': {'type': 'number', 'format': 'double', 'description': 'Total sales for the store'}
                }
            }
        },
        '404': {
            'description': 'Store not found',
            'schema': {
                'type': 'object',
                'properties': {
                    'error': {'type': 'string', 'example': 'Store not found'}
                }
            }
        }
    }
})
def get_total_sales_for_store(store_id):
    """Get total sales for a specific store"""
    if request.store_id != str(store_id):
        return jsonify({'error': 'Unauthorized to access this store data'}), 403

    total_sales = db.session.query(func.sum(Transaction.amount)).filter(
        Transaction.store_id == store_id,
        Transaction.type == 'SALES'
    ).scalar()
    
    if total_sales is None:
        return jsonify({'error': 'Store not found'}), 404

    return jsonify({'store_id': store_id, 'total_sales': float(total_sales)}), 200


@app.route('/api/manager/stores/<int:store_id>/cost_breakdown', methods=['GET'])
@swag_from({
    'tags': ['Manager Operations'],
    'summary': 'Get cost breakdown for a specific store',
    'description': 'This endpoint retrieves a breakdown of costs for a specified store, grouped by item.',
    'parameters': [
        {
            'name': 'store_id',
            'in': 'path',
            'type': 'integer',
            'required': True,
            'description': 'ID of the store to retrieve cost breakdown for'
        }
    ],
    'responses': {
        '200': {
            'description': 'Cost breakdown retrieved successfully',
            'schema': {
                'type': 'object',
                'properties': {
                    'store_id': {'type': 'integer', 'description': 'ID of the store'},
                    'cost_breakdown': {'type': 'object', 'additionalProperties': {'type': 'number'}, 'description': 'Breakdown of costs by item'}
                }
            }
        },
        '404': {
            'description': 'Store not found',
            'schema': {
                'type': 'object',
                'properties': {
                    'error': {'type': 'string', 'example': 'Store not found'}
                }
            }
        }
    }
})
def get_cost_breakdown_for_store(store_id):
    """Get cost breakdown for a specific store"""
    if request.store_id != str(store_id):
        return jsonify({'error': 'Unauthorized to access this store data'}), 403

    costs = db.session.query(Transaction.item, func.sum(Transaction.amount)).filter(
        Transaction.store_id == store_id, 
        Transaction.amount < 0
    ).group_by(Transaction.item).all()

    if not costs:
        return jsonify({'error': 'Store not found'}), 404

    cost_breakdown = {item: float(amount) for item, amount in costs}

    return jsonify({'store_id': store_id, 'cost_breakdown': cost_breakdown}), 200


@app.route('/api/manager/stores/<int:store_id>/revenue_breakdown', methods=['GET'])
@swag_from({
    'tags': ['Manager Operations'],
    'summary': 'Get revenue breakdown for a specific store',
    'description': 'This endpoint retrieves a breakdown of revenue for a specified store, grouped by item.',
    'parameters': [
        {
            'name': 'store_id',
            'in': 'path',
            'type': 'integer',
            'required': True,
            'description': 'ID of the store to retrieve revenue breakdown for'
        }
    ],
    'responses': {
        '200': {
            'description': 'Revenue breakdown retrieved successfully',
            'schema': {
                'type': 'object',
                'properties': {
                    'store_id': {'type': 'integer', 'description': 'ID of the store'},
                    'revenue_breakdown': {'type': 'object', 'additionalProperties': {'type': 'number'}, 'description': 'Breakdown of revenue by item'}
                }
            }
        },
        '404': {
            'description': 'Store not found',
            'schema': {
                'type': 'object',
                'properties': {
                    'error': {'type': 'string', 'example': 'Store not found'}
                }
            }
        }
    }
})
def get_revenue_breakdown_for_store(store_id):
    """Get revenue breakdown for a specific store"""
    if request.store_id != str(store_id):
        return jsonify({'error': 'Unauthorized to access this store data'}), 403

    revenues = db.session.query(Transaction.item, func.sum(Transaction.amount)).filter(
        Transaction.store_id == store_id, 
        Transaction.amount > 0
    ).group_by(Transaction.item).all()

    if not revenues:
        return jsonify({'error': 'Store not found'}), 404

    revenue_breakdown = {item: float(amount) for item, amount in revenues}

    return jsonify({'store_id': store_id, 'revenue_breakdown': revenue_breakdown}), 200


@app.route('/api/manager/stores/<int:store_id}/profit', methods=['GET'])
@swag_from({
    'tags': ['Manager Operations'],
    'summary': 'Get profit for a specific store',
    'description': 'This endpoint retrieves the profit for a specified store. Profit is calculated as the total balance minus initial funds.',
    'parameters': [
        {
            'name': 'store_id',
            'in': 'path',
            'type': 'integer',
            'required': True,
            'description': 'ID of the store to retrieve profit for'
        }
    ],
    'responses': {
        '200': {
            'description': 'Profit retrieved successfully',
            'schema': {
                'type': 'object',
                'properties': {
                    'store_id': {'type': 'integer', 'description': 'ID of the store'},
                    'profit': {'type': 'number', 'format': 'double', 'description': 'Profit for the store'}
                }
            }
        },
        '404': {
            'description': 'Store not found',
            'schema': {
                'type': 'object',
                'properties': {
                    'error': {'type': 'string', 'example': 'Store not found'}
                }
            }
        }
    }
})
def get_profit_for_store(store_id):
    """Get profit for a specific store"""
    if request.store_id != str(store_id):
        return jsonify({'error': 'Unauthorized to access this store data'}), 403

    total_balance = db.session.query(func.sum(Transaction.amount)).filter(
        Transaction.store_id == store_id
    ).scalar()
    
    initial_funds = db.session.query(func.sum(Transaction.amount)).filter(
        Transaction.store_id == store_id, 
        Transaction.type == 'INITIAL_FUNDS'
    ).scalar()

    if total_balance is None:
        return jsonify({'error': 'Store not found'}), 404

    profit = total_balance - (initial_funds or 0)

    return jsonify({'store_id': store_id, 'profit': float(profit)}), 200


@app.route('/api/manager/stores/<int:store_id>/transactions', methods=['GET'])
@swag_from({
    'tags': ['Manager Operations'],
    'summary': 'Get all transactions for a specific store',
    'description': 'This endpoint retrieves all transactions for a specified store.',
    'parameters': [
        {
            'name': 'store_id',
            'in': 'path',
            'type': 'integer',
            'required': True,
            'description': 'ID of the store to retrieve transactions for'
        }
    ],
    'responses': {
        '200': {
            'description': 'Transactions retrieved successfully',
            'schema': {
                'type': 'array',
                'items': {
                    'type': 'object',
                    'properties': {
                        'tx_id': {'type': 'string', 'description': 'Transaction ID'},
                        'store_id': {'type': 'integer', 'description': 'Store ID'},
                        'item': {'type': 'string', 'description': 'Item involved in the transaction'},
                        'amount': {'type': 'number', 'format': 'double', 'description': 'Transaction amount'},
                        'type': {'type': 'string', 'enum': ['SALES', 'INVENTORY', 'EXPENSES', 'INITIAL_FUNDS', 'DISBURSEMENT'], 'description': 'Type of transaction'},
                        'to_entity': {'type': 'string', 'description': 'Entity involved in the transaction'},
                        'date': {'type': 'string', 'format': 'date', 'description': 'Date of the transaction'}
                    }
                }
            }
        },
        '404': {
            'description': 'Store not found',
            'schema': {
                'type': 'object',
                'properties': {
                    'error': {'type': 'string', 'example': 'Store not found'}
                }
            }
        }
    }
})
def get_transactions_for_store(store_id):
    """Get all transactions for a specific store"""
    if request.store_id != str(store_id):
        return jsonify({'error': 'Unauthorized to access this store data'}), 403

    transactions = Transaction.query.filter_by(store_id=store_id).all()

    if not transactions:
        return jsonify({'error': 'Store not found'}), 404

    result = []
    for transaction in transactions:
        result.append({
            'tx_id': transaction.tx_id,
            'store_id': transaction.store_id,
            'item': transaction.item,
            'amount': float(transaction.amount),
            'type': transaction.type,
            'to_entity': transaction.to_entity,
            'date': transaction.date.isoformat() if transaction.date else None
        })

    return jsonify(result), 200


### Admin Operations ###

@app.route('/api/admin/total_expenses', methods=['GET'])
@swag_from({
    'tags': ['Admin Operations'],
    'summary': 'Get total expenses for all stores',
    'description': 'This endpoint retrieves the total expenses for all stores. Expenses are identified as transactions where the amount is negative.',
    'responses': {
        '200': {
            'description': 'Total expenses for all stores retrieved successfully',
            'schema': {
                'type': 'object',
                'additionalProperties': {'type': 'number', 'format': 'double', 'description': 'Total expenses for each store'}
            }
        }
    }
})
def get_total_expenses_for_all_stores():
    """Get total expenses for all stores"""
    if request.access_level != 'admin':
        return jsonify({'error': 'Unauthorized to access this data'}), 403

    expenses = db.session.query(Transaction.store_id, func.sum(Transaction.amount)).filter(
        Transaction.amount < 0
    ).group_by(Transaction.store_id).all()

    result = {store_id: float(total) for store_id, total in expenses}

    return jsonify(result), 200


@app.route('/api/admin/total_sales', methods=['GET'])
@swag_from({
    'tags': ['Admin Operations'],
    'summary': 'Get total sales for all stores',
    'description': 'This endpoint retrieves the total sales for all stores. Sales are identified as transactions where the type is "SALES".',
    'responses': {
        '200': {
            'description': 'Total sales for all stores retrieved successfully',
            'schema': {
                'type': 'object',
                'additionalProperties': {'type': 'number', 'format': 'double', 'description': 'Total sales for each store'}
            }
        }
    }
})
def get_total_sales_for_all_stores():
    """Get total sales for all stores"""
    if request.access_level != 'admin':
        return jsonify({'error': 'Unauthorized to access this data'}), 403

    sales = db.session.query(Transaction.store_id, func.sum(Transaction.amount)).filter(
        Transaction.type == 'SALES'
    ).group_by(Transaction.store_id).all()

    result = {store_id: float(total) for store_id, total in sales}

    return jsonify(result), 200


@app.route('/api/admin/account_balances', methods=['GET'])
@swag_from({
    'tags': ['Admin Operations'],
    'summary': 'Get account balances for all stores',
    'description': 'This endpoint retrieves the account balances for all stores. The balance is calculated as the sum of all transactions for each store.',
    'responses': {
        '200': {
            'description': 'Account balances for all stores retrieved successfully',
            'schema': {
                'type': 'object',
                'additionalProperties': {'type': 'number', 'format': 'double', 'description': 'Account balance for each store'}
            }
        }
    }
})
def get_account_balances_for_all_stores():
    """Get account balances for all stores"""
    if request.access_level != 'admin':
        return jsonify({'error': 'Unauthorized to access this data'}), 403

    balances = db.session.query(Transaction.store_id, func.sum(Transaction.amount)).group_by(Transaction.store_id).all()

    result = {store_id: float(total) for store_id, total in balances}

    return jsonify(result), 200


@app.route('/api/admin/profits', methods=['GET'])
@swag_from({
    'tags': ['Admin Operations'],
    'summary': 'Get profits for all stores',
    'description': 'This endpoint retrieves the profits for all stores. Profit is calculated as the total balance minus initial funds.',
    'responses': {
        '200': {
            'description': 'Profits for all stores retrieved successfully',
            'schema': {
                'type': 'object',
                'additionalProperties': {'type': 'number', 'format': 'double', 'description': 'Profit for each store'}
            }
        }
    }
})
def get_profits_for_all_stores():
    """Get profits for all stores"""
    if request.access_level != 'admin':
        return jsonify({'error': 'Unauthorized to access this data'}), 403

    total_balances = db.session.query(Transaction.store_id, func.sum(Transaction.amount)).group_by(Transaction.store_id).all()

    initial_funds = db.session.query(Transaction.store_id, func.sum(Transaction.amount)).filter(
        Transaction.type == 'INITIAL_FUNDS'
    ).group_by(Transaction.store_id).all()

    initial_funds_dict = {store_id: total for store_id, total in initial_funds}
    profits = {store_id: float(total) - float(initial_funds_dict.get(store_id, 0)) for store_id, total in total_balances}

    return jsonify(profits), 200


### Product and Supplier Management ###

@app.route('/api/admin/products', methods=['POST'])
@swag_from({
    'tags': ['Admin Operations'],
    'summary': 'Add a new product',
    'description': 'This endpoint allows an admin to add a new product to the webscraped products table.',
    'parameters': [
        {
            'name': 'body',
            'in': 'body',
            'required': True,
            'schema': {
                'type': 'object',
                'properties': {
                    'title': {'type': 'string', 'description': 'Product title'},
                    'type': {'type': 'string', 'description': 'Product type'},
                    'price': {'type': 'number', 'format': 'double', 'description': 'Product price'},
                    'supplier': {'type': 'string', 'description': 'Product supplier'},
                    'category': {'type': 'string', 'description': 'Product category'},
                    'name': {'type': 'string', 'description': 'Product name'},
                    'qty': {'type': 'integer', 'description': 'Product quantity'},
                    'sale_price': {'type': 'number', 'format': 'double', 'description': 'Sale price'},
                    'quantity': {'type': 'integer', 'description': 'Available quantity'}
                },
                'required': ['title', 'price']
            }
        }
    ],
    'responses': {
        '201': {
            'description': 'Product added successfully',
            'schema': {
                'type': 'object',
                'properties': {
                    'message': {'type': 'string', 'example': 'Product added successfully'}
                }
            }
        },
        '400': {
            'description': 'Bad Request',
            'schema': {
                'type': 'object',
                'properties': {
                    'error': {'type': 'string', 'example': 'Error message'}
                }
            }
        }
    }
})
def add_product():
    """Add a new product"""
    if request.access_level != 'admin':
        return jsonify({'error': 'Unauthorized to perform this action'}), 403

    data = request.json
    try:
        product = WebscrapedProduct(
            title=data['title'],
            type=data.get('type'),
            price=data['price'],
            supplier=data.get('supplier'),
            category=data.get('category'),
            name=data.get('name'),
            qty=data.get('qty'),
            sale_price=data.get('sale_price'),
            quantity=data.get('quantity'),
            appended_date=datetime.utcnow()
        )
        db.session.add(product)
        db.session.commit()
        return jsonify({'message': 'Product added successfully'}), 201
    except Exception as e:
        return jsonify({'error': str(e)}), 400


@app.route('/api/admin/products', methods=['GET'])
@swag_from({
    'tags': ['Admin Operations'],
    'summary': 'Get all products',
    'description': 'This endpoint allows an admin to retrieve all products from the webscraped products table.',
    'responses': {
        '200': {
            'description': 'Products retrieved successfully',
            'schema': {
                'type': 'array',
                'items': {
                    'type': 'object',
                    'properties': {
                        'id': {'type': 'integer', 'description': 'Product ID'},
                        'title': {'type': 'string', 'description': 'Product title'},
                        'type': {'type': 'string', 'description': 'Product type'},
                        'price': {'type': 'number', 'format': 'double', 'description': 'Product price'},
                        'supplier': {'type': 'string', 'description': 'Product supplier'},
                        'appended_date': {'type': 'string', 'format': 'date-time', 'description': 'Date when the product was added'},
                        'category': {'type': 'string', 'description': 'Product category'},
                        'name': {'type': 'string', 'description': 'Product name'},
                        'qty': {'type': 'integer', 'description': 'Product quantity'},
                        'sale_price': {'type': 'number', 'format': 'double', 'description': 'Sale price'},
                        'quantity': {'type': 'integer', 'description': 'Available quantity'}
                    }
                }
            }
        },
        '404': {
            'description': 'No products found',
            'schema': {
                'type': 'object',
                'properties': {
                    'error': {'type': 'string', 'example': 'No products found'}
                }
            }
        }
    }
})
def get_all_products():
    """Get all products"""
    if request.access_level != 'admin':
        return jsonify({'error': 'Unauthorized to access this data'}), 403

    products = WebscrapedProduct.query.all()

    if not products:
        return jsonify({'error': 'No products found'}), 404

    result = []
    for product in products:
        result.append({
            'id': product.id,
            'title': product.title,
            'type': product.type,
            'price': float(product.price),
            'supplier': product.supplier,
            'appended_date': product.appended_date.isoformat() if product.appended_date else None,
            'category': product.category,
            'name': product.name,
            'qty': product.qty,
            'sale_price': product.sale_price,
            'quantity': product.quantity,
        })

    return jsonify(result), 200


@app.route('/api/admin/suppliers', methods=['POST'])
@swag_from({
    'tags': ['Admin Operations'],
    'summary': 'Add a new supplier',
    'description': 'This endpoint allows an admin to add a new supplier to the suppliers table.',
    'parameters': [
        {
            'name': 'body',
            'in': 'body',
            'required': True,
            'schema': {
                'type': 'object',
                'properties': {
                    'title': {'type': 'string', 'description': 'Supplier title'},
                    'address': {'type': 'string', 'description': 'Supplier address'},
                    'supplier': {'type': 'string', 'description': 'Supplier name'},
                },
                'required': ['title', 'address', 'supplier']
            }
        }
    ],
    'responses': {
        '201': {
            'description': 'Supplier added successfully',
            'schema': {
                'type': 'object',
                'properties': {
                    'message': {'type': 'string', 'example': 'Supplier added successfully'}
                }
            }
        },
        '400': {
            'description': 'Bad Request',
            'schema': {
                'type': 'object',
                'properties': {
                    'error': {'type': 'string', 'example': 'Error message'}
                }
            }
        }
    }
})
def add_supplier():
    """Add a new supplier"""
    if request.access_level != 'admin':
        return jsonify({'error': 'Unauthorized to perform this action'}), 403

    data = request.json
    try:
        supplier = Supplier(
            title=data['title'],
            address=data['address'],
            supplier=data['supplier'],
            appended_date=datetime.utcnow()
        )
        db.session.add(supplier)
        db.session.commit()
        return jsonify({'message': 'Supplier added successfully'}), 201
    except Exception as e:
        return jsonify({'error': str(e)}), 400


@app.route('/api/admin/suppliers', methods=['GET'])
@swag_from({
    'tags': ['Admin Operations'],
    'summary': 'Get all suppliers',
    'description': 'This endpoint allows an admin to retrieve all suppliers from the suppliers table.',
    'responses': {
        '200': {
            'description': 'Suppliers retrieved successfully',
            'schema': {
                'type': 'array',
                'items': {
                    'type': 'object',
                    'properties': {
                        'id': {'type': 'integer', 'description': 'Supplier ID'},
                        'title': {'type': 'string', 'description': 'Supplier title'},
                        'address': {'type': 'string', 'description': 'Supplier address'},
                        'supplier': {'type': 'string', 'description': 'Supplier name'},
                        'appended_date': {'type': 'string', 'format': 'date-time', 'description': 'Date when the supplier was added'},
                    }
                }
            }
        },
        '404': {
            'description': 'No suppliers found',
            'schema': {
                'type': 'object',
                'properties': {
                    'error': {'type': 'string', 'example': 'No suppliers found'}
                }
            }
        }
    }
})
def get_all_suppliers():
    """Get all suppliers"""
    if request.access_level != 'admin':
        return jsonify({'error': 'Unauthorized to access this data'}), 403

    suppliers = Supplier.query.all()

    if not suppliers:
        return jsonify({'error': 'No suppliers found'}), 404

    result = []
    for supplier in suppliers:
        result.append({
            'id': supplier.id,
            'title': supplier.title,
            'address': supplier.address,
            'supplier': supplier.supplier,
            'appended_date': supplier.appended_date.isoformat() if supplier.appended_date else None,
        })

    return jsonify(result), 200


if __name__ == '__main__':
    app.run(debug=True)
