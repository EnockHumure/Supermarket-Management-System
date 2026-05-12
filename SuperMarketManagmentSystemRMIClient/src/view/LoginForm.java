package view;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.swing.JOptionPane;
import model.User;
import service.UserService;

public class LoginForm extends javax.swing.JFrame {

    private UserService userService;
    
    public LoginForm() {
        initComponents();
        connectToServer();
    }
    
    private void connectToServer() {
        try {
            Registry registry = LocateRegistry.getRegistry("127.0.0.1", 5000);
            userService = (UserService) registry.lookup("user-service");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Cannot connect to server: " + ex.getMessage(), 
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        loginPanel = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        lblSubtitle = new javax.swing.JLabel();
        lblUsername = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        lblPassword = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        btnLogin = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Supermarket Management System - Login");

        mainPanel.setBackground(new java.awt.Color(236, 240, 245));

        loginPanel.setBackground(new java.awt.Color(255, 255, 255));
        loginPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(189, 195, 195)));

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(52, 73, 94));
        lblTitle.setText("Welcome Back!");

        lblSubtitle.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblSubtitle.setForeground(new java.awt.Color(127, 140, 141));
        lblSubtitle.setText("Please login to your account");

        lblUsername.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblUsername.setForeground(new java.awt.Color(52, 73, 94));
        lblUsername.setText("Username");

        txtUsername.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        lblPassword.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblPassword.setForeground(new java.awt.Color(52, 73, 94));
        lblPassword.setText("Password");

        txtPassword.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        btnLogin.setBackground(new java.awt.Color(52, 152, 219));
        btnLogin.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        btnLogin.setForeground(new java.awt.Color(255, 255, 255));
        btnLogin.setText("LOGIN");
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout loginPanelLayout = new javax.swing.GroupLayout(loginPanel);
        loginPanel.setLayout(loginPanelLayout);
        loginPanelLayout.setHorizontalGroup(
            loginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(loginPanelLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(loginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE)
                    .addComponent(lblSubtitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblUsername)
                    .addComponent(txtUsername)
                    .addComponent(lblPassword)
                    .addComponent(txtPassword)
                    .addComponent(btnLogin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(40, Short.MAX_VALUE))
        );
        loginPanelLayout.setVerticalGroup(
            loginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(loginPanelLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblSubtitle, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addComponent(lblUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(lblPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(btnLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(40, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(loginPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addComponent(loginPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 10, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        
        System.out.println("=== LOGIN ATTEMPT ===");
        System.out.println("Username entered: " + username);
        
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username", 
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            btnLogin.setEnabled(false);
            btnLogin.setText("LOGGING IN...");
            
            System.out.println("Calling userService.findUserByUsername()...");
            
            // First check if user exists and has password set
            User user = userService.findUserByUsername(username);
            
            System.out.println("Result from server: " + (user != null ? "User found: " + user.getUsername() : "User is NULL"));
            
            if (user == null) {
                System.err.println("ERROR: User not found in database!");
                JOptionPane.showMessageDialog(this, 
                        "User not found: " + username + "\n\nPlease check:\n" +
                        "1. Server is running\n" +
                        "2. Username is correct\n" +
                        "3. Check server console for errors", 
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check if password is set (empty password hash means first login)
            if (user.getPasswordHash() == null || user.getPasswordHash().isEmpty()) {
                // First time login - send OTP
                userService.generateOtp(username);
                
                // Show OTP dialog
                String otpInput = JOptionPane.showInputDialog(this, 
                    "Enter the 6-digit OTP sent to your registered email:", 
                    "Two-Factor Authentication", 
                    JOptionPane.QUESTION_MESSAGE);
                
                if (otpInput == null || otpInput.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "OTP verification cancelled", 
                            "Login Cancelled", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                
                // Verify OTP
                boolean otpValid = userService.verifyOtp(username, otpInput.trim());
                
                if (otpValid) {
                    // Ask user to set password
                    String newPassword = JOptionPane.showInputDialog(this, 
                        "Enter your new password:", 
                        "Set Password", 
                        JOptionPane.QUESTION_MESSAGE);
                    
                    if (newPassword != null && !newPassword.isEmpty()) {
                        // Set password
                        userService.setPassword(username, newPassword);
                        
                        JOptionPane.showMessageDialog(this, 
                            "Password set successfully! Please login with your new password.", 
                            "Password Set", 
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        // Clear form
                        txtUsername.setText("");
                        txtPassword.setText("");
                    } else {
                        JOptionPane.showMessageDialog(this, "Password cannot be empty", 
                                "Validation Error", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid OTP. Please try again.", 
                            "OTP Verification Failed", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // User has password - authenticate with password + OTP
                if (password.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter password", 
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                User authenticatedUser = userService.authenticateUserWithLockout(username, password);
                
                if (authenticatedUser != null) {
                    // Generate OTP
                    userService.generateOtp(username);
                    
                    // Show OTP dialog
                    String otpInput = JOptionPane.showInputDialog(this, 
                        "Enter the 6-digit OTP sent to your registered email:", 
                        "Two-Factor Authentication", 
                        JOptionPane.QUESTION_MESSAGE);
                    
                    if (otpInput == null || otpInput.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(this, "OTP verification cancelled", 
                                "Login Cancelled", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                    
                    // Verify OTP
                    boolean otpValid = userService.verifyOtp(username, otpInput.trim());
                    
                    if (otpValid) {
                        System.out.println("OTP verified successfully, fetching user...");
                        
                        // Fetch user again after OTP verification
                        User loggedInUser = userService.findUserByUsername(username);
                        
                        if (loggedInUser == null) {
                            throw new Exception("User not found after OTP verification");
                        }
                        
                        System.out.println("User fetched: " + loggedInUser.getUsername());
                        
                        JOptionPane.showMessageDialog(this, 
                            "Welcome, " + loggedInUser.getFullName() + "!", 
                            "Login Successful", 
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        System.out.println("Opening MainDashboard...");
                        new MainDashboard(loggedInUser).setVisible(true);
                        this.dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid OTP. Please try again.", 
                                "OTP Verification Failed", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid username or password", 
                            "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            String errorMsg = ex.getMessage();
            if (errorMsg == null || errorMsg.isEmpty()) {
                errorMsg = ex.getClass().getSimpleName() + " occurred";
            }
            JOptionPane.showMessageDialog(this, 
                "Login failed: " + errorMsg + "\n\nPlease check:\n" +
                "1. Server is running\n" +
                "2. User has email address set\n" +
                "3. Check server console for OTP code", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            btnLogin.setEnabled(true);
            btnLogin.setText("LOGIN");
        }
    }//GEN-LAST:event_btnLoginActionPerformed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(LoginForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LoginForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LoginForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LoginForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LoginForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLogin;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JPanel loginPanel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
