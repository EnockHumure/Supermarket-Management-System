package controller;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import service.implementation.*;
import notification.LowStockNotificationPublisher;
import notification.EmbeddedActiveMQBroker;

public class Server {
    
    private static String repeat(String str, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }
    
    public static void main(String[] args) {
        System.out.println("=== Starting RMI Server ===");
        EmbeddedActiveMQBroker activeMQBroker = null;
        LowStockNotificationPublisher publisher = null;
        
        try {
            // Test Hibernate/Database connection first
            System.out.println("\n--- Testing Database Connection ---");
            try {
                dao.HibernateUtil.getSessionFactory();
                System.out.println("✓ Database connection successful");
            } catch (Exception dbEx) {
                System.err.println("✗ Database connection FAILED: " + dbEx.getMessage());
                System.err.println("Please check:");
                System.err.println("  1. PostgreSQL is running");
                System.err.println("  2. Database 'supermarket_management_system_db' exists");
                System.err.println("  3. Username/password in hibernate.cfg.xml is correct");
                throw dbEx;
            }
            
            // Start embedded ActiveMQ broker first
            System.out.println("\n--- Starting ActiveMQ Broker ---");
            try {
                activeMQBroker = new EmbeddedActiveMQBroker();
                activeMQBroker.start();
                System.out.println("✓ ActiveMQ broker started");
                Thread.sleep(2000);
            } catch (Exception amqEx) {
                System.out.println("⚠ ActiveMQ already running: " + amqEx.getMessage());
                activeMQBroker = null;
            }
            
            System.setProperty("java.rmi.server.hostname", "127.0.0.1");
            System.out.println("\n--- Starting RMI Services ---");
            System.out.println("Creating RMI registry on port 5000...");
            Registry theRegistry = LocateRegistry.createRegistry(5000);
            System.out.println("✓ RMI registry created successfully");
            
            // Start notification publisher
            System.out.println("\n--- Starting Notification System ---");
            try {
                publisher = new LowStockNotificationPublisher();
                System.out.println("✓ Notification publisher ready");
            } catch (Exception pubEx) {
                System.out.println("⚠ Notification publisher failed: " + pubEx.getMessage());
            }
            
            System.out.println("\n--- Binding RMI Services ---");
            theRegistry.rebind("user-service", new UserServiceImpl());
            System.out.println("✓ user-service bound");
            
            theRegistry.rebind("product-service", new ProductServiceImpl());
            System.out.println("✓ product-service bound");
            
            theRegistry.rebind("sale-service", new SaleServiceImpl());
            System.out.println("✓ sale-service bound");
            
            theRegistry.rebind("category-service", new CategoryServiceImpl());
            System.out.println("✓ category-service bound");
            
            System.out.println("\n" + repeat("=", 60));
            System.out.println("    SERVER READY");
            System.out.println(repeat("=", 60));
            System.out.println("✓ RMI Server running on port 5000");
            System.out.println("✓ ActiveMQ Broker running on tcp://localhost:61616");
            System.out.println("✓ Low Stock Notifications ACTIVE");
            System.out.println("\n📌 NEXT STEPS:");
            System.out.println("   1. Run LowStockNotificationConsumer to see alerts");
            System.out.println("   2. Start the client application");
            System.out.println("   3. Process sales to trigger low stock alerts");
            System.out.println(repeat("=", 60));
            System.out.println("\nServer is running. Press Ctrl+C to stop.\n");
            
            // Keep server running - THIS IS CRITICAL
            Thread.currentThread().join();
            
        } catch (Exception ex) {
            System.err.println("\n*** ERROR: Server failed to start ***");
            System.err.println("Error: " + ex.getMessage());
            ex.printStackTrace();
            System.err.println("\nServer will now exit.");
        }
    }
}
