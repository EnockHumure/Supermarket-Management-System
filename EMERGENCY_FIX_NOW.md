# EMERGENCY FIX GUIDE - Get Everything Working NOW!

## Problem: No data showing, buttons not working

## SOLUTION - Follow These Steps EXACTLY:

### Step 1: Fix Database (5 minutes)

1. **Open pgAdmin**
2. **Connect to supermarketdb**
3. **Open Query Tool**
4. **Run this ONE command:**

```sql
-- Add missing columns
ALTER TABLE sale ADD COLUMN IF NOT EXISTS discount_percent DECIMAL(5,2) DEFAULT 0;
ALTER TABLE sale ADD COLUMN IF NOT EXISTS discount_amount DECIMAL(10,2) DEFAULT 0;
ALTER TABLE sale ADD COLUMN IF NOT EXISTS vat_amount DECIMAL(10,2) DEFAULT 0;
ALTER TABLE sale ADD COLUMN IF NOT EXISTS subtotal DECIMAL(10,2) DEFAULT 0;
```

5. **Check if you have data:**

```sql
SELECT COUNT(*) FROM product;
SELECT COUNT(*) FROM category;
SELECT COUNT(*) FROM sale;
```

6. **If counts are 0 or very low, add sample data:**

```sql
-- Add categories
INSERT INTO category (name, description) VALUES 
('Beverages', 'Drinks'),
('Dairy', 'Milk products'),
('Bakery', 'Bread'),
('Snacks', 'Chips')
ON CONFLICT DO NOTHING;

-- Add products (change category_id if needed)
INSERT INTO product (name, barcode, price, stock_quantity, reorder_level, category_id) VALUES 
('Coca Cola', 'BAR001', 500.00, 100, 20, (SELECT category_id FROM category WHERE name='Beverages' LIMIT 1)),
('Milk 1L', 'BAR002', 1200.00, 50, 10, (SELECT category_id FROM category WHERE name='Dairy' LIMIT 1)),
('Bread', 'BAR003', 800.00, 30, 5, (SELECT category_id FROM category WHERE name='Bakery' LIMIT 1)),
('Chips', 'BAR004', 600.00, 80, 15, (SELECT category_id FROM category WHERE name='Snacks' LIMIT 1)),
('Water', 'BAR005', 300.00, 200, 50, (SELECT category_id FROM category WHERE name='Beverages' LIMIT 1));
```

### Step 2: Rebuild Server (2 minutes)

1. **Close server if running**
2. **Open NetBeans**
3. **Open SuperMarketManagmentSystemRMIServer project**
4. **Right-click project → Clean and Build**
5. **Right-click project → Run**
6. **Wait for "Server started" message**

### Step 3: Rebuild Client (2 minutes)

1. **Open SuperMarketManagmentSysRMIClient27394 project**
2. **Right-click project → Clean and Build**
3. **Right-click project → Run**

### Step 4: Test Everything (5 minutes)

1. **Login as admin/admin**
2. **Check Dashboard:**
   - Should see 6 ROI cards on left
   - Should see 3 charts on right
   - Click "Refresh Dashboard"

3. **Test Process Sale:**
   - Click "Process Sale" button
   - Scan barcode: BAR001
   - Add to cart
   - Enter payment
   - Click "Process Sale"

4. **Test Reports:**
   - Click "Reports" button
   - Click "All Sales"
   - Should see sales in table
   - Click "Export CSV"

5. **Test Charts:**
   - Go back to dashboard
   - Check "Stock Status" tab - should show bars
   - Check "Category Distribution" tab - should show pie chart
   - Check "Sales Trend" tab - should show line

---

## If Process Sale Button Not Working:

The button IS working, but you need to:
1. **Add products to cart first** (scan barcode)
2. **Cart must have items**
3. **Then click Process Sale**

---

## If Charts Show "No Data":

This means:
- **No products** in database → Add products (see Step 1)
- **No sales** in database → Process some sales first
- **No categories** → Add categories (see Step 1)

---

## If Reports Show Nothing:

This means:
- **No sales** in database
- **Solution:** Process 2-3 sales first, then check reports

---

## Quick Test Data Script (Copy-Paste in pgAdmin):

```sql
-- Run this to add test data quickly
DELETE FROM sale_item;
DELETE FROM sale;
DELETE FROM product;
DELETE FROM category;

-- Add categories
INSERT INTO category (name, description) VALUES 
('Beverages', 'Drinks'),
('Dairy', 'Milk'),
('Bakery', 'Bread'),
('Snacks', 'Chips');

-- Add products
INSERT INTO product (name, barcode, price, stock_quantity, reorder_level, category_id) 
SELECT 'Coca Cola', 'BAR001', 500, 100, 20, category_id FROM category WHERE name='Beverages'
UNION ALL
SELECT 'Milk', 'BAR002', 1200, 50, 10, category_id FROM category WHERE name='Dairy'
UNION ALL
SELECT 'Bread', 'BAR003', 800, 30, 5, category_id FROM category WHERE name='Bakery'
UNION ALL
SELECT 'Chips', 'BAR004', 600, 80, 15, category_id FROM category WHERE name='Snacks'
UNION ALL
SELECT 'Water', 'BAR005', 300, 200, 50, category_id FROM category WHERE name='Beverages';

-- Verify
SELECT 'Categories' as item, COUNT(*) as count FROM category
UNION ALL
SELECT 'Products', COUNT(*) FROM product;
```

---

## Expected Results After Fix:

✅ **Dashboard:** 6 ROI cards + 3 charts with data
✅ **Process Sale:** Button works, can add products and complete sale
✅ **Reports:** Shows sales with products in "Name×Qty" format
✅ **Export:** CSV export works
✅ **Charts:** All 3 charts show real data

---

## Still Not Working?

**Check Server Console for errors:**
- Look for "Connection refused"
- Look for "Table not found"
- Look for "Column not found"

**Check Client Console for errors:**
- Look for RMI errors
- Look for NullPointerException

**Send me the error message and I'll fix it immediately!**

---

## Summary:

1. ✅ Add database columns (ALTER TABLE)
2. ✅ Add sample data (INSERT INTO)
3. ✅ Rebuild server
4. ✅ Rebuild client
5. ✅ Test everything

**Time needed: 10-15 minutes total**

---

*This will get EVERYTHING working with real data!*
