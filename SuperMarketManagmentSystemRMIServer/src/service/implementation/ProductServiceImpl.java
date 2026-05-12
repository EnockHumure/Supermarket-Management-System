package service.implementation;

import dao.ProductDao;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import model.Product;
import service.ProductService;

public class ProductServiceImpl extends UnicastRemoteObject implements ProductService {

    ProductDao dao = new ProductDao();
    
    public ProductServiceImpl() throws RemoteException {
    }
    
    @Override
    public Product registerProductRecord(Product theProduct) throws RemoteException {
        return dao.registerProduct(theProduct);
    }

    @Override
    public Product updateProductRecord(Product theProduct) throws RemoteException {
        return dao.updateProduct(theProduct);
    }

    @Override
    public Product deleteProductRecord(Product theProduct) throws RemoteException {
        return dao.deleteProduct(theProduct);
    }

    @Override
    public Product findProductRecordById(Product theProduct) throws RemoteException {
        return dao.findProductById(theProduct);
    }

    @Override
    public List<Product> findAllProductRecords() throws RemoteException {
        return dao.findAllProducts();
    }
    
    @Override
    public Product findProductByBarcode(String barcode) throws RemoteException {
        return dao.findProductByBarcode(barcode);
    }
    
    @Override
    public Product findProductById(Long productId) throws RemoteException {
        return dao.findProductById(productId);
    }
    
    @Override
    public List<Product> findLowStockProducts() throws RemoteException {
        try {
            return dao.findLowStockProducts();
        } catch (Exception ex) {
            throw new RemoteException("Error finding low stock products: " + ex.getMessage());
        }
    }
    
    @Override
    public List<Product> findOutOfStockProducts() throws RemoteException {
        try {
            return dao.findOutOfStockProducts();
        } catch (Exception ex) {
            throw new RemoteException("Error finding out of stock products: " + ex.getMessage());
        }
    }
    
    @Override
    public List<Product> searchProductsByName(String name) throws RemoteException {
        try {
            return dao.searchProductsByName(name);
        } catch (Exception ex) {
            throw new RemoteException("Error searching products: " + ex.getMessage());
        }
    }
    
    @Override
    public List<Product> searchProductsByCategory(Long categoryId) throws RemoteException {
        try {
            return dao.searchProductsByCategory(categoryId);
        } catch (Exception ex) {
            throw new RemoteException("Error searching products by category: " + ex.getMessage());
        }
    }
}
