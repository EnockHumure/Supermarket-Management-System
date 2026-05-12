package service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import model.Category;

public interface CategoryService extends Remote {
    Category registerCategoryRecord(Category theCategory) throws RemoteException;
    Category updateCategoryRecord(Category theCategory) throws RemoteException;
    Category deleteCategoryRecord(Category theCategory) throws RemoteException;
    Category findCategoryRecordById(Category theCategory) throws RemoteException;
    List<Category> findAllCategoryRecords() throws RemoteException;
    
    // Category methods
    List<Category> searchCategoriesByName(String name) throws RemoteException;
    boolean hasProducts(Long categoryId) throws RemoteException;
}
