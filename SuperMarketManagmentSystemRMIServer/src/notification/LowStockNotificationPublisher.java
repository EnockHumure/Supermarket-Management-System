package notification;

import java.util.Date;
import model.LowStockEvent;
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;

public class LowStockNotificationPublisher {
    
    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String QUEUE_NAME = "low.stock.alerts";
    
    private Connection connection;
    private Session session;
    private MessageProducer producer;
    
    public LowStockNotificationPublisher() {
        try {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);
            connection = connectionFactory.createConnection();
            connection.start();
            
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(QUEUE_NAME);
            producer = session.createProducer(destination);
            
            System.out.println("LowStockNotificationPublisher initialized successfully");
        } catch (Exception ex) {
            // Silently disable publisher if ActiveMQ is not available
            // This is optional functionality
        }
    }
    
    public void publishLowStockAlert(Long productId, String productName, String barcode, 
                                     Integer currentStock, Integer reorderLevel) {
        if (session == null || producer == null) {
            return; // Publisher not initialized
        }
        try {
            LowStockEvent event = new LowStockEvent(
                productId, productName, barcode, currentStock, reorderLevel, 
                new Date().toString()
            );
            
            ObjectMessage message = session.createObjectMessage();
            message.setObject(event);
            
            producer.send(message);
            System.out.println("\n📢 LOW STOCK ALERT PUBLISHED:");
            System.out.println("   Product: " + productName);
            System.out.println("   Stock: " + currentStock + " (Reorder: " + reorderLevel + ")");
        } catch (Exception ex) {
            // Silently fail if ActiveMQ is not available
        }
    }
    
    public void close() {
        try {
            if (producer != null) producer.close();
            if (session != null) session.close();
            if (connection != null) connection.close();
        } catch (Exception ex) {
            // Silently fail
        }
    }
}
