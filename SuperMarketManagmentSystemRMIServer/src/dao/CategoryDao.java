package dao;

import java.util.Collections;
import java.util.List;
import model.Category;
import org.hibernate.*;

public class CategoryDao {
    
    public Category registerCategory(Category categoryObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            ss.save(categoryObj);
            tr.commit();
            ss.close();
            return categoryObj;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public Category updateCategory(Category categoryObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            ss.update(categoryObj);
            tr.commit();
            ss.close();
            return categoryObj;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public Category deleteCategory(Category categoryObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            
            // First fetch the full category object to ensure all fields are populated
            Category existingCategory = (Category) ss.get(Category.class, categoryObj.getCategoryId());
            
            if (existingCategory == null) {
                tr.rollback();
                ss.close();
                throw new Exception("Category not found");
            }
            
            // Check if category has products
            Query countQuery = ss.createQuery("SELECT COUNT(p) FROM Product p WHERE p.category.categoryId = :categoryId");
            countQuery.setParameter("categoryId", existingCategory.getCategoryId());
            Long productCount = (Long) countQuery.uniqueResult();
            
            if (productCount != null && productCount > 0) {
                tr.rollback();
                ss.close();
                throw new Exception("Cannot delete category. " + productCount + " product(s) are assigned to this category.");
            }
            
            ss.delete(existingCategory);
            tr.commit();
            ss.close();
            return existingCategory;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }
    
    public Category findCategoryById(Category categoryObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Category found = (Category) ss.get(Category.class, categoryObj.getCategoryId());
            ss.close();
            return found;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public List<Category> findAllCategories() {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            List<Category> categories = ss.createQuery("SELECT c FROM Category c").list();
            ss.close();
            return categories;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Collections.EMPTY_LIST;
    }
    
    public List<Category> searchCategoriesByName(String name) {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            Query query = ss.createQuery("FROM Category c WHERE LOWER(c.name) LIKE LOWER(:name) ORDER BY c.name ASC");
            query.setParameter("name", "%" + name + "%");
            List<Category> categories = query.list();
            return categories;
        } catch (Exception ex) {
            System.err.println("Error searching categories by name: " + ex.getMessage());
            ex.printStackTrace();
            return Collections.EMPTY_LIST;
        } finally {
            if (ss != null && ss.isOpen()) {
                ss.close();
            }
        }
    }
    
    public boolean hasProducts(Long categoryId) {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            Query query = ss.createQuery("SELECT COUNT(p) FROM Product p WHERE p.category.categoryId = :categoryId");
            query.setParameter("categoryId", categoryId);
            Long count = (Long) query.uniqueResult();
            return count != null && count > 0;
        } catch (Exception ex) {
            System.err.println("Error checking category products: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        } finally {
            if (ss != null && ss.isOpen()) {
                ss.close();
            }
        }
    }
}
