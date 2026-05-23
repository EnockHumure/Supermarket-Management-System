package model;

import java.io.Serializable;
import java.util.Date;

public class Session implements Serializable {
    
    public static final long serialVersionUID = 1L;
    
    private String sessionId;
    private User user;
    private Date loginTime;
    private Date lastActivity;
    private Boolean isActive;
    private String ipAddress;

    public Session() {
    }

    public Session(String sessionId, User user, Date loginTime, Date lastActivity, Boolean isActive, String ipAddress) {
        this.sessionId = sessionId;
        this.user = user;
        this.loginTime = loginTime;
        this.lastActivity = lastActivity;
        this.isActive = isActive;
        this.ipAddress = ipAddress;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public Date getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(Date lastActivity) {
        this.lastActivity = lastActivity;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
