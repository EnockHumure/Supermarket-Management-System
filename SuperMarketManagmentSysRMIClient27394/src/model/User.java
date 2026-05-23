package model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class User implements Serializable {
    
    public static final long serialVersionUID = 1L;
    
    private Long userId;
    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private ERole role;
    private Boolean isActive;
    private Date createdAt;
    private Integer failedLoginAttempts;
    private String otpCode;
    private Date otpExpiresAt;
    private UserProfile userProfile;
    private Set<Sale> sales = new HashSet<>();

    public User() {
    }

    public User(Long userId, String username, String passwordHash, String fullName, String email, ERole role, Boolean isActive, Date createdAt, Integer failedLoginAttempts, String otpCode, Date otpExpiresAt, UserProfile userProfile, Set<Sale> sales) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.failedLoginAttempts = failedLoginAttempts;
        this.otpCode = otpCode;
        this.otpExpiresAt = otpExpiresAt;
        this.userProfile = userProfile;
        this.sales = sales;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public ERole getRole() { return role; }
    public void setRole(ERole role) { this.role = role; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Integer getFailedLoginAttempts() { return failedLoginAttempts; }
    public void setFailedLoginAttempts(Integer failedLoginAttempts) { this.failedLoginAttempts = failedLoginAttempts; }

    public String getOtpCode() { return otpCode; }
    public void setOtpCode(String otpCode) { this.otpCode = otpCode; }

    public Date getOtpExpiresAt() { return otpExpiresAt; }
    public void setOtpExpiresAt(Date otpExpiresAt) { this.otpExpiresAt = otpExpiresAt; }

    public UserProfile getUserProfile() { return userProfile; }
    public void setUserProfile(UserProfile userProfile) { this.userProfile = userProfile; }

    public Set<Sale> getSales() { return sales; }
    public void setSales(Set<Sale> sales) { this.sales = sales; }
}
