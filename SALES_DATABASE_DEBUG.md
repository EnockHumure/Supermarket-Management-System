# Sales Database Debugging Guide

## Problem
Sales are being processed and receipts are generated, but they don't appear in the daily sales report.

## Diagnostic Tools Added

### 1. **"Show All" Button** (NEW!)
- **Dark gray button** next to "Today" button
- Shows ALL sales in database (no date filter)
- Use this to check if sales are being saved at all

### 2. **Enhanced Server Logging**
Server now prints detailed diagnostics:
```
=== DATABASE CHECK ===
[SaleDao] Total sales in database: 5
[SaleDao] Recent sales:
  Sale ID: 123, DateTime: 2026-05-16 14:30:00, Date Only: 2026-05-16
  Sale ID: 124, DateTime: 2026-05-16 15:45:00, Date Only: 2026-05-16
[SaleDao] Searching for dates between: 2026-05-16 and 2026-05-16
==================
```

### 3. **Client-Side Logging**
Client prints every sale found:
```
[ReportsModule] Searching for sales between 2026-05-16 and 2026-05-16
[ReportsModule] Found 2 sales
[ReportsModule] Sale #123, Date: 2026-05-16 14:30:00, Amount: 5000
```

## Testing Steps

### Step 1: Check if Sales Exist in Database

1. **Restart Server** (IMPORTANT!)
2. **Restart Client**
3. Go to Reports Module
4. Click **"Show All"** button (dark gray)
5. Check result:

**If you see sales:**
- ✅ Sales ARE being saved to database
- Problem is with date filtering
- Go to Step 2

**If you see "No sales in database":**
- ❌ Sales are NOT being saved
- Problem is in sale processing
- Go to Step 3

### Step 2: Test Date Filtering

If "Show All" shows sales but "Today" doesn't:

1. Look at the dates shown in "Show All" results
2. Note the exact date format
3. Click "Today" button
4. Check server console for:
   ```
   [SaleDao] Recent sales:
     Sale ID: X, DateTime: ..., Date Only: ...
   [SaleDao] Searching for dates between: ...
   ```
5. Compare the "Date Only" with "Searching for dates"
6. If they don't match, there's a date format issue

### Step 3: Check Sale Processing

If "Show All" shows no sales:

1. Go to Process Sale module
2. Add a product to cart
3. Click "Process Sale"
4. Enter customer name (or skip)
5. Check for success message
6. Check server console for:
   ```
   [SaleServiceImpl] Processing sale...
   [SaleDao] Saving sale...
   ```
7. If you see errors, sales aren't being saved

### Step 4: Check Database Directly

Open pgAdmin or psql and run:

```sql
-- Check total sales
SELECT COUNT(*) FROM sale;

-- Check recent sales with dates
SELECT 
    sale_id,
    sale_date,
    sale_date::date as date_only,
    total_amount,
    payment_method
FROM sale
ORDER BY sale_date DESC
LIMIT 10;

-- Check today's sales specifically
SELECT 
    sale_id,
    sale_date,
    total_amount
FROM sale
WHERE sale_date::date = CURRENT_DATE;

-- Check if date casting works
SELECT 
    sale_id,
    sale_date,
    sale_date::date,
    CURRENT_DATE,
    (sale_date::date = CURRENT_DATE) as is_today
FROM sale
ORDER BY sale_date DESC
LIMIT 5;
```

## Common Issues and Solutions

### Issue 1: Sales Not Saved to Database

**Symptoms:**
- "Show All" shows 0 sales
- Receipts are generated
- No errors during sale processing

**Possible Causes:**
- Transaction not committed
- Database connection issue
- Hibernate session problem

**Solution:**
Check server console during sale processing. Should see:
```
[SaleServiceImpl] Processing sale...
[SaleDao] Saving sale to database...
Sale saved with ID: 123
```

If you don't see this, the sale isn't being saved.

### Issue 2: Date Format Mismatch

**Symptoms:**
- "Show All" shows sales
- "Today" shows 0 sales
- Dates look correct

**Possible Causes:**
- Timezone issue
- Date vs DateTime comparison
- PostgreSQL date casting not working

**Solution:**
Check server console output:
```
[SaleDao] Recent sales:
  Sale ID: 123, DateTime: 2026-05-16 14:30:00, Date Only: 2026-05-16
[SaleDao] Searching for dates between: 2026-05-16 and 2026-05-16
```

The "Date Only" should match "Searching for dates".

### Issue 3: Server Not Restarted

**Symptoms:**
- Changes not taking effect
- Old behavior persists

**Solution:**
1. Stop server completely
2. Wait 5 seconds
3. Start server again
4. Check console for startup messages

## What to Check in Server Console

When you click "Today" or "Show All", you should see:

```
=== DATABASE CHECK ===
[SaleDao] Total sales in database: X
[SaleDao] Recent sales:
  Sale ID: ..., DateTime: ..., Date Only: ...
[SaleDao] Searching for dates between: ... and ...
==================

[SaleDao] Query returned X sales
[SaleDao] First sale: ID=..., Date=..., Amount=...
```

If you see:
- `Total sales in database: 0` → Sales not being saved
- `Query returned 0 sales` but total > 0 → Date filtering issue
- No output at all → Server not running or not restarted

## Quick Diagnostic Checklist

- [ ] Server is running
- [ ] Server was restarted after code changes
- [ ] Client was restarted after code changes
- [ ] Can process a sale successfully
- [ ] Receipt is generated
- [ ] "Show All" button shows sales
- [ ] "Today" button shows today's sales
- [ ] Server console shows diagnostic output
- [ ] No errors in server console
- [ ] Database is accessible

## Next Steps

1. **First:** Click "Show All" to see if ANY sales exist
2. **If yes:** Problem is date filtering - check server console
3. **If no:** Problem is sale saving - check sale processing
4. **Always:** Check server console for detailed diagnostics

---

**The "Show All" button is your best friend for debugging!**

It will immediately tell you if sales are in the database or not.
