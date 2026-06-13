package util;

import dao.HibernateUtil;
import org.hibernate.Session;

public class TestHibernateConnection {
    
    public static void main(String[] args) {
        System.out.println("=== Testing Hibernate Connection ===\n");
        
        try {
            System.out.println("1. Getting SessionFactory...");
            org.hibernate.SessionFactory sf = HibernateUtil.getSessionFactory();
            System.out.println("✓ SessionFactory obtained\n");
            
            System.out.println("2. Opening Hibernate session...");
            Session session = sf.openSession();
            System.out.println("✓ Session opened\n");
            
            System.out.println("3. Testing simple query...");
            Object result = session.createSQLQuery("SELECT COUNT(*) FROM users").uniqueResult();
            System.out.println("✓ Query executed: " + result + " users found\n");
            
            System.out.println("4. Closing session...");
            session.close();
            System.out.println("✓ Session closed\n");
            
            System.out.println("=== ALL TESTS PASSED ===");
            System.out.println("Hibernate is working correctly!");
            
        } catch (Exception ex) {
            System.err.println("\n=== TEST FAILED ===");
            System.err.println("Error: " + ex.getMessage());
            ex.printStackTrace();
            System.err.println("\nPlease fix the database connection issue before starting the server.");
        }
    }
}
