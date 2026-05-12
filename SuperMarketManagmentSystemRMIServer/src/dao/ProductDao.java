package dao;

import java.util.Collections;
import java.util.List;
import model.Product;
import org.hibernate.*;

public class ProductDao {
    
    public Product registerProduct(Product productObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            ss.save(productObj);
            tr.commit();
            ss.close();
            return productObj;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public Product updateProduct(Product productObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            ss.update(productObj);
            tr.commit();
            ss.close();
            return productObj;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public Product deleteProduct(Product productObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            
            // Check if product has sale items
            Query countQuery = ss.createQuery("SELECT COUNT(si) FROM SaleItem si WHERE si.product.productId = :productId");
            countQuery.setParameter("productId", productObj.getProductId());
            Long saleItemCount = (Long) countQuery.uniqueResult();
            
            if (saleItemCount != null && saleItemCount > 0) {
                tr.rollback();
                ss.close();
                throw new Exception("Cannot delete product. " + saleItemCount + " sale item(s) reference this product.");
            }
            
            ss.delete(productObj);
            tr.commit();
            ss.close();
            return productObj;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }
    
    public Product findProductById(Product productObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Product found = (Product) ss.get(Product.class, productObj.getProductId());
            ss.close();
            return found;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public Product findProductById(Long productId) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Product found = (Product) ss.get(Product.class, productId);
            ss.close();
            return found;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public List<Product> findAllProducts() {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            List<Product> products = ss.createQuery("SELECT p FROM Product p").list();
            ss.close();
            return products;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Collections.EMPTY_LIST;
    }
    
    public Product findProductByBarcode(String barcode) {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            Query query = ss.createQuery("FROM Product p WHERE p.barcode = :barcode");
            query.setParameter("barcode", barcode);
            Product product = (Product) query.uniqueResult();
            return product;
        } catch (Exception ex) {
            System.err.println("Error finding product by barcode: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        } finally {
            if (ss != null && ss.isOpen()) {
                ss.close();
            }
        }
    }
    
    public List<Product> findLowStockProducts() {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            Query query = ss.createQuery("FROM Product p WHERE p.stockQuantity <= p.reorderLevel ORDER BY p.stockQuantity ASC");
            List<Product> products = query.list();
            return products;
        } catch (Exception ex) {
            System.err.println("Error finding low stock products: " + ex.getMessage());
            ex.printStackTrace();
            return Collections.EMPTY_LIST;
        } finally {
            if (ss != null && ss.isOpen()) {
                ss.close();
            }
        }
    }
    
    public List<Product> findOutOfStockProducts() {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            Query query = ss.createQuery("FROM Product p WHERE p.stockQuantity = 0 ORDER BY p.name ASC");
            List<Product> products = query.list();
            return products;
        } catch (Exception ex) {
            System.err.println("Error finding out of stock products: " + ex.getMessage());
            ex.printStackTrace();
            return Collections.EMPTY_LIST;
        } finally {
            if (ss != null && ss.isOpen()) {
                ss.close();
            }
        }
    }
    
    public List<Product> searchProductsByName(String name) {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            Query query = ss.createQuery("FROM Product p WHERE LOWER(p.name) LIKE LOWER(:name) ORDER BY p.name ASC");
            query.setParameter("name", "%" + name + "%");
            List<Product> products = query.list();
            return products;
        } catch (Exception ex) {
            System.err.println("Error searching products by name: " + ex.getMessage());
            ex.printStackTrace();
            return Collections.EMPTY_LIST;
        } finally {
            if (ss != null && ss.isOpen()) {
                ss.close();
            }
        }
    }
    
    public List<Product> searchProductsByCategory(Long categoryId) {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            Query query = ss.createQuery("FROM Product p WHERE p.category.categoryId = :categoryId ORDER BY p.name ASC");
            query.setParameter("categoryId", categoryId);
            List<Product> products = query.list();
            return products;
        } catch (Exception ex) {
            System.err.println("Error searching products by category: " + ex.getMessage());
            ex.printStackTrace();
            return Collections.EMPTY_LIST;
        } finally {
            if (ss != null && ss.isOpen()) {
                ss.close();
            }
        }
    }
}
