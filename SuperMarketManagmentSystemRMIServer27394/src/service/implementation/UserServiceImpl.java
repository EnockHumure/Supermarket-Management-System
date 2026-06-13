package service.implementation;

import dao.UserDao;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import model.User;
import service.UserService;
import util.EmailOTPUtil;
import util.EmailNotificationUtil;
import util.SessionManager;

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
                
                // Send notification to admin that OTP was generated for this user
                notifyAdminOfOtpGeneration(user);
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
            boolean verified = dao.verifyOtp(username, otp);
            
            if (verified) {
                // Send welcome email notification
                User user = dao.findUserByUsername(username);
                if (user != null && user.getEmail() != null && !user.getEmail().isEmpty()) {
                    sendWelcomeNotification(user);
                }
                
                // Notify admin if not admin user
                if (user != null && user.getRole() != model.ERole.ADMIN) {
                    notifyAdminOfLogin(user);
                }
            }
            
            return verified;
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
    
    @Override
    public String loginWithSession(String username, String password) throws RemoteException {
        System.out.println("[UserServiceImpl] loginWithSession called for: " + username);
        try {
            System.out.println("[UserServiceImpl] Finding user for login...");
            User user = dao.findUserForLogin(username);
            System.out.println("[UserServiceImpl] User found: " + (user != null ? user.getUsername() : "NULL"));
            
            if (user != null && user.getIsActive()) {
                if (password.equals(user.getPasswordHash())) {
                    System.out.println("[UserServiceImpl] Password verified, creating session...");
                    
                    // Create session FIRST
                    String sessionId = SessionManager.createSession(user);
                    System.out.println("[UserServiceImpl] Session created: " + sessionId);
                    
                    // Reset failed attempts asynchronously (don't block login)
                    new Thread(() -> {
                        try {
                            System.out.println("[UserServiceImpl] Resetting failed attempts in background...");
                            dao.resetFailedLoginAttempt(username);
                        } catch (Exception e) {
                            System.err.println("[UserServiceImpl] Error resetting failed attempts: " + e.getMessage());
                        }
                    }).start();
                    
                    // Send welcome notification
                    sendWelcomeNotification(user);
                    
                    // Notify admin if not admin user
                    if (user.getRole() != model.ERole.ADMIN) {
                        notifyAdminOfLogin(user);
                    }
                    
                    System.out.println("✓ User logged in: " + username + " (Session: " + sessionId + ")");
                    return sessionId;
                }
            }
            
            System.out.println("[UserServiceImpl] Authentication failed");
            return null;
        } catch (Exception ex) {
            System.err.println("[UserServiceImpl] Login error: " + ex.getMessage());
            ex.printStackTrace();
            throw new RemoteException("Login failed: " + ex.getMessage());
        }
    }
    
    @Override
    public void logout(String sessionId) throws RemoteException {
        try {
            SessionManager.invalidateSession(sessionId);
            System.out.println("✓ User logged out (Session: " + sessionId + ")");
        } catch (Exception ex) {
            throw new RemoteException("Error logging out: " + ex.getMessage());
        }
    }
    
    @Override
    public User validateSession(String sessionId) throws RemoteException {
        try {
            return SessionManager.validateSession(sessionId);
        } catch (Exception ex) {
            throw new RemoteException("Error validating session: " + ex.getMessage());
        }
    }
    
    /**
     * Send welcome email notification after successful OTP login
     */
    private void sendWelcomeNotification(User user) {
        try {
            if (user.getEmail() == null || user.getEmail().isEmpty()) {
                return;
            }
            
            String subject = "✅ Login Successful - Welcome Back!";
            String body = "Dear " + user.getFullName() + ",\n\n" +
                         "Your login to the Supermarket Management System was successful!\n\n" +
                         "Login Details:\n" +
                         "👤 Username: " + user.getUsername() + "\n" +
                         "💼 Role: " + user.getRole() + "\n" +
                         "📅 Date: " + new java.util.Date() + "\n\n" +
                         "If this was not you, please contact the system administrator immediately.\n\n" +
                         "Best regards,\nSupermarket Management System";
            
            EmailNotificationUtil.sendNotification(user.getEmail(), subject, body);
            System.out.println("✓ Welcome email sent to: " + user.getEmail());
            
        } catch (Exception ex) {
            System.err.println("Error sending welcome email: " + ex.getMessage());
        }
    }
    
    /**
     * Notify admin when a manager or cashier logs in
     */
    private void notifyAdminOfLogin(User user) {
        try {
            // Get all admin users
            List<User> admins = dao.findAllUsers();
            
            for (User admin : admins) {
                if (admin.getRole() == model.ERole.ADMIN && 
                    admin.getEmail() != null && 
                    !admin.getEmail().isEmpty()) {
                    
                    String subject = "🔔 User Login Notification - " + user.getRole();
                    String body = "Dear Admin,\n\n" +
                                 "A user has logged into the system.\n\n" +
                                 "👤 User Details:\n" +
                                 "   Name: " + user.getFullName() + "\n" +
                                 "   Username: " + user.getUsername() + "\n" +
                                 "   Role: " + user.getRole() + "\n" +
                                 "   Email: " + (user.getEmail() != null ? user.getEmail() : "N/A") + "\n\n" +
                                 "📅 Login Time: " + new java.util.Date() + "\n\n" +
                                 "You can view the login activity in the system logs.\n\n" +
                                 "Best regards,\nSupermarket Management System";
                    
                    EmailNotificationUtil.sendNotification(admin.getEmail(), subject, body);
                    System.out.println("✓ Admin notification sent to: " + admin.getEmail());
                }
            }
            
        } catch (Exception ex) {
            System.err.println("Error sending admin notification: " + ex.getMessage());
        }
    }
    
    /**
     * Notify admin when OTP is generated for a user
     */
    private void notifyAdminOfOtpGeneration(User user) {
        try {
            List<User> admins = dao.findAllUsers();
            
            for (User admin : admins) {
                if (admin.getRole() == model.ERole.ADMIN && 
                    admin.getEmail() != null && 
                    !admin.getEmail().isEmpty()) {
                    
                    String subject = "🔐 OTP Generated for User - " + user.getUsername();
                    String body = "Dear Admin,\n\n" +
                                 "An OTP has been generated for user login.\n\n" +
                                 "👤 User Details:\n" +
                                 "   Name: " + user.getFullName() + "\n" +
                                 "   Username: " + user.getUsername() + "\n" +
                                 "   Role: " + user.getRole() + "\n" +
                                 "   Email: " + (user.getEmail() != null ? user.getEmail() : "N/A") + "\n\n" +
                                 "📅 Request Time: " + new java.util.Date() + "\n\n" +
                                 "The user will receive the OTP via email.\n\n" +
                                 "Best regards,\nSupermarket Management System";
                    
                    EmailNotificationUtil.sendNotification(admin.getEmail(), subject, body);
                    System.out.println("✓ Admin OTP notification sent to: " + admin.getEmail());
                }
            }
            
        } catch (Exception ex) {
            System.err.println("Error sending admin OTP notification: " + ex.getMessage());
        }
    }
}
