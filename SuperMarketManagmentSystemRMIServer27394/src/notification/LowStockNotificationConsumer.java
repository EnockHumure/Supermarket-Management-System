package notification;

import java.util.Date;
import model.LowStockEvent;
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;

public class LowStockNotificationConsumer {
    
    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String QUEUE_NAME = "low.stock.alerts";
    
    private static String repeat(String str, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }
    
    public static void main(String[] args) {
        System.out.println("\n" + repeat("=", 60));
        System.out.println("    LOW STOCK NOTIFICATION CONSUMER");
        System.out.println(repeat("=", 60));
        
        try {
            System.out.println("Connecting to ActiveMQ broker...");
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);
            Connection connection = connectionFactory.createConnection();
            connection.start();
            
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(QUEUE_NAME);
            MessageConsumer consumer = session.createConsumer(destination);
            
            System.out.println("✓ Connected to: " + BROKER_URL);
            System.out.println("✓ Listening on queue: " + QUEUE_NAME);
            System.out.println("\n⏳ Waiting for low stock alerts...\n");
            System.out.println("(Process a sale in the client to trigger alerts)\n");
            
            consumer.setMessageListener(new MessageListener() {
                public void onMessage(Message message) {
                    try {
                        if (message instanceof ObjectMessage) {
                            ObjectMessage objectMessage = (ObjectMessage) message;
                            LowStockEvent event = (LowStockEvent) objectMessage.getObject();
                            
                            System.out.println("\n" + repeat("=", 60));
                            System.out.println("⚠️  LOW STOCK ALERT RECEIVED");
                            System.out.println(repeat("=", 60));
                            System.out.println("📦 Product Name    : " + event.getProductName());
                            System.out.println("🔢 Barcode         : " + event.getBarcode());
                            System.out.println("📊 Current Stock   : " + event.getCurrentStock());
                            System.out.println("⚡ Reorder Level   : " + event.getReorderLevel());
                            System.out.println("🕐 Alert Time      : " + event.getEventTime());
                            System.out.println(repeat("=", 60));
                            System.out.println("✓ Alert processed successfully\n");
                        }
                    } catch (Exception ex) {
                        System.err.println("Error processing message: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            });
            
            // Keep running
            System.out.println("\n💡 Consumer is running. Press Ctrl+C to stop.\n");
            Thread.sleep(Long.MAX_VALUE);
            
        } catch (Exception ex) {
            System.err.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
