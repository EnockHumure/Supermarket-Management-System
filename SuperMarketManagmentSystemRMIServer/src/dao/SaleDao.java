package dao;

import java.util.Collections;
import java.util.List;
import model.Sale;
import model.User;
import org.hibernate.*;

public class SaleDao {
    
    public Sale registerSale(Sale saleObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            
            System.out.println("\n=== REGISTERING SALE ===");
            System.out.println("Sale Date: " + saleObj.getSaleDate());
            System.out.println("Total Amount: " + saleObj.getTotalAmount());
            System.out.println("Cashier: " + (saleObj.getCashier() != null ? saleObj.getCashier().getFullName() : "NULL"));
            System.out.println("Payment Method: " + saleObj.getPaymentMethod());
            
            ss.save(saleObj);
            tr.commit();
            
            System.out.println("Sale saved with ID: " + saleObj.getSaleId());
            System.out.println("======================\n");
            
            ss.close();
            return saleObj;
        } catch (Exception ex) {
            System.err.println("ERROR saving sale: " + ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }
    
    public Sale updateSale(Sale saleObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            ss.update(saleObj);
            tr.commit();
            ss.close();
            return saleObj;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public Sale deleteSale(Sale saleObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            
            // Check if sale has sale items
            Query countQuery = ss.createQuery("SELECT COUNT(si) FROM SaleItem si WHERE si.sale.saleId = :saleId");
            countQuery.setParameter("saleId", saleObj.getSaleId());
            Long saleItemCount = (Long) countQuery.uniqueResult();
            
            if (saleItemCount != null && saleItemCount > 0) {
                tr.rollback();
                ss.close();
                throw new Exception("Cannot delete sale. " + saleItemCount + " sale item(s) are associated with this sale.");
            }
            
            ss.delete(saleObj);
            tr.commit();
            ss.close();
            return saleObj;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }
    
    public boolean deleteSaleById(Long saleId) {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            
            // Check if sale has sale items
            Query countQuery = ss.createQuery("SELECT COUNT(si) FROM SaleItem si WHERE si.sale.saleId = :saleId");
            countQuery.setParameter("saleId", saleId);
            Long saleItemCount = (Long) countQuery.uniqueResult();
            
            if (saleItemCount != null && saleItemCount > 0) {
                tr.rollback();
                ss.close();
                return false;
            }
            
            Sale sale = (Sale) ss.get(Sale.class, saleId);
            if (sale != null) {
                ss.delete(sale);
            }
            tr.commit();
            ss.close();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            if (ss != null && ss.isOpen()) {
                ss.close();
            }
        }
    }
    
    public Sale findSaleById(Sale saleObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Sale found = (Sale) ss.get(Sale.class, saleObj.getSaleId());
            ss.close();
            return found;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public Sale findSaleById(Long saleId) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Sale found = (Sale) ss.get(Sale.class, saleId);
            ss.close();
            return found;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public List<Sale> findAllSales() {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            // Use native SQL with JOIN to get cashier name
            List<Object[]> results = ss.createSQLQuery(
                "SELECT s.sale_id, s.cashier_id, s.sale_date, s.total_amount, s.payment_method, u.full_name " +
                "FROM sale s " +
                "LEFT JOIN users u ON s.cashier_id = u.user_id " +
                "ORDER BY s.sale_date DESC"
            ).list();
            
            List<Sale> sales = new java.util.ArrayList<>();
            for (Object[] row : results) {
                Sale sale = new Sale();
                sale.setSaleId(((Number) row[0]).longValue());
                
                User cashier = new User();
                cashier.setUserId(((Number) row[1]).longValue());
                if (row[5] != null) {
                    cashier.setFullName((String) row[5]);
                }
                sale.setCashier(cashier);
                
                // Handle date - it might be Timestamp
                if (row[2] instanceof java.sql.Timestamp) {
                    sale.setSaleDate(new java.util.Date(((java.sql.Timestamp) row[2]).getTime()));
                } else if (row[2] instanceof java.util.Date) {
                    sale.setSaleDate((java.util.Date) row[2]);
                }
                
                sale.setTotalAmount((java.math.BigDecimal) row[3]);
                sale.setPaymentMethod((String) row[4]);
                
                sales.add(sale);
            }
            
            ss.close();
            System.out.println("[SaleDao] findAllSales() returned " + sales.size() + " sales");
            return sales;
        } catch (Exception ex) {
            System.err.println("[SaleDao] Error in findAllSales: " + ex.getMessage());
            ex.printStackTrace();
        }
        return Collections.EMPTY_LIST;
    }
    
    public List<Sale> findSalesByDateRange(java.sql.Date startDate, java.sql.Date endDate) {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            
            System.out.println("\n=== SEARCHING SALES BY DATE RANGE ===");
            System.out.println("[SaleDao] Start Date: " + startDate);
            System.out.println("[SaleDao] End Date: " + endDate);
            
            // Check total sales
            Object countResult = ss.createSQLQuery("SELECT COUNT(*) FROM sale").uniqueResult();
            Long totalCount = ((Number) countResult).longValue();
            System.out.println("[SaleDao] Total sales in database: " + totalCount);
            
            // Show recent sales with their dates
            List<Object[]> recentSales = ss.createSQLQuery(
                "SELECT sale_id, sale_date, total_amount FROM sale ORDER BY sale_date DESC LIMIT 5"
            ).list();
            System.out.println("[SaleDao] Recent sales:");
            for (Object[] row : recentSales) {
                System.out.println("  ID: " + row[0] + ", Date: " + row[1] + ", Amount: " + row[2]);
            }
            
            // Use native SQL with proper timestamp comparison
            // PostgreSQL timestamp comparison without DATE() function
            String sql = "SELECT s.sale_id, s.cashier_id, s.sale_date, s.total_amount, s.payment_method, u.full_name " +
                "FROM sale s " +
                "LEFT JOIN users u ON s.cashier_id = u.user_id " +
                "WHERE s.sale_date >= :startDate AND s.sale_date < :endDate " +
                "ORDER BY s.sale_date DESC";
            
            System.out.println("[SaleDao] SQL Query: " + sql);
            System.out.println("[SaleDao] startDate parameter: " + startDate + " (type: " + startDate.getClass().getName() + ")");
            System.out.println("[SaleDao] endDate parameter: " + endDate + " (type: " + endDate.getClass().getName() + ")");
            
            List<Object[]> results = ss.createSQLQuery(sql)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .list();
            
            System.out.println("[SaleDao] Query returned " + results.size() + " results");
            
            List<Sale> sales = new java.util.ArrayList<>();
            for (Object[] row : results) {
                Sale sale = new Sale();
                sale.setSaleId(((Number) row[0]).longValue());
                
                User cashier = new User();
                cashier.setUserId(((Number) row[1]).longValue());
                if (row[5] != null) {
                    cashier.setFullName((String) row[5]);
                }
                sale.setCashier(cashier);
                
                if (row[2] instanceof java.sql.Timestamp) {
                    sale.setSaleDate(new java.util.Date(((java.sql.Timestamp) row[2]).getTime()));
                } else if (row[2] instanceof java.util.Date) {
                    sale.setSaleDate((java.util.Date) row[2]);
                }
                
                sale.setTotalAmount((java.math.BigDecimal) row[3]);
                sale.setPaymentMethod((String) row[4]);
                
                sales.add(sale);
                System.out.println("  Found: ID=" + sale.getSaleId() + ", Date=" + sale.getSaleDate() + ", Amount=" + sale.getTotalAmount());
            }
            
            System.out.println("[SaleDao] Returning " + sales.size() + " sales");
            System.out.println("======================================\n");
            
            return sales;
        } catch (Exception ex) {
            System.err.println("[SaleDao] ERROR: " + ex.getMessage());
            ex.printStackTrace();
            return Collections.EMPTY_LIST;
        } finally {
            if (ss != null && ss.isOpen()) {
                ss.close();
            }
        }
    }
    
