package service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import model.Sale;
import model.SaleItem;
import model.User;

public interface SaleService extends Remote {
    Sale registerSaleRecord(Sale theSale) throws RemoteException;
    Sale updateSaleRecord(Sale theSale) throws RemoteException;
    Sale deleteSaleRecord(Sale theSale) throws RemoteException;
    Sale findSaleRecordById(Sale theSale) throws RemoteException;
    List<Sale> findAllSaleRecords() throws RemoteException;
    
    // Sales processing methods
    Sale processSale(Sale sale, List<SaleItem> saleItems) throws RemoteException;
    List<Sale> findSalesByDateRange(java.sql.Date startDate, java.sql.Date endDate) throws RemoteException;
    List<Sale> findSalesByCashier(User cashier) throws RemoteException;
    
    // Revenue tracking methods
    java.math.BigDecimal getTotalRevenue() throws RemoteException;
    java.math.BigDecimal getRevenueByDateRange(java.sql.Date startDate, java.sql.Date endDate) throws RemoteException;
    java.math.BigDecimal getTotalInventoryValue() throws RemoteException;
    
    // Sale items
    List<SaleItem> findSaleItemsBySaleId(Long saleId) throws RemoteException;
    
    // ROI metrics
    java.util.Map<String, Object> getDashboardMetrics() throws RemoteException;
    java.util.List<java.util.Map<String, Object>> getBestSellingProducts(int limit) throws RemoteException;
    java.util.Map<String, java.math.BigDecimal> getCashierPerformance() throws RemoteException;
}
