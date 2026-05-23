-- Add Discount and VAT columns to sale table

ALTER TABLE sale ADD COLUMN IF NOT EXISTS discount_percent DECIMAL(5,2) DEFAULT 0;
ALTER TABLE sale ADD COLUMN IF NOT EXISTS discount_amount DECIMAL(10,2) DEFAULT 0;
ALTER TABLE sale ADD COLUMN IF NOT EXISTS vat_amount DECIMAL(10,2) DEFAULT 0;
ALTER TABLE sale ADD COLUMN IF NOT EXISTS subtotal DECIMAL(10,2) DEFAULT 0;

-- Update existing sales to have 0 discount and calculate VAT retroactively
UPDATE sale 
SET discount_percent = 0,
    discount_amount = 0,
    subtotal = total_amount / 1.18,
    vat_amount = total_amount - (total_amount / 1.18)
WHERE discount_percent IS NULL;

-- Verify changes
SELECT sale_id, sale_date, subtotal, discount_percent, discount_amount, vat_amount, total_amount 
FROM sale 
ORDER BY sale_date DESC 
LIMIT 5;

-- Show table structure
SELECT column_name, data_type, character_maximum_length, is_nullable, column_default
FROM information_schema.columns
WHERE table_name = 'sale'
ORDER BY ordinal_position;