    public List<Sale> findSalesByCashier(User cashier) {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            Query query = ss.createQuery("FROM Sale s WHERE s.cashier.userId = :cashierId ORDER BY s.saleDate DESC");
            query.setParameter("cashierId", cashier.getUserId());
            List<Sale> sales = query.list();
            return sales;
        } catch (Exception ex) {
            System.err.println("Error finding sales by cashier: " + ex.getMessage());
            ex.printStackTrace();
            return Collections.EMPTY_LIST;
        } finally {
            if (ss != null && ss.isOpen()) {
                ss.close();
            }
        }
    }
    
    // Get total revenue from all sales
    public java.math.BigDecimal getTotalRevenue() {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            Query query = ss.createQuery("SELECT SUM(s.totalAmount) FROM Sale s");
            java.math.BigDecimal total = (java.math.BigDecimal) query.uniqueResult();
            return total != null ? total : java.math.BigDecimal.ZERO;
        } catch (Exception ex) {
            System.err.println("Error getting total revenue: " + ex.getMessage());
            ex.printStackTrace();
            return java.math.BigDecimal.ZERO;
        } finally {
            if (ss != null && ss.isOpen()) {
                ss.close();
            }
        }
    }
    
    // Get total revenue by date range
    public java.math.BigDecimal getRevenueByDateRange(java.sql.Date startDate, java.sql.Date endDate) {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            // Use DATE() function to compare dates only
            Object result = ss.createSQLQuery(
                "SELECT SUM(total_amount) FROM sale WHERE DATE(sale_date) >= DATE(:startDate) AND DATE(sale_date) <= DATE(:endDate)"
            )
            .setParameter("startDate", startDate)
            .setParameter("endDate", endDate)
            .uniqueResult();
            
            java.math.BigDecimal total = result != null ? new java.math.BigDecimal(result.toString()) : java.math.BigDecimal.ZERO;
            System.out.println("[SaleDao] Revenue between " + startDate + " and " + endDate + ": " + total);
            return total;
        } catch (Exception ex) {
            System.err.println("[SaleDao] Error getting revenue by date range: " + ex.getMessage());
            ex.printStackTrace();
            return java.math.BigDecimal.ZERO;
        } finally {
            if (ss != null && ss.isOpen()) {
                ss.close();
            }
        }
    }
    
    // Get total inventory value
    public java.math.BigDecimal getTotalInventoryValue() {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            Query query = ss.createQuery("SELECT SUM(p.price * p.stockQuantity) FROM Product p");
            java.math.BigDecimal total = (java.math.BigDecimal) query.uniqueResult();
            return total != null ? total : java.math.BigDecimal.ZERO;
        } catch (Exception ex) {
            System.err.println("Error getting total inventory value: " + ex.getMessage());
            ex.printStackTrace();
            return java.math.BigDecimal.ZERO;
        } finally {
            if (ss != null && ss.isOpen()) {
                ss.close();
            }
        }
    }
    
    // Get sale items for a specific sale
    public List<model.SaleItem> findSaleItemsBySaleId(Long saleId) {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            Query query = ss.createQuery("FROM SaleItem si WHERE si.sale.saleId = :saleId");
            query.setParameter("saleId", saleId);
            List<model.SaleItem> items = query.list();
            return items;
        } catch (Exception ex) {
            System.err.println("Error finding sale items: " + ex.getMessage());
            ex.printStackTrace();
            return Collections.EMPTY_LIST;
        } finally {
            if (ss != null && ss.isOpen()) {
                ss.close();
            }
        }
    }
    
    // Dashboard metrics for ROI
    public java.util.Map<String, Object> getDashboardMetrics() {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            java.util.Map<String, Object> metrics = new java.util.HashMap<>();
            
            // Total revenue
            java.math.BigDecimal totalRevenue = getTotalRevenue();
            metrics.put("totalRevenue", totalRevenue);
            
            // Today's revenue
            java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
            java.math.BigDecimal todayRevenue = getRevenueByDateRange(today, today);
            metrics.put("todayRevenue", todayRevenue);
            
            // Yesterday's revenue
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.add(java.util.Calendar.DATE, -1);
            java.sql.Date yesterday = new java.sql.Date(cal.getTimeInMillis());
            java.math.BigDecimal yesterdayRevenue = getRevenueByDateRange(yesterday, yesterday);
            metrics.put("yesterdayRevenue", yesterdayRevenue);
            
            // This month's revenue
            cal = java.util.Calendar.getInstance();
            cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
            java.sql.Date monthStart = new java.sql.Date(cal.getTimeInMillis());
            java.math.BigDecimal monthRevenue = getRevenueByDateRange(monthStart, today);
            metrics.put("monthRevenue", monthRevenue);
            
            // Total sales count
            Long totalSales = (Long) ss.createSQLQuery("SELECT COUNT(*) FROM sale").uniqueResult();
            metrics.put("totalSales", totalSales != null ? totalSales : 0L);
            
            // Today's sales count
            Long todaySales = (Long) ss.createSQLQuery(
                "SELECT COUNT(*) FROM sale WHERE DATE(sale_date) = DATE(:today)"
            ).setParameter("today", today).uniqueResult();
            metrics.put("todaySales", todaySales != null ? todaySales : 0L);
            
            return metrics;
        } catch (Exception ex) {
            System.err.println("Error getting dashboard metrics: " + ex.getMessage());
            ex.printStackTrace();
            return new java.util.HashMap<>();
        } finally {
            if (ss != null && ss.isOpen()) {
                ss.close();
            }
        }
    }
    
    // Best selling products
    public java.util.List<java.util.Map<String, Object>> getBestSellingProducts(int limit) {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            List<Object[]> results = ss.createSQLQuery(
                "SELECT p.name, SUM(si.quantity) as total_qty, SUM(si.sub_total) as total_revenue " +
                "FROM sale_item si " +
                "JOIN product p ON si.product_id = p.product_id " +
                "GROUP BY p.name " +
                "ORDER BY total_qty DESC " +
                "LIMIT :limit"
            ).setParameter("limit", limit).list();
            
            java.util.List<java.util.Map<String, Object>> products = new java.util.ArrayList<>();
            for (Object[] row : results) {
                java.util.Map<String, Object> product = new java.util.HashMap<>();
                product.put("name", row[0]);
                product.put("quantity", ((Number) row[1]).intValue());
                product.put("revenue", (java.math.BigDecimal) row[2]);
                products.add(product);
            }
            return products;
        } catch (Exception ex) {
            System.err.println("Error getting best selling products: " + ex.getMessage());
            ex.printStackTrace();
            return new java.util.ArrayList<>();
        } finally {
            if (ss != null && ss.isOpen()) {
                ss.close();
            }
        }
    }
    
    // Cashier performance
    public java.util.Map<String, java.math.BigDecimal> getCashierPerformance() {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            List<Object[]> results = ss.createSQLQuery(
                "SELECT u.full_name, SUM(s.total_amount) as total_sales " +
                "FROM sale s " +
                "JOIN users u ON s.cashier_id = u.user_id " +
                "GROUP BY u.full_name " +
                "ORDER BY total_sales DESC"
            ).list();
            
            java.util.Map<String, java.math.BigDecimal> performance = new java.util.LinkedHashMap<>();
            for (Object[] row : results) {
                performance.put((String) row[0], (java.math.BigDecimal) row[1]);
            }
            return performance;
        } catch (Exception ex) {
            System.err.println("Error getting cashier performance: " + ex.getMessage());
            ex.printStackTrace();
            return new java.util.HashMap<>();
        } finally {
            if (ss != null && ss.isOpen()) {
                ss.close();
            }
        }
    }
}
