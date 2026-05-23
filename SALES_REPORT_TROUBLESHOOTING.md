# Sales Report Troubleshooting Guide

## Issue
Sales are being processed successfully (receipts generated), but reports show "No sales found".

## What We've Done
1. ✅ Added extensive debugging to SaleDao.java
2. ✅ Fixed PostgreSQL date casting (using CAST instead of ::)
3. ✅ Added "Show All" button to see all sales in database
4. ✅ Added "Today" quick button for today's sales

## Steps to Test

### Step 1: Restart Server
**IMPORTANT**: You MUST restart the server to load the updated SaleDao.java with new debugging.

```
1. Stop the server (Ctrl+C or close window)
2. Restart: Run SuperMarketManagmentSystemRMIServer
3. Wait for "Server is running..." message
```

### Step 2: Process a Test Sale
1. Login to client
2. Go to "Process Sale"
3. Add any product to cart
4. Process the sale
5. **Watch the SERVER console** - you should see:
   ```
   === REGISTERING SALE ===
   Sale Date: [date]
   Total Amount: [amount]
   Cashier: [name]
   Payment Method: [method]
   Sale saved with ID: [number]
   ======================
   ```

### Step 3: Check Reports
1. Go to Reports module
2. Click "Show All" button first
   - This shows ALL sales in database (no date filter)
   - If this shows sales, the problem is the date query
   - If this shows NO sales, sales aren't being saved

3. Then click "Today" button
   - This sets dates to today and searches
   - **Watch the SERVER console** for debugging output:
   ```
   === DATABASE CHECK ===
   [SaleDao] Total sales in database: X
   [SaleDao] Recent sales:
     Sale ID: X, DateTime: [timestamp]
   [SaleDao] Searching for dates between: [date] and [date]
   ==================
   
   [SaleDao] Query returned X sales
   ```

## What the Server Console Will Tell You

### If Sale is Saved Successfully:
```
=== REGISTERING SALE ===
Sale Date: Fri May 16 17:07:16 CAT 2026
Total Amount: 625000
Cashier: Humure Enock
Payment Method: Cash
Sale saved with ID: 16
======================
```

### If Query Finds Sales:
```
=== DATABASE CHECK ===
[SaleDao] Total sales in database: 16
[SaleDao] Recent sales:
  Sale ID: 16, DateTime: 2026-05-16 17:07:16.123
[SaleDao] Searching for dates between: 2026-05-16 and 2026-05-16
==================

[SaleDao] Query returned 1 sales
[SaleDao] First sale: ID=16, Date=Fri May 16 17:07:16 CAT 2026, Amount=625000
```

### If Query Finds NO Sales (Problem):
```
=== DATABASE CHECK ===
[SaleDao] Total sales in database: 16
[SaleDao] Recent sales:
  Sale ID: 16, DateTime: 2026-05-16 17:07:16.123
[SaleDao] Searching for dates between: 2026-05-16 and 2026-05-16
==================

[SaleDao] Query returned 0 sales
[SaleDao] WARNING: No sales found for date range!
```

## Common Issues

### Issue 1: Server Not Restarted
**Symptom**: No debugging output in server console
**Solution**: Restart the server to load updated code

### Issue 2: Date Format Mismatch
**Symptom**: "Show All" works, but date queries don't
**Solution**: Already fixed with CAST(sale_date AS DATE)

### Issue 3: Timezone Issues
**Symptom**: Sale saved with one date, query searches different date
**Solution**: Check server console to see actual timestamps

### Issue 4: Dashboard Not Refreshing
**Symptom**: Dashboard shows old data
**Solution**: Dashboard loads data once at startup. Close and reopen dashboard to refresh.

## Quick Test Commands

### Check Database Directly (if you have psql):
```sql
-- Connect to database
psql -U postgres -d supermarket_management_system_db

-- Check all sales
SELECT sale_id, sale_date, total_amount FROM sale ORDER BY sale_date DESC LIMIT 10;

-- Check today's sales
SELECT sale_id, sale_date, total_amount 
FROM sale 
WHERE CAST(sale_date AS DATE) = CURRENT_DATE;

-- Check specific date
SELECT sale_id, sale_date, total_amount 
FROM sale 
WHERE CAST(sale_date AS DATE) = '2026-05-16';
```

## Expected Behavior After Fix

1. **Process Sale**: 
   - Server console shows "=== REGISTERING SALE ===" with details
   - Receipt generated successfully
   - Sale ID assigned

2. **Show All Button**:
   - Shows ALL sales in database
   - No date filtering
   - Should always work if sales exist

3. **Today Button**:
   - Sets dates to today
   - Searches for today's sales
   - Server console shows database check and query results

4. **Date Range Search**:
   - Enter start/end dates in YYYY-MM-DD format
   - Click "Generate Daily Sales"
   - Server console shows debugging output

## Next Steps

1. **Restart server** (CRITICAL!)
2. Process a new sale
3. Watch server console for debugging
4. Try "Show All" button first
5. Then try "Today" button
6. Share server console output if still not working

## Dashboard Refresh Issue

The dashboard loads data once when you login. It does NOT automatically refresh when new sales are made.

**To see updated data in dashboard**:
1. Logout
2. Login again
OR
3. Close and reopen the dashboard window

**Future Enhancement**: Add a "Refresh" button to dashboard to reload charts without restarting.
