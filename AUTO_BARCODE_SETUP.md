# 🎯 AUTO-BARCODE SYSTEM - COMPLETE SETUP

## What You'll Get:
- **Imported products:** SIM800, SIM801, SIM802... → SIM1588 (789 products)
- **New products you add:** SIM1589, SIM1590, SIM1591... (auto-generates FOREVER!)

---

## 📋 STEP-BY-STEP SETUP

### **STEP 1: Run SimbaDataImporter.java**
1. Open NetBeans
2. Find: `src/util/SimbaDataImporter.java`
3. Right-click → **Run File**
4. Wait for: "✅ SUCCESS! SQL file created..."

---

### **STEP 2: Clear Database**
Open pgAdmin, connect to `supermarket_management_system_db`, run:

```sql
-- Clear existing data
DELETE FROM sale_item;
DELETE FROM sale;
DELETE FROM product;
DELETE FROM category;

-- Reset sequences
ALTER SEQUENCE category_category_id_seq RESTART WITH 1;
ALTER SEQUENCE product_product_id_seq RESTART WITH 1;
```

---

### **STEP 3: Import Products**
1. In pgAdmin Query Tool, click **Open File** (📁)
2. Select: `import_simba_products.sql`
3. Click **Execute** (▶️ or F5)
4. Wait 10-30 seconds...

---

### **STEP 4: Create Auto-Barcode Trigger**
1. In pgAdmin Query Tool, click **Open File** (📁)
2. Select: `create_barcode_trigger.sql`
3. Click **Execute** (▶️ or F5)
4. You should see: "Trigger created successfully!"

---

### **STEP 5: Verify Everything Works**

Run these queries:

```sql
-- Check imported products
SELECT product_id, name, barcode, price 
FROM product 
ORDER BY product_id 
LIMIT 10;
```

**Expected result:**
```
product_id | name                          | barcode
-----------+-------------------------------+---------
1          | Lentz Radiant Heater 80036    | SIM800
2          | Simba SS Icecream Scoop...    | SIM801
3          | Simba SS Shovel...            | SIM802
...
```

---

### **STEP 6: Test Auto-Generation**

Add a new product manually:

```sql
-- Insert without specifying barcode
INSERT INTO product (name, barcode, price, stock_quantity, reorder_level, category_id)
VALUES ('My Test Product', '', 5000, 100, 20, 1);

-- Check what barcode was generated
SELECT product_id, name, barcode 
FROM product 
WHERE name = 'My Test Product';
```

**Expected result:**
```
product_id | name              | barcode
-----------+-------------------+---------
790        | My Test Product   | SIM1589
```

✅ **IT WORKS! Barcode auto-generated as SIM1589!**

---

## 🎉 DONE! Now What?

### **In Your Application:**

When you add a product through your Java application:
1. Leave barcode field **empty** or set to **empty string**
2. Database trigger will **automatically** generate: SIM + (product_id + 799)
3. No code changes needed in your Java application!

---

## 📊 BARCODE MAPPING

| Product ID | Barcode | Source |
|------------|---------|--------|
| 1-789      | SIM800-SIM1588 | Imported from Simba |
| 790        | SIM1589 | Your first manual product |
| 791        | SIM1590 | Your second manual product |
| 792        | SIM1591 | Your third manual product |
| ...        | ...     | Continues forever! |

---

## ⚠️ IMPORTANT NOTES

### **When Adding Products in Your App:**
- **Option 1:** Leave barcode field empty → Auto-generates
- **Option 2:** Enter custom barcode → Uses your barcode
- **Option 3:** Enter "TEMP" → Auto-generates

### **The Trigger Runs:**
- ✅ When inserting new products
- ✅ When updating products with empty barcode
- ❌ Does NOT change existing barcodes (unless empty)

---

## 🔧 TROUBLESHOOTING

### **Problem: Barcode not auto-generating**
**Solution:** Make sure trigger was created. Run:
```sql
SELECT * FROM pg_trigger WHERE tgname = 'auto_generate_barcode';
```
If empty, run `create_barcode_trigger.sql` again.

### **Problem: Duplicate barcode error**
**Solution:** The trigger ensures unique barcodes. If you get this error, check if you manually entered a duplicate barcode.

---

## ✅ CHECKLIST

- [ ] Ran SimbaDataImporter.java
- [ ] Cleared database
- [ ] Imported products (import_simba_products.sql)
- [ ] Created trigger (create_barcode_trigger.sql)
- [ ] Verified products have SIM800, SIM801, etc.
- [ ] Tested adding new product
- [ ] Confirmed new product got SIM1589 or higher
- [ ] Restarted RMI Server (for 60-min session timeout)
- [ ] Tested in application

---

**Everything working? You're ready to go! 🚀**
