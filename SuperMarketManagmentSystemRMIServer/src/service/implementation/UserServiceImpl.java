package service.implementation;

import dao.UserDao;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import model.User;
import service.UserService;
import util.EmailOTPUtil;

public class UserServiceImpl extends UnicastRemoteObject implements UserService {

    UserDao dao = new UserDao();
    
    public UserServiceImpl() throws RemoteException {
    }
    
    @Override
    public User registerUserRecord(User theUser) throws RemoteException {
        return dao.registerUser(theUser);
    }

    @Override
    public User updateUserRecord(User theUser) throws RemoteException {
        return dao.updateUser(theUser);
    }

    @Override
    public User deleteUserRecord(User theUser) throws RemoteException {
        return dao.deleteUser(theUser);
    }

    @Override
    public User findUserRecordById(User theUser) throws RemoteException {
        return dao.findUserById(theUser);
    }

    @Override
    public User findUserByUsername(String username) throws RemoteException {
        System.out.println("[UserServiceImpl] findUserByUsername called with: " + username);
        try {
            User user = dao.findUserByUsername(username);
            System.out.println("[UserServiceImpl] DAO returned: " + (user != null ? "User found - " + user.getUsername() : "NULL"));
            return user;
        } catch (Exception ex) {
            System.err.println("[UserServiceImpl] ERROR: " + ex.getMessage());
            ex.printStackTrace();
            throw new RemoteException("Error finding user: " + ex.getMessage());
        }
    }

    @Override
    public List<User> findAllUserRecords() throws RemoteException {
        return dao.findAllUsers();
    }

    @Override
    public User authenticateUser(String username, String password) throws RemoteException {
        try {
            User user = dao.findUserByUsername(username);
            if (user != null && user.getIsActive()) {
                if (password.equals(user.getPasswordHash())) {
                    dao.resetFailedLoginAttempt(username);
                    return user;
                }
            }
            return null;
        } catch (Exception ex) {
            System.err.println("Authentication error: " + ex.getMessage());
            ex.printStackTrace();
            throw new RemoteException("Authentication failed");
        }
    }
    
    @Override
    public User authenticateUserWithLockout(String username, String password) throws RemoteException {
        try {
            // Check if account is locked
            if (dao.isAccountLocked(username)) {
                throw new RemoteException("Account is locked. Please try again later.");
            }
            
            User user = dao.findUserForLogin(username);
            if (user != null && user.getIsActive()) {
                if (password.equals(user.getPasswordHash())) {
                    dao.resetFailedLoginAttempt(username);
                    return user;
                }
            }
            
            // Increment failed login attempt
            dao.incrementFailedLoginAttempt(username);
            
            // Check if account should be locked
            if (dao.isAccountLocked(username)) {
                throw new RemoteException("Account locked due to multiple failed attempts.");
            }
            
            return null;
        } catch (RemoteException ex) {
            throw ex;
        } catch (Exception ex) {
            System.err.println("Authentication error: " + ex.getMessage());
            ex.printStackTrace();
            throw new RemoteException("Authentication failed");
        }
    }

    @Override
    public void incrementFailedLoginAttempt(String username) throws RemoteException {
        try {
            dao.incrementFailedLoginAttempt(username);
        } catch (Exception ex) {
            throw new RemoteException("Error incrementing failed login attempt: " + ex.getMessage());
        }
    }

    @Override
    public void resetFailedLoginAttempt(String username) throws RemoteException {
        try {
            dao.resetFailedLoginAttempt(username);
        } catch (Exception ex) {
            throw new RemoteException("Error resetting failed login attempt: " + ex.getMessage());
        }
    }

    @Override
    public boolean isAccountLocked(String username) throws RemoteException {
        try {
            return dao.isAccountLocked(username);
        } catch (Exception ex) {
            throw new RemoteException("Error checking account lock status: " + ex.getMessage());
        }
    }
    
    @Override
    public void generateOtp(String username) throws RemoteException {
        try {
            User user = dao.findUserForLogin(username);
            if (user != null && user.getEmail() != null && !user.getEmail().isEmpty()) {
                // Generate and send OTP via email
                String otpCode = EmailOTPUtil.generateAndSendOTP(user.getEmail(), username);
                
                // Save OTP to database
                dao.saveOtp(username, otpCode);
                
                System.out.println("OTP sent to email: " + user.getEmail());
            } else {
                throw new RemoteException("User does not have an email address registered.");
            }
        } catch (Exception ex) {
            throw new RemoteException("Error generating OTP: " + ex.getMessage());
        }
    }
    
    @Override
    public boolean verifyOtp(String username, String otp) throws RemoteException {
        try {
            return dao.verifyOtp(username, otp);
        } catch (Exception ex) {
            throw new RemoteException("Error verifying OTP: " + ex.getMessage());
        }
    }
    
    @Override
    public void setPassword(String username, String password) throws RemoteException {
        try {
            dao.setPassword(username, password);
        } catch (Exception ex) {
            throw new RemoteException("Error setting password: " + ex.getMessage());
        }
    }
}
