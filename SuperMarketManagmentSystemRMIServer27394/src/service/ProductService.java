package service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import model.Product;

public interface ProductService extends Remote {
    Product registerProductRecord(Product theProduct) throws RemoteException;
    Product updateProductRecord(Product theProduct) throws RemoteException;
    Product deleteProductRecord(Product theProduct) throws RemoteException;
    Product findProductRecordById(Product theProduct) throws RemoteException;
    Product findProductByBarcode(String barcode) throws RemoteException;
    Product findProductById(Long productId) throws RemoteException;
    List<Product> findAllProductRecords() throws RemoteException;
    
    // Inventory tracking methods
    List<Product> findLowStockProducts() throws RemoteException;
    List<Product> findOutOfStockProducts() throws RemoteException;
    List<Product> searchProductsByName(String name) throws RemoteException;
    List<Product> searchProductsByCategory(Long categoryId) throws RemoteException;
}
