package service.implementation;

import dao.ProductDao;
import dao.SaleDao;
import dao.SaleItemDao;
import dao.HibernateUtil;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import model.Product;
import model.Sale;
import model.SaleItem;
import model.User;
import service.SaleService;
import notification.LowStockNotificationPublisher;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class SaleServiceImpl extends UnicastRemoteObject implements SaleService {

    SaleDao saleDao = new SaleDao();
    SaleItemDao saleItemDao = new SaleItemDao();
    ProductDao productDao = new ProductDao();
    LowStockNotificationPublisher publisher = null;
    
    public SaleServiceImpl() throws RemoteException {
        // Try to initialize notification publisher (optional)
        try {
            publisher = new LowStockNotificationPublisher();
        } catch (Exception ex) {
            // Silently disable publisher if ActiveMQ is not available
        }
    }
    
    @Override
    public Sale registerSaleRecord(Sale theSale) throws RemoteException {
        return saleDao.registerSale(theSale);
    }

    @Override
    public Sale updateSaleRecord(Sale theSale) throws RemoteException {
        return saleDao.updateSale(theSale);
    }

    @Override
    public Sale deleteSaleRecord(Sale theSale) throws RemoteException {
        return saleDao.deleteSale(theSale);
    }

    @Override
    public Sale findSaleRecordById(Sale theSale) throws RemoteException {
        return saleDao.findSaleById(theSale);
    }

    @Override
    public List<Sale> findAllSaleRecords() throws RemoteException {
        return saleDao.findAllSales();
    }
    
    @Override
    public Sale processSale(Sale sale, List<SaleItem> saleItems) throws RemoteException {
        Session ss = null;
        Transaction tr = null;
        try {
            if (saleItems == null || saleItems.isEmpty()) {
                throw new RemoteException("Sale cannot be empty");
            }
            
            ss = HibernateUtil.getSessionFactory().openSession();
            tr = ss.beginTransaction();
            
            for (SaleItem item : saleItems) {
                Product dbProduct = productDao.findProductById(item.getProduct().getProductId());
                
                if (dbProduct == null) {
                    tr.rollback();
                    throw new RemoteException("Product not found: " + item.getProduct().getName());
                }
                
                if (dbProduct.getStockQuantity() < item.getQuantity()) {
                    tr.rollback();
                    throw new RemoteException("Insufficient stock for: " + item.getProduct().getName() + 
                            ". Available: " + dbProduct.getStockQuantity() + ", Requested: " + item.getQuantity());
                }
            }
            
            sale.setTotalAmount(calculateTotal(saleItems));
            
            // Save the sale first to get the sale ID
            ss.save(sale);
            ss.flush(); // Ensure sale is persisted to get the ID
            
            for (SaleItem item : saleItems) {
                Product dbProduct = productDao.findProductById(item.getProduct().getProductId());
                
                item.setSale(sale);
                item.setUnitPrice(dbProduct.getPrice());
                item.setSubTotal(dbProduct.getPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity())));
                
                ss.save(item);
                
                int newStock = dbProduct.getStockQuantity() - item.getQuantity();
                dbProduct.setStockQuantity(newStock);
                ss.update(dbProduct);
                
                if (newStock <= dbProduct.getReorderLevel()) {
                    System.out.println("LOW STOCK ALERT: " + dbProduct.getName() + 
                            " - Current stock: " + newStock + 
                            ", Reorder level: " + dbProduct.getReorderLevel());
                    
                    // Publish low stock alert via ActiveMQ (if available)
                    if (publisher != null) {
                        publisher.publishLowStockAlert(
                            dbProduct.getProductId(),
                            dbProduct.getName(),
                            dbProduct.getBarcode(),
                            newStock,
                            dbProduct.getReorderLevel()
                        );
                    }
                }
            }
            
            tr.commit();
            ss.close();
            return sale;
            
        } catch (RemoteException ex) {
            if (tr != null) {
                try { tr.rollback(); } catch (Exception e) {}
            }
            if (ss != null && ss.isOpen()) {
                ss.close();
            }
            throw ex;
        } catch (Exception ex) {
            if (tr != null) {
                try { tr.rollback(); } catch (Exception e) {}
            }
            if (ss != null && ss.isOpen()) {
                ss.close();
            }
            throw new RemoteException("Error processing sale: " + ex.getMessage());
        }
    }
    
    private java.math.BigDecimal calculateTotal(List<SaleItem> saleItems) {
        java.math.BigDecimal total = java.math.BigDecimal.ZERO;
        for (SaleItem item : saleItems) {
            if (item.getSubTotal() != null) {
                total = total.add(item.getSubTotal());
            }
        }
        return total;
    }
    
    @Override
    public List<Sale> findSalesByDateRange(java.sql.Date startDate, java.sql.Date endDate) throws RemoteException {
        try {
            return saleDao.findSalesByDateRange(startDate, endDate);
        } catch (Exception ex) {
            throw new RemoteException("Error finding sales: " + ex.getMessage());
        }
    }
    
    @Override
    public List<Sale> findSalesByCashier(User cashier) throws RemoteException {
        try {
            return saleDao.findSalesByCashier(cashier);
        } catch (Exception ex) {
            throw new RemoteException("Error finding sales: " + ex.getMessage());
        }
    }
}
