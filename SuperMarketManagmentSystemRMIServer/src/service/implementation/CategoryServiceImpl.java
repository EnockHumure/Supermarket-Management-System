package service.implementation;

import dao.CategoryDao;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import model.Category;
import service.CategoryService;

public class CategoryServiceImpl extends UnicastRemoteObject implements CategoryService {

    CategoryDao dao = new CategoryDao();
    
    public CategoryServiceImpl() throws RemoteException {
    }
    
    @Override
    public Category registerCategoryRecord(Category theCategory) throws RemoteException {
        return dao.registerCategory(theCategory);
    }

    @Override
    public Category updateCategoryRecord(Category theCategory) throws RemoteException {
        return dao.updateCategory(theCategory);
    }

    @Override
    public Category deleteCategoryRecord(Category theCategory) throws RemoteException {
        return dao.deleteCategory(theCategory);
    }

    @Override
    public Category findCategoryRecordById(Category theCategory) throws RemoteException {
        return dao.findCategoryById(theCategory);
    }

    @Override
    public List<Category> findAllCategoryRecords() throws RemoteException {
        return dao.findAllCategories();
    }
    
    @Override
    public List<Category> searchCategoriesByName(String name) throws RemoteException {
        try {
            return dao.searchCategoriesByName(name);
        } catch (Exception ex) {
            throw new RemoteException("Error searching categories: " + ex.getMessage());
        }
    }
    
    @Override
    public boolean hasProducts(Long categoryId) throws RemoteException {
        try {
            return dao.hasProducts(categoryId);
        } catch (Exception ex) {
            throw new RemoteException("Error checking category products: " + ex.getMessage());
        }
    }
}
