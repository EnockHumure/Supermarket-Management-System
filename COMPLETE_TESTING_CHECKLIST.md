# COMPLETE TESTING CHECKLIST

## ✅ Database Status
- [x] Columns exist (discount_percent, discount_amount, vat_amount, subtotal)
- [x] 789 products in database
- [x] Categories exist
- [x] Sales data exists (14 sales)

## 🔧 What You Need To Do NOW:

### Step 1: Rebuild Server (CRITICAL!)
1. **Close the running server** (if it's running)
2. Open NetBeans
3. Right-click **SuperMarketManagmentSystemRMIServer** project
4. Click **Clean and Build**
5. Wait for "BUILD SUCCESSFUL"
6. Right-click project → **Run**
7. Wait for "Server started on port 5000"

### Step 2: Rebuild Client (CRITICAL!)
1. Right-click **SuperMarketManagmentSysRMIClient27394** project
2. Click **Clean and Build**
3. Wait for "BUILD SUCCESSFUL"
4. Right-click project → **Run**

### Step 3: Test Everything

#### A. Test Dashboard
1. Login as **admin/admin**
2. **Check LEFT side** - Should see 6 ROI cards:
   - Total Revenue
   - Today's Revenue
   - This Month
   - Total Transactions
   - Today's Transactions
   - Today vs Yesterday
3. **Check RIGHT side** - Should see 3 tabs:
   - Stock Status (bar chart)
   - Category Distribution (pie chart)
   - Sales Trend (line chart with transaction counts)
4. Click **"Refresh Dashboard"** button

#### B. Test Process Sale
1. Click **"Process Sale"** button
2. **Check if window opens** ✓
3. **Check if you see:**
   - Products table (should show 789 products)
   - Cart table (empty)
   - **Sale Details panel on right** with:
     - Sale Date field
     - Discount % field
     - Subtotal label
     - Discount Amount label
     - After Discount label
     - VAT (18%) label
     - GRAND TOTAL label
   - Payment Method dropdown
   - PROCESS SALE button

4. **Add a product to cart:**
   - Select any product from table
   - Click "ADD TO CART"
   - Enter quantity (e.g., 2)
   - Click OK
   - **Check if product appears in cart**
   - **Check if totals update automatically**

5. **Test discount (ADMIN only):**
   - Type "10" in Discount % field
   - **Check if discount amount calculates**
   - **Check if VAT recalculates**
   - **Check if grand total updates**

6. **Process the sale:**
   - Click "PROCESS SALE" button
   - Enter customer name (optional)
   - Click OK
   - **Check if success message appears**
   - **Check if receipt opens**

#### C. Test Reports
1. Click **"Reports"** button
2. Click **"All Sales"** button
3. **Check if sales appear in table**
4. **Check "Products (Name×Qty)" column** - should show like "Milk×2, Bread×1"
5. **Check if date/time shows** in Date column
6. Click **"Export CSV"** button
7. **Check if file saves** to reports folder

#### D. Test Charts
1. Go back to Dashboard
2. Click **"Stock Status"** tab
   - Should see bars for In Stock, Low Stock, Out of Stock
3. Click **"Category Distribution"** tab
   - Should see pie chart with categories
4. Click **"Sales Trend"** tab
   - Should see line chart with transaction counts for last 7 days

---

## 🚨 If Something Doesn't Work:

### Problem: Process Sale button doesn't open window
**Solution:** Server not running or client not rebuilt
- Restart server
- Rebuild client

### Problem: No products showing
**Solution:** Database connection issue
- Check if server is running
- Check PostgreSQL is running
- Check connection in hibernate.cfg.xml

### Problem: Charts show "No Data"
**Solution:** Need to process some sales first
- Process 2-3 sales
- Refresh dashboard

### Problem: Reports show nothing
**Solution:** No sales in database
- Process some sales first
- Then check reports

### Problem: Discount field not working
**Solution:** Only ADMIN can add discount
- Login as admin/admin
- Manager and Cashier cannot add discount

### Problem: ROI cards show 0.00
**Solution:** No sales processed yet
- Process some sales
- Click "Refresh Dashboard"

---

## 📊 Expected Results After Testing:

✅ **Dashboard:**
- 6 ROI cards with real numbers
- 3 charts with data
- Refresh button works

✅ **Process Sale:**
- Window opens
- Products load (789 items)
- Can add to cart
- Discount calculates (ADMIN only)
- VAT calculates automatically (18%)
- Sale processes successfully
- Receipt generates

✅ **Reports:**
- Shows sales with products in "Name×Qty" format
- Shows date and time
- CSV export works

✅ **Charts:**
- Stock Status shows bars
- Category Distribution shows pie
- Sales Trend shows line with transaction counts

---

## 🎯 Quick Test Script:

1. **Rebuild server** → Run
2. **Rebuild client** → Run
3. **Login** as admin/admin
4. **Check dashboard** - see 6 cards + 3 charts
5. **Click Process Sale**
6. **Add product** (select, click ADD TO CART, enter qty)
7. **Type 10** in Discount %
8. **Check totals** update
9. **Click PROCESS SALE**
10. **Check receipt** opens
11. **Go to Reports** → All Sales
12. **Check products** show as "Name×Qty"
13. **Click Export CSV**
14. **Go to Dashboard** → Click Refresh
15. **Check ROI cards** update

---

## ⏱️ Time Needed: 5-10 minutes

---

**If EVERYTHING works:** ✅ System is ready for viva!

**If SOMETHING fails:** Send me the error message and I'll fix it immediately!

---

*Remember: The most common issue is forgetting to rebuild after code changes!*
