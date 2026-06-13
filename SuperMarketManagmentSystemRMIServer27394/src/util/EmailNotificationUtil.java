package util;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailNotificationUtil {
    
    // Email configuration - CHANGE THESE TO YOUR EMAIL
    private static final String EMAIL_USERNAME = "humureenock@gmail.com";
    private static final String EMAIL_PASSWORD = "axak qnao yiea tddy";
    private static final String EMAIL_HOST = "smtp.gmail.com";
    private static final int EMAIL_PORT = 587;
    
    /**
     * Send low stock notification email
     * @param toEmail Recipient email address
     * @param productName Name of the product with low stock
     * @param barcode Product barcode
     * @param currentStock Current stock quantity
     * @param reorderLevel Reorder level threshold
     */
    public static void sendLowStockNotification(String toEmail, String productName, 
                                                 String barcode, Integer currentStock, 
                                                 Integer reorderLevel) throws Exception {
        String subject = "⚠️ LOW STOCK ALERT - " + productName;
        String body = "Dear User,\n\n" +
                     "This is an automated notification from the Supermarket Management System.\n\n" +
                     "⚠️ LOW STOCK ALERT\n\n" +
                     "The following product has reached or fallen below its reorder level:\n\n" +
                     "📦 Product Name : " + productName + "\n" +
                     "🔢 Barcode      : " + barcode + "\n" +
                     "📊 Current Stock: " + currentStock + "\n" +
                     "⚡ Reorder Level: " + reorderLevel + "\n\n" +
                     "Action Required: Please reorder this product immediately to avoid stockout.\n\n" +
                     "If you do not wish to receive these notifications, please contact the system administrator.\n\n" +
                     "Best regards,\nSupermarket Management System\n" +
                     "Automated Notification Service";
        
        sendEmail(toEmail, subject, body);
    }
    
    /**
     * Send generic notification email
     * @param toEmail Recipient email address
     * @param subject Email subject
     * @param body Email body content
     */
    public static void sendNotification(String toEmail, String subject, String body) throws Exception {
        sendEmail(toEmail, subject, body);
    }
    
    private static void sendEmail(String toEmail, String subject, String body) throws Exception {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", EMAIL_HOST);
        props.put("mail.smtp.port", EMAIL_PORT);
        props.put("mail.smtp.ssl.trust", EMAIL_HOST);
        
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
            }
        });
        
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(EMAIL_USERNAME));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject);
        message.setText(body);
        
        Transport.send(message);
    }
}
