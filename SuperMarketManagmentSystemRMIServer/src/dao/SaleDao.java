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
            ss.save(saleObj);
            tr.commit();
            ss.close();
            return saleObj;
        } catch (Exception ex) {
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
            // Only fetch sale data without eager joins to prevent N+1 queries
            List<Sale> sales = ss.createQuery("SELECT s FROM Sale s").list();
            ss.close();
            return sales;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Collections.EMPTY_LIST;
    }
    
    public List<Sale> findSalesByDateRange(java.sql.Date startDate, java.sql.Date endDate) {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            Query query = ss.createQuery("FROM Sale s WHERE DATE(s.saleDate) BETWEEN :startDate AND :endDate ORDER BY s.saleDate DESC");
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            List<Sale> sales = query.list();
            return sales;
        } catch (Exception ex) {
            System.err.println("Error finding sales by date range: " + ex.getMessage());
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
}
