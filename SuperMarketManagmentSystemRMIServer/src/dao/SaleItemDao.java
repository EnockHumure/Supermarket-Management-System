package dao;

import java.util.Collections;
import java.util.List;
import model.Sale;
import model.SaleItem;
import org.hibernate.*;

public class SaleItemDao {
    
    public SaleItem registerSaleItem(SaleItem saleItemObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            ss.save(saleItemObj);
            tr.commit();
            ss.close();
            return saleItemObj;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public SaleItem updateSaleItem(SaleItem saleItemObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            ss.update(saleItemObj);
            tr.commit();
            ss.close();
            return saleItemObj;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public SaleItem deleteSaleItem(SaleItem saleItemObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            ss.delete(saleItemObj);
            tr.commit();
            ss.close();
            return saleItemObj;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public SaleItem findSaleItemById(SaleItem saleItemObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            SaleItem found = (SaleItem) ss.get(SaleItem.class, saleItemObj.getSaleItemId());
            ss.close();
            return found;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public List<SaleItem> findAllSaleItems() {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            List<SaleItem> saleItems = ss.createQuery("SELECT si FROM SaleItem si").list();
            ss.close();
            return saleItems;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Collections.EMPTY_LIST;
    }
    
    public List<SaleItem> findSaleItemsBySale(Sale sale) {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            Query query = ss.createQuery("FROM SaleItem si WHERE si.sale.saleId = :saleId");
            query.setParameter("saleId", sale.getSaleId());
            List<SaleItem> saleItems = query.list();
            return saleItems;
        } catch (Exception ex) {
            System.err.println("Error finding sale items by sale: " + ex.getMessage());
            ex.printStackTrace();
            return Collections.EMPTY_LIST;
        } finally {
            if (ss != null && ss.isOpen()) {
                ss.close();
            }
        }
    }
}
