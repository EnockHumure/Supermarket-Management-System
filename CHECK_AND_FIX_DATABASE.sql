-- STEP 1: Check if discount/VAT columns exist
-- Run this first in pgAdmin

SELECT column_name, data_type 
FROM information_schema.columns 
WHERE table_name = 'sale' 
ORDER BY ordinal_position;

-- STEP 2: Add columns if missing (run only if columns don't exist)
ALTER TABLE sale ADD COLUMN IF NOT EXISTS discount_percent DECIMAL(5,2) DEFAULT 0;
ALTER TABLE sale ADD COLUMN IF NOT EXISTS discount_amount DECIMAL(10,2) DEFAULT 0;
ALTER TABLE sale ADD COLUMN IF NOT EXISTS vat_amount DECIMAL(10,2) DEFAULT 0;
ALTER TABLE sale ADD COLUMN IF NOT EXISTS subtotal DECIMAL(10,2) DEFAULT 0;

-- STEP 3: Check existing data
SELECT COUNT(*) as total_products FROM product;
SELECT COUNT(*) as total_categories FROM category;
SELECT COUNT(*) as total_sales FROM sale;
SELECT COUNT(*) as total_users FROM users;

-- STEP 4: Check if you have products
SELECT product_id, name, price, stock_quantity, barcode FROM product LIMIT 10;

-- STEP 5: Check if you have categories
SELECT category_id, name, description FROM category LIMIT 10;

-- STEP 6: Check sales
SELECT sale_id, sale_date, total_amount, payment_method FROM sale ORDER BY sale_date DESC LIMIT 10;

-- STEP 7: If NO DATA exists, add sample data
-- Only run this if you have NO products/categories

-- Add categories (if empty)
INSERT INTO category (name, description) VALUES 
('Beverages', 'Drinks and beverages'),
('Dairy', 'Milk and dairy products'),
('Bakery', 'Bread and baked goods'),
('Snacks', 'Chips and snacks')
ON CONFLICT DO NOTHING;

-- Add products (if empty) - adjust category_id based on your data
INSERT INTO product (name, barcode, price, stock_quantity, reorder_level, category_id) VALUES 
('Coca Cola 500ml', 'BAR001', 500.00, 100, 20, 1),
('Milk 1L', 'BAR002', 1200.00, 50, 10, 2),
('Bread', 'BAR003', 800.00, 30, 5, 3),
('Chips', 'BAR004', 600.00, 80, 15, 4),
('Water 500ml', 'BAR005', 300.00, 200, 50, 1)
ON CONFLICT DO NOTHING;

-- STEP 8: Verify data was added
SELECT 'Products' as table_name, COUNT(*) as count FROM product
UNION ALL
SELECT 'Categories', COUNT(*) FROM category
UNION ALL
SELECT 'Sales', COUNT(*) FROM sale
UNION ALL
SELECT 'Users', COUNT(*) FROM users;
