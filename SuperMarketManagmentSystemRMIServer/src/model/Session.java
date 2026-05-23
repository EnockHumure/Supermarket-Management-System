package model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "user_session")
public class Session implements Serializable {
    
    public static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "session_id", length = 100)
    private String sessionId;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "login_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date loginTime;
    
    @Column(name = "last_activity")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastActivity;
    
    @Column(name = "is_active")
    private Boolean isActive;
    
    @Column(name = "ip_address")
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
