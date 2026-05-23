-- ============================================
-- AUTO-GENERATE BARCODE TRIGGER
-- ============================================
-- This trigger automatically creates barcodes in format: SIM + (product_id + 99)
-- Example: product_id=1 → barcode=SIM100
--          product_id=2 → barcode=SIM101
--          product_id=790 → barcode=SIM889
-- Run this ONCE in your database after importing products

-- Step 1: Create the trigger function
CREATE OR REPLACE FUNCTION generate_barcode()
RETURNS TRIGGER AS $$
BEGIN
    -- If barcode is empty or NULL, generate it automatically
    IF NEW.barcode IS NULL OR NEW.barcode = '' OR NEW.barcode = 'TEMP' THEN
        NEW.barcode := 'SIM' || CAST((NEW.product_id + 99) AS TEXT);
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Step 2: Create the trigger on product table
DROP TRIGGER IF EXISTS auto_generate_barcode ON product;

CREATE TRIGGER auto_generate_barcode
    BEFORE INSERT OR UPDATE ON product
    FOR EACH ROW
    EXECUTE FUNCTION generate_barcode();

-- ============================================
-- TEST THE TRIGGER
-- ============================================
-- Try inserting a product without barcode:
-- INSERT INTO product (name, barcode, price, stock_quantity, reorder_level, category_id)
-- VALUES ('Test Product', '', 1000, 50, 10, 1);
-- 
-- Check the result:
-- SELECT product_id, name, barcode FROM product WHERE name = 'Test Product';
-- 
-- You should see barcode auto-generated as SIM800 or higher!

-- ============================================
-- VERIFICATION
-- ============================================
SELECT 'Trigger created successfully! New products will auto-generate barcodes.' AS status;
