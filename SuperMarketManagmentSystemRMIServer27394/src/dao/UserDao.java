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
            System.out.println("[UserDao] findUserByUsername called with: " + username);
            ss = HibernateUtil.getSessionFactory().openSession();
            System.out.println("[UserDao] Session opened successfully");
            
            // Use native SQL to avoid lazy loading issues
            Object[] result = (Object[]) ss.createSQLQuery(
                "SELECT user_id, username, password_hash, full_name, email, role, is_active " +
                "FROM users WHERE LOWER(username) = LOWER(:username)"
            )
            .setParameter("username", username)
            .uniqueResult();
            
            if (result != null) {
                User user = new User();
                user.setUserId(((Number) result[0]).longValue());
                user.setUsername((String) result[1]);
                user.setPasswordHash((String) result[2]);
                user.setFullName((String) result[3]);
                user.setEmail((String) result[4]);
                user.setRole(model.ERole.valueOf((String) result[5]));
                user.setIsActive((Boolean) result[6]);
                
                System.out.println("[UserDao] User details - Username: " + user.getUsername() + ", Role: " + user.getRole() + ", Active: " + user.getIsActive());
                ss.close();
                return user;
            } else {
                System.out.println("[UserDao] User not found: " + username);
                ss.close();
                return null;
            }
        } catch (Exception ex) {
            System.err.println("[UserDao] ERROR finding user: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        } finally {
            if (ss != null && ss.isOpen()) {
                try {
                    ss.close();
                } catch (Exception e) {
                    System.err.println("[UserDao] Error closing session: " + e.getMessage());
                }
            }
        }
    }
    
    public User findUserForLogin(String username) {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            System.out.println("[UserDao] findUserForLogin - Opening session for: " + username);
            
            // Use native SQL to avoid lazy loading issues
            Object[] result = (Object[]) ss.createSQLQuery(
                "SELECT user_id, username, password_hash, full_name, email, role, is_active " +
                "FROM users WHERE LOWER(username) = LOWER(:username)"
            )
            .setParameter("username", username)
            .uniqueResult();
            
            if (result != null) {
                User user = new User();
                user.setUserId(((Number) result[0]).longValue());
                user.setUsername((String) result[1]);
                user.setPasswordHash((String) result[2]);
                user.setFullName((String) result[3]);
                user.setEmail((String) result[4]);
                user.setRole(model.ERole.valueOf((String) result[5]));
                user.setIsActive((Boolean) result[6]);
                
                System.out.println("[UserDao] User found: " + user.getUsername() + " (Role: " + user.getRole() + ")");
                ss.close();
                return user;
            } else {
                System.out.println("[UserDao] User not found: " + username);
                ss.close();
                return null;
            }
        } catch (Exception ex) {
            System.err.println("[UserDao] ERROR finding user for login: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        } finally {
            if (ss != null && ss.isOpen()) {
                try {
                    ss.close();
                } catch (Exception e) {
                    System.err.println("[UserDao] Error closing session: " + e.getMessage());
                }
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
        Transaction tr = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            tr = ss.beginTransaction();
            ss.createSQLQuery(
                "UPDATE users SET failed_login_attempts = COALESCE(failed_login_attempts, 0) + 1 WHERE LOWER(username) = LOWER(:username)"
            )
            .setParameter("username", username)
            .executeUpdate();
            tr.commit();
        } catch (Exception ex) {
            if (tr != null) tr.rollback();
            ex.printStackTrace();
        } finally {
            if (ss != null && ss.isOpen()) {
                ss.close();
            }
        }
    }
    
    public void resetFailedLoginAttempt(String username) {
        Session ss = null;
        Transaction tr = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            tr = ss.beginTransaction();
            ss.createSQLQuery(
                "UPDATE users SET failed_login_attempts = 0 WHERE LOWER(username) = LOWER(:username)"
            )
            .setParameter("username", username)
            .executeUpdate();
            tr.commit();
        } catch (Exception ex) {
            if (tr != null) tr.rollback();
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
            Object result = ss.createSQLQuery(
                "SELECT failed_login_attempts FROM users WHERE LOWER(username) = LOWER(:username)"
            )
            .setParameter("username", username)
            .uniqueResult();
            
            Integer attempts = result != null ? ((Number) result).intValue() : 0;
            return attempts >= 3;
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
        Transaction tr = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            tr = ss.beginTransaction();
            
            // Generate 6-digit OTP
            int otp = (int)(Math.random() * 900000) + 100000;
            Date expiresAt = new Date(System.currentTimeMillis() + 5 * 60 * 1000); // 5 minutes
            
            ss.createSQLQuery(
                "UPDATE users SET otp_code = :otp, otp_expires_at = :expires WHERE LOWER(username) = LOWER(:username)"
            )
            .setParameter("otp", String.valueOf(otp))
            .setParameter("expires", expiresAt)
            .setParameter("username", username)
            .executeUpdate();
            
            tr.commit();
            
            System.out.println("\n========================================");
            System.out.println("OTP GENERATED FOR: " + username);
            System.out.println("OTP CODE: " + otp);
            System.out.println("EXPIRES AT: " + expiresAt);
            System.out.println("========================================\n");
        } catch (Exception ex) {
            if (tr != null) tr.rollback();
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
        Transaction tr = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            tr = ss.beginTransaction();
            
            Date expiresAt = new Date(System.currentTimeMillis() + 5 * 60 * 1000); // 5 minutes
            
            ss.createSQLQuery(
                "UPDATE users SET otp_code = :otp, otp_expires_at = :expires WHERE LOWER(username) = LOWER(:username)"
            )
            .setParameter("otp", otp)
            .setParameter("expires", expiresAt)
            .setParameter("username", username)
            .executeUpdate();
            
            tr.commit();
        } catch (Exception ex) {
            if (tr != null) tr.rollback();
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
        Transaction tr = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            tr = ss.beginTransaction();
            
            // Use native SQL to get OTP info
            Object[] result = (Object[]) ss.createSQLQuery(
                "SELECT otp_code, otp_expires_at FROM users WHERE LOWER(username) = LOWER(:username)"
            )
            .setParameter("username", username)
            .uniqueResult();
            
            if (result == null) {
                System.out.println("OTP verification FAILED: User not found - " + username);
                return false;
            }
            
            String storedOtp = (String) result[0];
            Date expiresAt = (Date) result[1];
            
            if (storedOtp == null) {
                System.out.println("OTP verification FAILED: No OTP found for user - " + username);
                return false;
            }
            
            // Check if OTP expired
            Date now = new Date();
            if (expiresAt != null && now.after(expiresAt)) {
                System.out.println("OTP verification FAILED: OTP expired for - " + username);
                return false;
            }
            
            // Verify OTP
            boolean valid = storedOtp.equals(otp);
            
            if (valid) {
                System.out.println("OTP verification SUCCESSFUL for " + username);
                // Clear OTP after verification using native SQL
                ss.createSQLQuery(
                    "UPDATE users SET otp_code = NULL, otp_expires_at = NULL WHERE LOWER(username) = LOWER(:username)"
                )
                .setParameter("username", username)
                .executeUpdate();
                tr.commit();
            } else {
                System.out.println("OTP verification FAILED: Invalid OTP for " + username);
                tr.rollback();
            }
            
            return valid;
        } catch (Exception ex) {
            if (tr != null) tr.rollback();
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
        Transaction tr = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            tr = ss.beginTransaction();
            
            ss.createSQLQuery(
                "UPDATE users SET password_hash = :password WHERE LOWER(username) = LOWER(:username)"
            )
            .setParameter("password", password)
            .setParameter("username", username)
            .executeUpdate();
            
            tr.commit();
        } catch (Exception ex) {
            if (tr != null) tr.rollback();
            System.err.println("ERROR setting password: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            if (ss != null && ss.isOpen()) {
                ss.close();
            }
        }
    }
}
