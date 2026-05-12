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
            // Start embedded ActiveMQ broker first
            System.out.println("\n--- Starting ActiveMQ Broker ---");
            activeMQBroker = new EmbeddedActiveMQBroker();
            activeMQBroker.start();
            
            // Wait a moment for broker to fully start
            Thread.sleep(2000);
            
            System.setProperty("java.rmi.server.hostname", "127.0.0.1");
            System.out.println("\n--- Starting RMI Services ---");
            System.out.println("Creating RMI registry on port 5000...");
            Registry theRegistry = LocateRegistry.createRegistry(5000);
            System.out.println("✓ RMI registry created successfully");
            
            // Start notification publisher
            System.out.println("\n--- Starting Notification System ---");
            publisher = new LowStockNotificationPublisher();
            System.out.println("✓ Notification publisher ready");
            
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
            System.out.println("\nWaiting for client connections...\n");
            
        } catch (Exception ex) {
            System.err.println("\n*** ERROR: Server failed to start ***");
            System.err.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
