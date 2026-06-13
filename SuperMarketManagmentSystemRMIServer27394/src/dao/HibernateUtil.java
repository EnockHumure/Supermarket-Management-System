package dao;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import java.sql.DriverManager;
import java.sql.Connection;

public class HibernateUtil {

    private static final SessionFactory sessionFactory;
    
    static {
        try {
            // Explicitly load PostgreSQL driver
            Class.forName("org.postgresql.Driver");
            System.out.println("PostgreSQL driver loaded successfully!");
            
            // Test database connection before creating SessionFactory
            String url = "jdbc:postgresql://localhost:5432/supermarket_management_system_db";
            String user = "postgres";
            String password = "Mataru@8";
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("Database connection test successful!");
            conn.close();
            
            // Create the SessionFactory from hibernate.cfg.xml
            sessionFactory = new Configuration().configure().buildSessionFactory();
            System.out.println("Hibernate SessionFactory created successfully!");
        } catch (ClassNotFoundException ex) {
            System.err.println("PostgreSQL driver NOT found! " + ex.getMessage());
            ex.printStackTrace();
            throw new ExceptionInInitializerError(ex);
        } catch (Exception ex) {
            System.err.println("Initial SessionFactory creation failed: " + ex.getMessage());
            ex.printStackTrace();
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
