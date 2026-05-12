package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import model.Category;
import model.Product;
import model.Sale;
import model.User;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import service.CategoryService;
import service.ProductService;
import service.SaleService;
import service.UserService;

public class MainDashboard extends javax.swing.JFrame {

    private User currentUser;
    private ProductService productService;
    private CategoryService categoryService;
    private SaleService saleService;
    private UserService userService;
    
    public MainDashboard(User user) {
        this.currentUser = user;
        initComponents();
        connectToServer();
        loadDashboard();
        updateUIBasedOnRole();
        
        // Set welcome message after init
        if (currentUser != null) {
            lblUserWelcome.setText("Welcome, " + currentUser.getFullName());
        }
    }
    
    private void connectToServer() {
        try {
            Registry registry = LocateRegistry.getRegistry("127.0.0.1", 5000);
            productService = (ProductService) registry.lookup("product-service");
            categoryService = (CategoryService) registry.lookup("category-service");
            saleService = (SaleService) registry.lookup("sale-service");
            userService = (UserService) registry.lookup("user-service");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Cannot connect to server: " + ex.getMessage(), 
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadDashboard() {
        // CASHIER doesn't see statistics
        if (currentUser.getRole().toString().equals("CASHIER")) {
            System.out.println("Cashier logged in - skipping statistics");
            return;
        }
        
        try {
            System.out.println("Loading dashboard statistics...");
            
            // Load statistics
            List<Product> lowStockProducts = productService.findLowStockProducts();
            List<Product> allProducts = productService.findAllProductRecords();
            List<Sale> allSales = saleService.findAllSaleRecords();
            List<Category> allCategories = categoryService.findAllCategoryRecords();
            
            int lowStockCount = lowStockProducts.size();
            int totalProducts = allProducts.size();
            int totalSales = allSales.size();
            int totalCategories = allCategories.size();
            
            System.out.println("Stats - Products: " + totalProducts + ", Categories: " + totalCategories + ", Sales: " + totalSales + ", Low Stock: " + lowStockCount);
            
            // Create charts
            createBarChart(totalProducts, totalCategories, totalSales, lowStockCount);
            createPieChart(totalProducts, lowStockCount);
            
            System.out.println("Dashboard loaded successfully");
        } catch (Exception ex) {
            System.err.println("Error loading dashboard: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading dashboard: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void createBarChart(int products, int categories, int sales, int lowStock) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(products, "Count", "Total Products");
        dataset.addValue(categories, "Count", "Categories");
        dataset.addValue(sales, "Count", "Total Sales");
        dataset.addValue(lowStock, "Count", "Low Stock Items");
        
        JFreeChart barChart = ChartFactory.createBarChart(
            "System Statistics Overview",
            "Metrics",
            "Count",
            dataset,
            PlotOrientation.VERTICAL,
            false, true, false
        );
        
        CategoryPlot plot = barChart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        plot.getRenderer().setSeriesPaint(0, new Color(52, 152, 219));
        
        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(400, 300));
        
        barChartPanel.removeAll();
        barChartPanel.setLayout(new BorderLayout());
        barChartPanel.add(chartPanel, BorderLayout.CENTER);
        barChartPanel.validate();
    }
    
    private void createPieChart(int totalProducts, int lowStock) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        int normalStock = totalProducts - lowStock;
        
        if (totalProducts == 0) {
            dataset.setValue("No Products", 1);
        } else {
            dataset.setValue("Normal Stock (" + normalStock + ")", normalStock);
            dataset.setValue("Low Stock (" + lowStock + ")", lowStock);
        }
        
        JFreeChart pieChart = ChartFactory.createPieChart(
            "Stock Status Distribution",
            dataset,
            true, true, false
        );
        
        PiePlot plot = (PiePlot) pieChart.getPlot();
        plot.setSectionPaint("Normal Stock (" + normalStock + ")", new Color(46, 204, 113));
        plot.setSectionPaint("Low Stock (" + lowStock + ")", new Color(231, 76, 60));
        plot.setSectionPaint("No Products", Color.LIGHT_GRAY);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setLabelGenerator(new org.jfree.chart.labels.StandardPieSectionLabelGenerator("{0}: {1} ({2})"));
        
        ChartPanel chartPanel = new ChartPanel(pieChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(400, 300));
        
        pieChartPanel.removeAll();
        pieChartPanel.setLayout(new BorderLayout());
        pieChartPanel.add(chartPanel, BorderLayout.CENTER);
        pieChartPanel.validate();
    }
    
    private void updateUIBasedOnRole() {
        String role = currentUser.getRole().toString();
        System.out.println("=== ROLE-BASED UI UPDATE ===");
        System.out.println("User: " + currentUser.getUsername());
        System.out.println("Role: " + role);
        
        // CASHIER: No statistics, no charts, limited buttons
        if (role.equals("CASHIER")) {
            System.out.println("Applying CASHIER restrictions...");
            // Hide charts
            barChartPanel.setVisible(false);
            pieChartPanel.setVisible(false);
            
            // Hide admin/manager buttons
            btnUserManagement.setVisible(false);
            btnReports.setVisible(false);
            btnProducts.setVisible(false);
            btnCategories.setVisible(false);
            
            // Only show Process Sale button
            btnProcessSale.setVisible(true);
            
            // Change title
            lblTitle.setText("Cashier Point of Sale");
            System.out.println("CASHIER UI: Only Process Sale visible");
            return;
        }
        
        // ADMIN: Full access
        if (role.equals("ADMIN")) {
            System.out.println("Applying ADMIN full access...");
            btnUserManagement.setVisible(true);
            btnReports.setVisible(true);
            btnProducts.setVisible(true);
            btnCategories.setVisible(true);
            btnProcessSale.setVisible(true);
            barChartPanel.setVisible(true);
            pieChartPanel.setVisible(true);
            System.out.println("ADMIN UI: All buttons visible, User Management = true");
        } else if (role.equals("MANAGER")) {
            System.out.println("Applying MANAGER restrictions...");
            // MANAGER: No user management
            btnUserManagement.setVisible(false);
            btnReports.setVisible(true);
            btnProducts.setVisible(true);
            btnCategories.setVisible(true);
            btnProcessSale.setVisible(true);
            barChartPanel.setVisible(true);
            pieChartPanel.setVisible(true);
            System.out.println("MANAGER UI: User Management = false, Reports/Products/Categories = true");
        }
        System.out.println("=== UI UPDATE COMPLETE ===");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        headerPanel = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        lblUserWelcome = new javax.swing.JLabel();
        btnLogout = new javax.swing.JButton();
        dashboardPanel = new javax.swing.JPanel();
        btnProducts = new javax.swing.JButton();
        btnCategories = new javax.swing.JButton();
        btnProcessSale = new javax.swing.JButton();
        btnUserManagement = new javax.swing.JButton();
        btnReports = new javax.swing.JButton();
        barChartPanel = new javax.swing.JPanel();
        pieChartPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Supermarket Management System - Dashboard");

        mainPanel.setBackground(new java.awt.Color(236, 240, 245));

        headerPanel.setBackground(new java.awt.Color(52, 152, 219));

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setText("Supermarket Management System");

        lblUserWelcome.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblUserWelcome.setForeground(new java.awt.Color(255, 255, 255));
        lblUserWelcome.setText("Welcome, ");

        btnLogout.setBackground(new java.awt.Color(231, 76, 60));
        btnLogout.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLogout.setForeground(new java.awt.Color(255, 255, 255));
        btnLogout.setText("LOGOUT");
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout headerPanelLayout = new javax.swing.GroupLayout(headerPanel);
        headerPanel.setLayout(headerPanelLayout);
        headerPanelLayout.setHorizontalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 620, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 203, Short.MAX_VALUE)
                .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, headerPanelLayout.createSequentialGroup()
                        .addComponent(lblUserWelcome)
                        .addGap(109, 109, 109))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, headerPanelLayout.createSequentialGroup()
                        .addComponent(btnLogout)
                        .addContainerGap())))
        );
        headerPanelLayout.setVerticalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTitle)
                    .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblUserWelcome)
                .addContainerGap())
        );

        dashboardPanel.setBackground(new java.awt.Color(255, 255, 255));
        dashboardPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(189, 195, 195)));

        javax.swing.GroupLayout dashboardPanelLayout = new javax.swing.GroupLayout(dashboardPanel);
        dashboardPanel.setLayout(dashboardPanelLayout);
        dashboardPanelLayout.setHorizontalGroup(
            dashboardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 523, Short.MAX_VALUE)
        );
        dashboardPanelLayout.setVerticalGroup(
            dashboardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 569, Short.MAX_VALUE)
        );

        btnProducts.setBackground(new java.awt.Color(52, 152, 219));
        btnProducts.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnProducts.setForeground(new java.awt.Color(255, 255, 255));
        btnProducts.setText("Products");
        btnProducts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProductsActionPerformed(evt);
            }
        });

        btnCategories.setBackground(new java.awt.Color(46, 204, 103));
        btnCategories.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnCategories.setForeground(new java.awt.Color(255, 255, 255));
        btnCategories.setText("Categories");
        btnCategories.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCategoriesActionPerformed(evt);
            }
        });

        btnProcessSale.setBackground(new java.awt.Color(241, 196, 244));
        btnProcessSale.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnProcessSale.setForeground(new java.awt.Color(255, 255, 255));
        btnProcessSale.setText("Process Sale");
        btnProcessSale.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProcessSaleActionPerformed(evt);
            }
        });

        btnUserManagement.setBackground(new java.awt.Color(150, 89, 182));
        btnUserManagement.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnUserManagement.setForeground(new java.awt.Color(255, 255, 255));
        btnUserManagement.setText("User Management");
        btnUserManagement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUserManagementActionPerformed(evt);
            }
        });

        btnReports.setBackground(new java.awt.Color(149, 165, 165));
        btnReports.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnReports.setForeground(new java.awt.Color(255, 255, 255));
        btnReports.setText("Reports");
        btnReports.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReportsActionPerformed(evt);
            }
        });

        barChartPanel.setBackground(new java.awt.Color(255, 255, 255));
        barChartPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("System Statistics Overview"));
        barChartPanel.setLayout(new java.awt.BorderLayout());

        pieChartPanel.setBackground(new java.awt.Color(255, 255, 255));
        pieChartPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Stock Status Distribution"));
        pieChartPanel.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(btnReports, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(btnProcessSale, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnProducts, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnCategories, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnUserManagement, javax.swing.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(barChartPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(51, 51, 51)
                        .addComponent(pieChartPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 377, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(122, 122, 122))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(dashboardPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addComponent(dashboardPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(24, 24, 24))
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnProducts, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnCategories, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(58, 58, 58)
                                .addComponent(btnProcessSale, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(47, 47, 47)
                                .addComponent(btnUserManagement, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGap(57, 57, 57)
                                .addComponent(barChartPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(15, 15, 15)
                        .addComponent(btnReports, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGap(57, 57, 57)
                        .addComponent(pieChartPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", 
                "Logout Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            new LoginForm().setVisible(true);
            this.dispose();
        }
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void btnProductsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProductsActionPerformed
        // CASHIER: View-only products
        if (currentUser.getRole().toString().equals("CASHIER")) {
            new CashierProductView().setVisible(true);
        } else {
            new ProductManagementModule().setVisible(true);
        }
    }//GEN-LAST:event_btnProductsActionPerformed

    private void btnCategoriesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCategoriesActionPerformed
        // CASHIER: View-only categories
        if (currentUser.getRole().toString().equals("CASHIER")) {
            new CashierCategoryView().setVisible(true);
        } else {
            new CategoryManagementModule().setVisible(true);
        }
    }//GEN-LAST:event_btnCategoriesActionPerformed

    private void btnProcessSaleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProcessSaleActionPerformed
        new SaleProcessingModule(currentUser).setVisible(true);
    }//GEN-LAST:event_btnProcessSaleActionPerformed

    private void btnUserManagementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUserManagementActionPerformed
        new UserManagementModule().setVisible(true);
    }//GEN-LAST:event_btnUserManagementActionPerformed

    private void btnReportsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportsActionPerformed
        new ReportsModule(currentUser).setVisible(true);
    }//GEN-LAST:event_btnReportsActionPerformed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainDashboard(null).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel barChartPanel;
    private javax.swing.JButton btnCategories;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnProcessSale;
    private javax.swing.JButton btnProducts;
    private javax.swing.JButton btnReports;
    private javax.swing.JButton btnUserManagement;
    private javax.swing.JPanel dashboardPanel;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblUserWelcome;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel pieChartPanel;
    // End of variables declaration//GEN-END:variables
}
