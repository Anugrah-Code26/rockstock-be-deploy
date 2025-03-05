ALTER TABLE users
ADD COLUMN user_provider_id INT REFERENCES user_providers(user_provider_id) ON DELETE CASCADE;

ALTER TABLE user_roles
ADD COLUMN role_id INT REFERENCES roles(role_id) ON DELETE CASCADE,
ADD COLUMN user_id INT REFERENCES users(user_id) ON DELETE CASCADE;

ALTER TABLE cities 
ADD COLUMN province_id INT REFERENCES provinces(province_id) ON DELETE CASCADE;

ALTER TABLE addresses 
ADD COLUMN user_id INT REFERENCES users(user_id) ON DELETE CASCADE,
ADD COLUMN city_id INT REFERENCES cities(city_id) ON DELETE CASCADE;

ALTER TABLE warehouses 
ADD COLUMN city_id INT REFERENCES cities(city_id) ON DELETE CASCADE;

ALTER TABLE warehouse_admins 
ADD COLUMN user_id INT REFERENCES users(user_id) ON DELETE CASCADE,
ADD COLUMN warehouse_id INT REFERENCES warehouses(warehouse_id) ON DELETE CASCADE;

ALTER TABLE products 
ADD COLUMN product_category_id INT REFERENCES product_categories(product_category_id) ON DELETE CASCADE;

ALTER TABLE product_pictures 
ADD COLUMN product_id INT REFERENCES products(product_id) ON DELETE CASCADE;

ALTER TABLE warehouse_stocks
ADD COLUMN warehouse_id INT REFERENCES warehouses(warehouse_id) ON DELETE CASCADE,
ADD COLUMN product_id INT REFERENCES products(product_id) ON DELETE CASCADE;

ALTER TABLE stock_journals
ADD COLUMN warehouse_stock_id INT REFERENCES warehouse_stocks(warehouse_stock_id) ON DELETE CASCADE,
ADD COLUMN origin_warehouse_id INT REFERENCES warehouses(warehouse_id) ON DELETE CASCADE,
ADD COLUMN destination_warehouse_id INT REFERENCES warehouses(warehouse_id) ON DELETE CASCADE,
ADD COLUMN stock_change_type_id INT REFERENCES stock_change_types(stock_change_type_id) ON DELETE CASCADE,
ADD COLUMN stock_statuses_id INT REFERENCES stock_statuses(stock_statuses_id) ON DELETE CASCADE;

ALTER TABLE carts 
ADD COLUMN user_id INT REFERENCES users(user_id) ON DELETE CASCADE;

ALTER TABLE cart_items 
ADD COLUMN cart_id INT REFERENCES carts(cart_id) ON DELETE CASCADE,
ADD COLUMN product_id INT REFERENCES products(product_id) ON DELETE CASCADE;

ALTER TABLE payment_methods 
ADD COLUMN payment_category_id INT REFERENCES payment_categories(payment_category_id) ON DELETE CASCADE;

ALTER TABLE orders 
ADD COLUMN user_id INT REFERENCES users(user_id) ON DELETE CASCADE,
ADD COLUMN payment_method_id INT REFERENCES payment_methods(payment_method_id) ON DELETE CASCADE,
ADD COLUMN order_status_id INT REFERENCES order_statuses(order_status_id) ON DELETE CASCADE;

ALTER TABLE order_items 
ADD COLUMN order_id INT REFERENCES orders(order_id) ON DELETE CASCADE,
ADD COLUMN product_id INT REFERENCES products(product_id) ON DELETE CASCADE;
