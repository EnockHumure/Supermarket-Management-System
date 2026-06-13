package service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import model.User;

public interface UserService extends Remote {
    User registerUserRecord(User theUser) throws RemoteException;
    User updateUserRecord(User theUser) throws RemoteException;
    User deleteUserRecord(User theUser) throws RemoteException;
    User findUserRecordById(User theUser) throws RemoteException;
    User findUserByUsername(String username) throws RemoteException;
    List<User> findAllUserRecords() throws RemoteException;
    User authenticateUser(String username, String password) throws RemoteException;
    
    // Authentication methods
    User authenticateUserWithLockout(String username, String password) throws RemoteException;
    void incrementFailedLoginAttempt(String username) throws RemoteException;
    void resetFailedLoginAttempt(String username) throws RemoteException;
    boolean isAccountLocked(String username) throws RemoteException;
    
    // OTP methods
    void generateOtp(String username) throws RemoteException;
    boolean verifyOtp(String username, String otp) throws RemoteException;
    
    // Password methods
    void setPassword(String username, String password) throws RemoteException;
    
    // Session methods
    String loginWithSession(String username, String password) throws RemoteException;
    void logout(String sessionId) throws RemoteException;
    User validateSession(String sessionId) throws RemoteException;
}
