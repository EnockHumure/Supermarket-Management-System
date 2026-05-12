package util;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailOTPUtil {
    
    // Email configuration - CHANGE THESE TO YOUR EMAIL
    private static final String EMAIL_USERNAME = "humureenock@gmail.com";
    private static final String EMAIL_PASSWORD = "axak qnao yiea tddy";
    private static final String EMAIL_HOST = "smtp.gmail.com";
    private static final int EMAIL_PORT = 587;
    
    public static String generateAndSendOTP(String email, String username) throws Exception {
        // Generate 6-digit OTP
        int otp = (int)(Math.random() * 900000) + 100000;
        String otpCode = String.valueOf(otp);
        
        // Email content
        String subject = "Your Supermarket Management System OTP";
        String body = "Dear " + username + ",\n\n" +
                     "Your OTP for login is: " + otpCode + "\n\n" +
                     "This OTP is valid for 5 minutes.\n" +
                     "Do not share this code with anyone.\n\n" +
                     "If you didn't request this, please ignore this email.\n\n" +
                     "Best regards,\nSupermarket Management System";
        
        // Send email
        sendEmail(email, subject, body);
        
        return otpCode;
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
