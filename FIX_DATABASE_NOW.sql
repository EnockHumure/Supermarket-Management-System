-- SIMPLE FIX - Run this in pgAdmin NOW!
-- This adds the missing columns to your sale table

ALTER TABLE sale ADD COLUMN IF NOT EXISTS discount_percent DECIMAL(5,2) DEFAULT 0;
ALTER TABLE sale ADD COLUMN IF NOT EXISTS discount_amount DECIMAL(10,2) DEFAULT 0;
ALTER TABLE sale ADD COLUMN IF NOT EXISTS vat_amount DECIMAL(10,2) DEFAULT 0;
ALTER TABLE sale ADD COLUMN IF NOT EXISTS subtotal DECIMAL(10,2) DEFAULT 0;

-- Check if it worked
SELECT column_name FROM information_schema.columns WHERE table_name = 'sale';
