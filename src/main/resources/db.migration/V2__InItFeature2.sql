-- Creating Product Categories Table

CREATE SEQUENCE product_category_id_seq START 1;
CREATE TABLE product_categories (
    product_category_id SERIAL PRIMARY KEY,
    category_name VARCHAR(255) NOT NULL,
    category_picture VARCHAR(255),
    deleted_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- Creating Products Table
CREATE SEQUENCE product_id_seq START 1;
CREATE TABLE products (
    product_id SERIAL PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    detail VARCHAR(255) NOT NULL,
    price NUMERIC(10, 0) NOT NULL,
    weight NUMERIC(10, 0) NOT NULL,
    total_stock NUMERIC(10, 0) NOT NULL,
    deleted_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- Creating Product Pictures Table
CREATE SEQUENCE product_picture_id_seq START 1;
CREATE TABLE product_pictures (
    product_picture_id SERIAL PRIMARY KEY,
    product_picture_url VARCHAR(255) NOT NULL,
    is_main BOOLEAN DEFAULT FALSE,
    position INTEGER NOT NULL
    deleted_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- Creating Warehouse Stocks Table
CREATE SEQUENCE warehouse_stock_id_seq START 1;
CREATE TABLE warehouse_stocks (
    warehouse_stock_id SERIAL PRIMARY KEY,
    stock_quantity NUMERIC(10, 0) NOT NULL,
    deleted_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- Creating Stock Statuses Table
CREATE SEQUENCE stock_status_id_seq START 1;
CREATE TABLE stock_statuses (
    stock_status_id SERIAL PRIMARY KEY,
    status VARCHAR(255) NOT NULL,
    deleted_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- Creating Stock Change Type Table
CREATE SEQUENCE stock_change_type_id_seq START 1;
CREATE TABLE stock_change_types (
    stock_change_type_id SERIAL PRIMARY KEY,
    change_type VARCHAR(255) NOT NULL,
    detail VARCHAR(255) NOT NULL,
    deleted_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- Creating Stock Journal Table (Log by Warehouse)
CREATE SEQUENCE stock_journal_id_seq START 1;
CREATE TABLE stock_journals (
    stock_journal_id SERIAL PRIMARY KEY,
    mutation_quantity NUMERIC(10, 0) NOT NULL CHECK (mutation_quantity > 0),
    previous_stock_quantity NUMERIC(10, 0) NOT NULL,
    new_stock_quantity NUMERIC(10, 0) NOT NULL CHECK (new_stock_quantity >= 0),
    description VARCHAR(255),
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);