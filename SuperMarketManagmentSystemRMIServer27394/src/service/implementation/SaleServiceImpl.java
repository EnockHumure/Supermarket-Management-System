package service.implementation;

import dao.ProductDao;
import dao.SaleDao;
import dao.SaleItemDao;
import dao.UserDao;
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
import util.EmailNotificationUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class SaleServiceImpl extends UnicastRemoteObject implements SaleService {

    SaleDao saleDao = new SaleDao();
    SaleItemDao saleItemDao = new SaleItemDao();
    ProductDao productDao = new ProductDao();
    UserDao userDao = new UserDao();
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
            
            // If totalAmount not set, calculate it
            if (sale.getTotalAmount() == null || sale.getTotalAmount().compareTo(java.math.BigDecimal.ZERO) == 0) {
                sale.setTotalAmount(calculateTotal(saleItems));
            }
            
            // Ensure discount and VAT fields are set
            if (sale.getDiscountPercent() == null) sale.setDiscountPercent(java.math.BigDecimal.ZERO);
            if (sale.getDiscountAmount() == null) sale.setDiscountAmount(java.math.BigDecimal.ZERO);
            if (sale.getVatAmount() == null) sale.setVatAmount(java.math.BigDecimal.ZERO);
            if (sale.getSubtotal() == null) sale.setSubtotal(java.math.BigDecimal.ZERO);
            
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
                    
                    // Send email notification to all users with emails
                    sendLowStockEmailNotifications(dbProduct, newStock);
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
    
    @Override
    public java.math.BigDecimal getTotalRevenue() throws RemoteException {
        try {
            return saleDao.getTotalRevenue();
        } catch (Exception ex) {
            throw new RemoteException("Error getting total revenue: " + ex.getMessage());
        }
    }
    
    @Override
    public java.math.BigDecimal getRevenueByDateRange(java.sql.Date startDate, java.sql.Date endDate) throws RemoteException {
        try {
            return saleDao.getRevenueByDateRange(startDate, endDate);
        } catch (Exception ex) {
            throw new RemoteException("Error getting revenue by date range: " + ex.getMessage());
        }
    }
    
    @Override
    public java.math.BigDecimal getTotalInventoryValue() throws RemoteException {
        try {
            return saleDao.getTotalInventoryValue();
        } catch (Exception ex) {
            throw new RemoteException("Error getting total inventory value: " + ex.getMessage());
        }
    }
    
    @Override
    public List<SaleItem> findSaleItemsBySaleId(Long saleId) throws RemoteException {
        try {
            return saleDao.findSaleItemsBySaleId(saleId);
        } catch (Exception ex) {
            throw new RemoteException("Error finding sale items: " + ex.getMessage());
        }
    }
    
    @Override
    public java.util.Map<String, Object> getDashboardMetrics() throws RemoteException {
        try {
            return saleDao.getDashboardMetrics();
        } catch (Exception ex) {
            throw new RemoteException("Error getting dashboard metrics: " + ex.getMessage());
        }
    }
    
    @Override
    public java.util.List<java.util.Map<String, Object>> getBestSellingProducts(int limit) throws RemoteException {
        try {
            return saleDao.getBestSellingProducts(limit);
        } catch (Exception ex) {
            throw new RemoteException("Error getting best selling products: " + ex.getMessage());
        }
    }
    
    @Override
    public java.util.Map<String, java.math.BigDecimal> getCashierPerformance() throws RemoteException {
        try {
            return saleDao.getCashierPerformance();
        } catch (Exception ex) {
            throw new RemoteException("Error getting cashier performance: " + ex.getMessage());
        }
    }
    
    /**
     * Send low stock email notifications to all users with email addresses
     */
    private void sendLowStockEmailNotifications(Product product, Integer currentStock) {
        try {
            List<User> allUsers = userDao.findAllUsers();
            int sentCount = 0;
            
            for (User user : allUsers) {
                if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                    try {
                        EmailNotificationUtil.sendLowStockNotification(
                            user.getEmail(),
                            product.getName(),
                            product.getBarcode(),
                            currentStock,
                            product.getReorderLevel()
                        );
                        sentCount++;
                        System.out.println("✓ Low stock email sent to: " + user.getEmail());
                    } catch (Exception ex) {
                        System.err.println("✗ Failed to send email to: " + user.getEmail() + 
                                " - " + ex.getMessage());
                    }
                }
            }
            
            System.out.println("\n=== LOW STOCK EMAIL NOTIFICATIONS ===");
            System.out.println("Product: " + product.getName());
            System.out.println("Current Stock: " + currentStock);
            System.out.println("Reorder Level: " + product.getReorderLevel());
            System.out.println("Emails Sent: " + sentCount);
            System.out.println("======================================\n");
            
        } catch (Exception ex) {
            System.err.println("Error sending low stock emails: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
