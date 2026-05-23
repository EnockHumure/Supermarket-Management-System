# 🚀 QUICK START - SIMBA DATA IMPORT

## ⚡ 5-MINUTE SETUP

### 1️⃣ Download JSON Library
Download: `json-simple-1.1.1.jar`
- Link: https://repo1.maven.org/maven2/com/googlecode/json-simple/json-simple/1.1.1/json-simple-1.1.1.jar

### 2️⃣ Add to Project
- Right-click `SuperMarketManagmentSystemRMIServer` → Properties
- Libraries → Add JAR/Folder → Select `json-simple-1.1.1.jar`

### 3️⃣ Run Importer
- Open: `src/util/SimbaDataImporter.java`
- Right-click → Run File (Shift+F6)

### 4️⃣ Import to Database
- Open pgAdmin
- Open Query Tool on `supermarket_db`
- Open file: `import_simba_products.sql`
- Execute (F5)

### 5️⃣ Done! 🎉
- Start your application
- See 673 products ready to use!

---

## 📊 WHAT YOU GET

✅ **10 Categories**
- Cosmetics & Personal Care
- Sports & Wellness
- Baby Products
- Kitchenware & Electronics
- Food Products
- Alcoholic Drinks
- Cleaning & Sanitary
- General
- Pet Care
- Kitchen Storage

✅ **673 Products**
- Real product names
- Realistic prices (RWF)
- Generated barcodes (SIM######)
- Random stock quantities (10-100)
- Auto-calculated reorder levels

---

## 🔍 VERIFY IMPORT

Run in pgAdmin:
```sql
-- Check categories
SELECT COUNT(*) FROM category;  -- Should be 10

-- Check products
SELECT COUNT(*) FROM product;   -- Should be 673

-- View sample
SELECT p.name, p.barcode, p.price, c.name as category
FROM product p
JOIN category c ON p.category_id = c.category_id
LIMIT 5;
```

---

## ⚠️ BEFORE YOU START

### Backup Your Database (Optional but Recommended)
```sql
-- In pgAdmin, right-click supermarket_db → Backup
```

### Clear Existing Data (If Needed)
```sql
-- Only if you want to start fresh
DELETE FROM sale_item;
DELETE FROM sale;
DELETE FROM product;
DELETE FROM category;
```

---

## 🎯 FILE LOCATIONS

| File | Location |
|------|----------|
| Importer Code | `SuperMarketManagmentSystemRMIServer/src/util/SimbaDataImporter.java` |
| JSON Source | `C:\Users\humur\Desktop\A2SVOVERCOUNTRY\simba\simba_products.json` |
| SQL Output | `SuperMarketManagementSystem/import_simba_products.sql` |
| Full Guide | `SIMBA_IMPORT_GUIDE.md` |
| Data Mapping | `DATA_MAPPING_EXPLAINED.md` |

---

## 💡 TIPS

- **First time?** Read `SIMBA_IMPORT_GUIDE.md` for detailed steps
- **Want to understand?** Read `DATA_MAPPING_EXPLAINED.md`
- **Having issues?** Check Troubleshooting section in guide
- **Need help?** Check the error message and guide

---

## 🎨 SAMPLE PRODUCTS YOU'LL GET

```
Lentz Radiant Heater 80036          RWF 83,600
Amstel 100% Pure Malt 330ml         RWF 1,300
Baby Steps Diaper L 9-18 KG         RWF 3,500
Gorillas Coffee 500g                RWF 12,600
Blue Band Original 1kg              RWF 6,700
Everyday Toilet Paper 4P            RWF 9,800
Jack Daniels 1L                     RWF 80,000
Farmer's Choice Beef Smokies 400g   RWF 6,200
```

---

## ✅ CHECKLIST

- [ ] Downloaded json-simple-1.1.1.jar
- [ ] Added JAR to project libraries
- [ ] Ran SimbaDataImporter.java
- [ ] SQL file generated successfully
- [ ] Opened pgAdmin
- [ ] Connected to supermarket_db
- [ ] Executed import_simba_products.sql
- [ ] Verified data with SELECT queries
- [ ] Started application
- [ ] Tested product search
- [ ] Celebrated success! 🎉

---

## 🆘 QUICK TROUBLESHOOTING

| Problem | Solution |
|---------|----------|
| "Cannot find json-simple" | Add JAR to project libraries |
| "File not found" | Check file paths in SimbaDataImporter.java |
| "Duplicate key error" | Clear existing data first |
| "Permission denied" | Run as administrator or change output path |

---

## 📞 NEED MORE HELP?

1. Read the full guide: `SIMBA_IMPORT_GUIDE.md`
2. Understand the mapping: `DATA_MAPPING_EXPLAINED.md`
3. Check your database connection
4. Verify file paths are correct

---

**Ready? Let's import! 🚀**

Time needed: **5-10 minutes**
Difficulty: **Beginner-friendly**
Result: **673 products ready to use!**
