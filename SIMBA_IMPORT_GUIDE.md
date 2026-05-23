# 📦 SIMBA PRODUCTS IMPORT GUIDE
## Step-by-Step Instructions for Beginners

---

## 🎯 What We're Doing

We're taking the 673 products from Simba Supermarket JSON file and importing them into your Supermarket Management System database.

---

## 📊 What Data We'll Use

### ✅ **Data We WILL Use:**
- **Product ID** → Used to generate barcode
- **Product Name** → Goes to your `name` field
- **Price** → Goes to your `price` field
- **Category** → Goes to your `category` table

### ❌ **Data We WON'T Use (Not in your system):**
- `subcategoryId` - You don't have subcategories
- `inStock` - You use `stockQuantity` instead
- `image` - You don't store images
- `unit` - Not implemented yet

### 🔧 **Data We'll GENERATE:**
- **Barcode** → Generated as "SIM" + 6-digit number (e.g., SIM013001)
- **Stock Quantity** → Random number between 10-100
- **Reorder Level** → Set to 20% of stock quantity

---

## 📝 STEP-BY-STEP INSTRUCTIONS

### **STEP 1: Add JSON Library to Your Project**

1. Download `json-simple-1.1.1.jar` from:
   - https://code.google.com/archive/p/json-simple/downloads
   - OR use Maven: https://mvnrepository.com/artifact/com.googlecode.json-simple/json-simple/1.1.1

2. Add the JAR to your project:
   - Right-click on `SuperMarketManagmentSystemRMIServer` project
   - Select **Properties**
   - Go to **Libraries**
   - Click **Add JAR/Folder**
   - Browse and select `json-simple-1.1.1.jar`
   - Click **OK**

---

### **STEP 2: Copy the Simba JSON File**

Copy the `simba_products.json` file to your project root:

```
From: C:\Users\humur\Desktop\A2SVOVERCOUNTRY\simba\simba_products.json
To:   C:\Users\humur\Documents\NetBeansProjects\SuperMarketManagementSystem\simba_products.json
```

**OR** update the path in `SimbaDataImporter.java` line 18 to point to the correct location.

---

### **STEP 3: Run the Importer**

1. Open NetBeans
2. Open the `SuperMarketManagmentSystemRMIServer` project
3. Find the file: `src/util/SimbaDataImporter.java`
4. Right-click on the file
5. Select **Run File** (or press Shift+F6)

**Expected Output:**
```
Starting import process...
Found 673 products in JSON file
Found 10 unique categories

✅ SUCCESS!
SQL file created at: C:\Users\humur\Documents\NetBeansProjects\SuperMarketManagementSystem\import_simba_products.sql

Next steps:
1. Open pgAdmin or your PostgreSQL client
2. Connect to your 'supermarket_db' database
3. Run the SQL file
4. Refresh your application to see the new products!
```

---

### **STEP 4: Import Data into Database**

#### **Option A: Using pgAdmin (Recommended for Beginners)**

1. Open **pgAdmin**
2. Connect to your PostgreSQL server
3. Navigate to: **Servers** → **PostgreSQL** → **Databases** → **supermarket_db**
4. Right-click on **supermarket_db**
5. Select **Query Tool**
6. Click **Open File** icon (folder icon)
7. Browse to: `C:\Users\humur\Documents\NetBeansProjects\SuperMarketManagementSystem\import_simba_products.sql`
8. Click **Execute** (Play button or F5)
9. Wait for completion (should take 10-30 seconds)

#### **Option B: Using Command Line**

```bash
psql -U postgres -d supermarket_db -f "C:\Users\humur\Documents\NetBeansProjects\SuperMarketManagementSystem\import_simba_products.sql"
```

---

### **STEP 5: Verify the Import**

Run these SQL queries in pgAdmin to verify:

```sql
-- Check how many categories were imported
SELECT COUNT(*) FROM category;
-- Should show 10 categories

-- Check how many products were imported
SELECT COUNT(*) FROM product;
-- Should show 673 products

-- View sample products
SELECT p.name, p.barcode, p.price, p.stock_quantity, c.name as category
FROM product p
JOIN category c ON p.category_id = c.category_id
LIMIT 10;
```

---

### **STEP 6: Test in Your Application**

1. Start your **RMI Server**
2. Start your **Client Application**
3. Login as Admin or Manager
4. Go to **Product Management**
5. You should see all 673 products!

---

## 🎨 CATEGORIES IMPORTED

The following categories will be created:

1. **Cosmetics & Personal Care**
2. **Sports & Wellness**
3. **Baby Products**
4. **Kitchenware & Electronics**
5. **Food Products**
6. **Alcoholic Drinks**
7. **Cleaning & Sanitary**
8. **General**
9. **Pet Care**
10. **Kitchen Storage**

---

## 📊 SAMPLE PRODUCTS

Here are some examples of what will be imported:

| Product Name | Barcode | Price (RWF) | Category |
|-------------|---------|-------------|----------|
| Lentz Radiant Heater 80036 | SIM013001 | 83,600 | Cosmetics & Personal Care |
| Amstel 100% Pure Malt 330ml | SIM235001 | 1,300 | Alcoholic Drinks |
| Baby Steps Diaper L 9-18 KG | SIM099001 | 3,500 | Baby Products |
| Gorillas Coffee 500g | SIM074004 | 12,600 | Cosmetics & Personal Care |

---

## ⚠️ TROUBLESHOOTING

### **Problem: "Cannot find json-simple library"**
**Solution:** Make sure you added the `json-simple-1.1.1.jar` to your project libraries (see STEP 1)

### **Problem: "File not found"**
**Solution:** Check the file paths in `SimbaDataImporter.java` lines 18-19. Make sure they point to the correct locations.

### **Problem: "Duplicate key error in database"**
**Solution:** Your database already has data. You need to either:
- Delete existing products: `DELETE FROM product; DELETE FROM category;`
- Or modify the script to use different IDs

### **Problem: "Permission denied writing SQL file"**
**Solution:** Make sure you have write permissions to the output folder. Try changing the output path to your Desktop.

---

## 🎉 SUCCESS!

Once completed, you'll have:
- ✅ 10 product categories
- ✅ 673 real products with names, prices, and barcodes
- ✅ Realistic stock quantities
- ✅ Ready-to-use data for testing your system

---

## 📞 NEXT STEPS

After importing:
1. Test the **Product Search** feature
2. Try creating a **Sale** with imported products
3. Check **Low Stock Alerts** (some products will be below reorder level)
4. Generate **Reports** with real data
5. Test **Barcode Scanning** with generated barcodes

---

## 💡 TIPS

- **Backup your database** before importing (just in case!)
- The import generates **random stock quantities** - you can adjust these later
- **Barcodes** are generated as SIM + product ID (e.g., SIM013001)
- **Prices** are in Rwandan Francs (RWF) - you can convert if needed

---

**Happy Importing! 🚀**
