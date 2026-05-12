package dao;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import model.User;
import model.UserProfile;
import org.hibernate.*;

public class UserDao {
    
    public User registerUser(User userObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            ss.save(userObj);
            tr.commit();
            ss.close();
            return userObj;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public User updateUser(User userObj) {
        Session ss = null;
        Transaction tr = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            tr = ss.beginTransaction();
            
            // Update username, fullName, email, role, and isActive
            Query query = ss.createQuery(
                "UPDATE User SET username = :username, fullName = :fullName, email = :email, role = :role, isActive = :isActive WHERE userId = :userId"
            );
            query.setParameter("username", userObj.getUsername());
            query.setParameter("fullName", userObj.getFullName());
            query.setParameter("email", userObj.getEmail());
            query.setParameter("role", userObj.getRole());
            query.setParameter("isActive", userObj.getIsActive());
            query.setParameter("userId", userObj.getUserId());
            
            int updated = query.executeUpdate();
            tr.commit();
            
            System.out.println("Updated " + updated + " user record(s) - Username: " + userObj.getUsername());
            return updated > 0 ? userObj : null;
        } catch (Exception ex) {
            if (tr != null) tr.rollback();
            System.err.println("ERROR updating user: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        } finally {
            if (ss != null && ss.isOpen()) {
                ss.close();
            }
        }
    }
    
    public User deleteUser(User userObj) {
        Session ss = null;
        Transaction tr = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            tr = ss.beginTransaction();
            
            // Check if user has sales using native SQL
            Long saleCount = ((Number) ss.createSQLQuery(
                "SELECT COUNT(*) FROM sale WHERE cashier_id = :userId"
            ).setParameter("userId", userObj.getUserId()).uniqueResult()).longValue();
            
            if (saleCount > 0) {
                throw new IllegalStateException("Cannot delete user with " + saleCount + " sale(s)");
            }
            
            // Delete user profile if exists
            ss.createSQLQuery("DELETE FROM user_profile WHERE user_id = :userId")
                .setParameter("userId", userObj.getUserId()).executeUpdate();
            
            // Delete user
            int deleted = ss.createSQLQuery("DELETE FROM users WHERE user_id = :userId")
                .setParameter("userId", userObj.getUserId()).executeUpdate();
            
            tr.commit();
            System.out.println("Deleted user: " + userObj.getUsername());
            return deleted > 0 ? userObj : null;
        } catch (IllegalStateException ex) {
            if (tr != null) tr.rollback();
            throw new RuntimeException(ex.getMessage());
        } catch (Exception ex) {
            if (tr != null) tr.rollback();
            System.err.println("ERROR deleting user: " + ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException("Failed to delete user: " + ex.getMessage());
        } finally {
            if (ss != null && ss.isOpen()) {
                ss.close();
            }
        }
    }
    
    public User findUserById(User userObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            User found = (User) ss.get(User.class, userObj.getUserId());
            ss.close();
            return found;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public User findUserByUsername(String username) {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            // Case-insensitive username search
            Query query = ss.createQuery("FROM User u WHERE LOWER(u.username) = LOWER(:username)");
            query.setParameter("username", username);
            User user = (User) query.uniqueResult();
            
            if (user != null) {
                System.out.println("Found user: " + user.getUsername() + " (searched for: " + username + ")");
            } else {
                System.out.println("User not found: " + username);
            }
            
            return user;
        } catch (Exception ex) {
            System.err.println("Error finding user: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        } finally {
            if (ss != null && ss.isOpen()) {
                ss.close();
            }
        }
    }
    
    public User findUserForLogin(String username) {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            // Case-insensitive username search for login
            Query query = ss.createQuery("FROM User u WHERE LOWER(u.username) = LOWER(:username)");
            query.setParameter("username", username);
            User user = (User) query.uniqueResult();
            
            if (user != null) {
                System.out.println("Login attempt for user: " + user.getUsername() + " (Role: " + user.getRole() + ")");
            } else {
                System.out.println("Login failed - User not found: " + username);
            }
            
            return user;
        } catch (Exception ex) {
            System.err.println("Error finding user for login: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        } finally {
            if (ss != null && ss.isOpen()) {
                ss.close();
            }
        }
    }
    
    public List<User> findAllUsers() {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            // Use native SQL to avoid Hibernate mapping issues
            List<Object[]> results = ss.createSQLQuery(
                "SELECT user_id, username, full_name, email, role, is_active FROM users ORDER BY user_id"
            ).list();
            
            List<User> users = new java.util.ArrayList<>();
            for (Object[] row : results) {
                User u = new User();
                u.setUserId(((Number) row[0]).longValue());
                u.setUsername((String) row[1]);
                u.setFullName((String) row[2]);
                u.setEmail((String) row[3]);
                u.setRole(model.ERole.valueOf((String) row[4]));
                u.setIsActive((Boolean) row[5]);
                users.add(u);
            }
            
            System.out.println("UserDao.findAllUsers() returned " + users.size() + " users");
            return users;
        } catch (Exception ex) {
            System.err.println("ERROR in findAllUsers: " + ex.getMessage());
            ex.printStackTrace();
            return Collections.EMPTY_LIST;
        } finally {
            if (ss != null && ss.isOpen()) {
                ss.close();
            }
        }
    }
    
    // Account lockout methods
    public void incrementFailedLoginAttempt(String username) {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            Query query = ss.createQuery("UPDATE User SET failedLoginAttempts = failedLoginAttempts + 1 WHERE LOWER(username) = LOWER(:username)");
            query.setParameter("username", username);
            int updated = query.executeUpdate();
            tr.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (ss != null && ss.isOpen()) {
                ss.close();
            }
        }
    }
    
