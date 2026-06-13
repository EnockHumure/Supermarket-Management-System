package util;

import dao.HibernateUtil;
import model.Session;
import model.User;
import org.hibernate.Transaction;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    
    private static final long SESSION_TIMEOUT = 60 * 60 * 1000; // 60 minutes (1 hour)
    private static final Map<String, Session> activeSessions = new ConcurrentHashMap<>();
    private static Timer cleanupTimer;
    
    static {
        // Start cleanup task to remove expired sessions every 5 minutes
        cleanupTimer = new Timer(true);
        cleanupTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                cleanupExpiredSessions();
            }
        }, 5 * 60 * 1000, 5 * 60 * 1000);
    }
    
    public static String createSession(User user) {
        System.out.println("[SessionManager] createSession called for user: " + user.getUsername());
        String sessionId = UUID.randomUUID().toString();
        Date now = new Date();
        
        System.out.println("[SessionManager] Creating session object...");
        Session session = new Session();
        session.setSessionId(sessionId);
        session.setUser(user);
        session.setLoginTime(now);
        session.setLastActivity(now);
        session.setIsActive(true);
        session.setIpAddress("127.0.0.1");
        
        System.out.println("[SessionManager] Storing session in memory...");
        activeSessions.put(sessionId, session);
        System.out.println("[SessionManager] Session stored in memory");
        System.out.println("[SessionManager] Returning sessionId: " + sessionId);
        System.out.println("✓ Session created: " + sessionId + " for user: " + user.getUsername());
        return sessionId;
    }
    
    public static User validateSession(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            return null;
        }
        
        Session session = activeSessions.get(sessionId);
        if (session == null) {
            return null;
        }
        
        // Check if session expired
        Date now = new Date();
        long timeSinceLastActivity = now.getTime() - session.getLastActivity().getTime();
        
        if (timeSinceLastActivity > SESSION_TIMEOUT) {
            invalidateSession(sessionId);
            System.out.println("⚠ Session expired: " + sessionId + " (inactive for " + (timeSinceLastActivity/1000) + " seconds)");
            return null;
        }
        
        // DO NOT update last activity - let it expire after 3 minutes
        System.out.println("✓ Session valid: " + sessionId + " (inactive for " + (timeSinceLastActivity/1000) + " seconds, expires in " + ((SESSION_TIMEOUT - timeSinceLastActivity)/1000) + " seconds)");
        
        return session.getUser();
    }
    
    public static void invalidateSession(String sessionId) {
        Session session = activeSessions.remove(sessionId);
        if (session != null) {
            System.out.println("✓ Session invalidated: " + sessionId);
        }
    }
    
    public static List<Session> getActiveSessions() {
        return new ArrayList<>(activeSessions.values());
    }
    
    public static int getActiveSessionCount() {
        return activeSessions.size();
    }
    
    private static void updateSessionActivity(String sessionId, Date lastActivity) {
        Session session = activeSessions.get(sessionId);
        if (session != null) {
            session.setLastActivity(lastActivity);
        }
    }
    
    private static void cleanupExpiredSessions() {
        Date now = new Date();
        List<String> expiredSessions = new ArrayList<>();
        
        for (Map.Entry<String, Session> entry : activeSessions.entrySet()) {
            long timeSinceLastActivity = now.getTime() - entry.getValue().getLastActivity().getTime();
            if (timeSinceLastActivity > SESSION_TIMEOUT) {
                expiredSessions.add(entry.getKey());
            }
        }
        
        for (String sessionId : expiredSessions) {
            invalidateSession(sessionId);
        }
        
        if (!expiredSessions.isEmpty()) {
            System.out.println("🧹 Cleaned up " + expiredSessions.size() + " expired session(s)");
        }
    }
}