    public void resetFailedLoginAttempt(String username) {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            Query query = ss.createQuery("UPDATE User SET failedLoginAttempts = 0 WHERE LOWER(username) = LOWER(:username)");
            query.setParameter("username", username);
            int updated = query.executeUpdate();
            tr.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (ss != null && ss.isOpen()) {
                ss.close();
            }
        }
    }
    
    public boolean isAccountLocked(String username) {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            Query query = ss.createQuery("SELECT u.failedLoginAttempts FROM User u WHERE LOWER(u.username) = LOWER(:username)");
            query.setParameter("username", username);
            Integer attempts = (Integer) query.uniqueResult();
            return attempts != null && attempts >= 3;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            if (ss != null && ss.isOpen()) {
                ss.close();
            }
        }
    }
    
    public void generateOtp(String username) {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            
            // Generate 6-digit OTP
            int otp = (int)(Math.random() * 900000) + 100000;
            Date expiresAt = new Date(System.currentTimeMillis() + 5 * 60 * 1000); // 5 minutes
            
            Query query = ss.createQuery("UPDATE User SET otpCode = :otp, otpExpiresAt = :expires WHERE LOWER(username) = LOWER(:username)");
            query.setParameter("otp", String.valueOf(otp));
            query.setParameter("expires", expiresAt);
            query.setParameter("username", username);
            int updated = query.executeUpdate();
            
            tr.commit();
            
            if (updated > 0) {
                System.out.println("\n========================================");
                System.out.println("OTP GENERATED FOR: " + username);
                System.out.println("OTP CODE: " + otp);
                System.out.println("EXPIRES AT: " + expiresAt);
                System.out.println("========================================\n");
            }
        } catch (Exception ex) {
            System.err.println("ERROR generating OTP: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            if (ss != null && ss.isOpen()) {
                ss.close();
            }
        }
    }
    
    public void saveOtp(String username, String otp) {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            
            Date expiresAt = new Date(System.currentTimeMillis() + 5 * 60 * 1000); // 5 minutes
            
            Query query = ss.createQuery("UPDATE User SET otpCode = :otp, otpExpiresAt = :expires WHERE LOWER(username) = LOWER(:username)");
            query.setParameter("otp", otp);
            query.setParameter("expires", expiresAt);
            query.setParameter("username", username);
            query.executeUpdate();
            
            tr.commit();
        } catch (Exception ex) {
            System.err.println("ERROR saving OTP: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            if (ss != null && ss.isOpen()) {
                ss.close();
            }
        }
    }
    
    public boolean verifyOtp(String username, String otp) {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            
            // Get user with OTP info (case-insensitive)
            Query query = ss.createQuery("FROM User WHERE LOWER(username) = LOWER(:username)");
            query.setParameter("username", username);
            User user = (User) query.uniqueResult();
            
            if (user == null) {
                System.out.println("OTP verification FAILED: User not found - " + username);
                return false;
            }
            
            if (user.getOtpCode() == null) {
                System.out.println("OTP verification FAILED: No OTP found for user - " + username);
                return false;
            }
            
            // Check if OTP expired
            Date now = new Date();
            if (now.after(user.getOtpExpiresAt())) {
                System.out.println("OTP verification FAILED: OTP expired for - " + username);
                return false;
            }
            
            // Verify OTP
            boolean valid = user.getOtpCode().equals(otp);
            
            if (valid) {
                System.out.println("OTP verification SUCCESSFUL for " + username);
                // Clear OTP after verification
                Query clearQuery = ss.createQuery("UPDATE User SET otpCode = null, otpExpiresAt = null WHERE LOWER(username) = LOWER(:username)");
                clearQuery.setParameter("username", username);
                clearQuery.executeUpdate();
            } else {
                System.out.println("OTP verification FAILED: Invalid OTP for " + username + ". Expected: " + user.getOtpCode() + ", Got: " + otp);
            }
            
            return valid;
        } catch (Exception ex) {
            System.err.println("ERROR verifying OTP: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        } finally {
            if (ss != null && ss.isOpen()) {
                ss.close();
            }
        }
    }
    
    public void setPassword(String username, String password) {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            
            Query query = ss.createQuery("UPDATE User SET passwordHash = :password WHERE LOWER(username) = LOWER(:username)");
            query.setParameter("password", password);
            query.setParameter("username", username);
            query.executeUpdate();
            
            tr.commit();
        } catch (Exception ex) {
            System.err.println("ERROR setting password: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            if (ss != null && ss.isOpen()) {
                ss.close();
            }
        }
    }
}
