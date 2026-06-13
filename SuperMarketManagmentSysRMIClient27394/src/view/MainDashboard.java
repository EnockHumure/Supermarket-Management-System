package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
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
    private String sessionId; // Store session ID
    private ProductService productService;
    private CategoryService categoryService;
    private SaleService saleService;
    private UserService userService;
    private Timer inactivityTimer; // 5-minute inactivity timer
    private long lastActivityTime; // Track last user activity
    private java.util.Calendar currentWeekCalendar; // Track current week for navigation
    private javax.swing.JPanel navPanel; // Navigation panel for week buttons
    
    public MainDashboard(User user, String sessionId) {
        this.currentUser = user;
        this.sessionId = sessionId;
        this.currentWeekCalendar = java.util.Calendar.getInstance(); // Initialize to current week
        initComponents();
        connectToServer();
        loadDashboard();
        updateUIBasedOnRole();
        startInactivityMonitoring(); // Start 5-minute inactivity timer
        setupActivityTracking(); // Track user interactions
        
        // Set welcome message after init
        if (currentUser != null) {
            lblUserWelcome.setText("Welcome, " + currentUser.getFullName());
        }
        
        // Handle window close - terminate client
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                handleWindowClose();
            }
        });
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
            List<Product> outOfStockProducts = productService.findOutOfStockProducts();
            List<Product> allProducts = productService.findAllProductRecords();
            List<Sale> allSales = saleService.findAllSaleRecords();
            List<Category> allCategories = categoryService.findAllCategoryRecords();
            
            int lowStockCount = lowStockProducts.size();
            int outOfStockCount = outOfStockProducts.size();
            int inStockCount = allProducts.size() - lowStockCount - outOfStockCount;
            int totalProducts = allProducts.size();
            int totalSales = allSales.size();
            int totalCategories = allCategories.size();
            
            System.out.println("Stats - Products: " + totalProducts + ", Categories: " + totalCategories + ", Sales: " + totalSales + ", Low Stock: " + lowStockCount);
            
            // Create charts
            createBarChart(inStockCount, lowStockCount, outOfStockCount);
            createPieChart(allProducts, allCategories);
            createLineChart(allSales);
            
            System.out.println("Dashboard loaded successfully");
        } catch (Exception ex) {
            System.err.println("Error loading dashboard: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading dashboard: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    

    
    private void createBarChart(int inStock, int lowStock, int outOfStock) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(inStock, "Products", "In Stock");
        dataset.addValue(lowStock, "Products", "Low Stock");
        dataset.addValue(outOfStock, "Products", "Out of Stock");
        
        JFreeChart barChart = ChartFactory.createBarChart(
            "Stock Status Overview",
            "Status",
            "Number of Products",
            dataset,
            PlotOrientation.VERTICAL,
            false, true, false
        );
        
        CategoryPlot plot = barChart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        
        // Set fixed range from 0 to 1000
        plot.getRangeAxis().setRange(0, 1000);
        plot.getRangeAxis().setAutoRange(false);
        
        // Color coding: Green for In Stock, Orange for Low Stock, Red for Out of Stock
        plot.getRenderer().setSeriesPaint(0, new Color(46, 204, 113)); // Green
        
        // Item labels not supported in JFreeChart 1.5.x BarRenderer
        
        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(850, 550));
        chartPanel.setMouseWheelEnabled(false);
        chartPanel.setDomainZoomable(false);
        chartPanel.setRangeZoomable(false);
        
        barChartPanel.removeAll();
        barChartPanel.setLayout(new BorderLayout());
        barChartPanel.add(chartPanel, BorderLayout.CENTER);
        barChartPanel.validate();
    }
    
    private void createPieChart(List<Product> allProducts, List<Category> allCategories) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        
        if (allProducts.isEmpty() || allCategories.isEmpty()) {
            dataset.setValue("No Data", 1);
        } else {
            // Count products per category
            java.util.Map<String, Integer> categoryCount = new java.util.HashMap<>();
            
            for (Product product : allProducts) {
                if (product.getCategory() != null) {
                    String categoryName = product.getCategory().getName();
                    categoryCount.put(categoryName, categoryCount.getOrDefault(categoryName, 0) + 1);
                }
            }
            
            // Add to dataset
            for (java.util.Map.Entry<String, Integer> entry : categoryCount.entrySet()) {
                dataset.setValue(entry.getKey() + " (" + entry.getValue() + ")", entry.getValue());
            }
            
            // Add categories with 0 products
            for (Category category : allCategories) {
                if (!categoryCount.containsKey(category.getName())) {
                    dataset.setValue(category.getName() + " (0)", 0);
                }
            }
        }
        
        JFreeChart pieChart = ChartFactory.createPieChart(
            "Products by Category",
            dataset,
            true, true, false
        );
        
        PiePlot plot = (PiePlot) pieChart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setLabelGenerator(new org.jfree.chart.labels.StandardPieSectionLabelGenerator("{0}: {2}"));
        
        // Color palette for categories
        Color[] colors = {
            new Color(52, 152, 219),   // Blue
            new Color(46, 204, 113),   // Green
            new Color(155, 89, 182),   // Purple
            new Color(241, 196, 15),   // Yellow
            new Color(230, 126, 34),   // Orange
            new Color(231, 76, 60),    // Red
            new Color(26, 188, 156),   // Turquoise
            new Color(52, 73, 94)      // Dark Blue
        };
        
        int colorIndex = 0;
        for (Object key : dataset.getKeys()) {
            plot.setSectionPaint((Comparable) key, colors[colorIndex % colors.length]);
            colorIndex++;
        }
        
        ChartPanel chartPanel = new ChartPanel(pieChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(850, 550));
        chartPanel.setMouseWheelEnabled(true);
        
        pieChartPanel.removeAll();
        pieChartPanel.setLayout(new BorderLayout());
        pieChartPanel.add(chartPanel, BorderLayout.CENTER);
        pieChartPanel.validate();
    }
    
    private void createLineChart(List<Sale> allSales) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        try {
            System.out.println("\n=== CREATING SALES TREND CHART ===");
            System.out.println("Total sales: " + allSales.size());
            
            // Get week dates from currentWeekCalendar (Mon-Sun)
            java.util.Calendar cal = (java.util.Calendar) currentWeekCalendar.clone();
            int currentDayOfWeek = cal.get(java.util.Calendar.DAY_OF_WEEK);
            int daysSinceMonday = (currentDayOfWeek == java.util.Calendar.SUNDAY) ? 6 : (currentDayOfWeek - java.util.Calendar.MONDAY);
            
            // Go back to Monday
            cal.add(java.util.Calendar.DAY_OF_YEAR, -daysSinceMonday);
            cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
            cal.set(java.util.Calendar.MINUTE, 0);
            cal.set(java.util.Calendar.SECOND, 0);
            cal.set(java.util.Calendar.MILLISECOND, 0);
            
            // Get 7 days of the week
            java.util.List<java.util.Date> weekDates = new java.util.ArrayList<>();
            for (int i = 0; i < 7; i++) {
                weekDates.add(cal.getTime());
                cal.add(java.util.Calendar.DAY_OF_YEAR, 1);
            }
            
            // Count sales per day
            java.util.Map<java.util.Date, Integer> dailySales = new java.util.HashMap<>();
            for (java.util.Date date : weekDates) {
                dailySales.put(date, 0);
            }
            
            for (Sale sale : allSales) {
                java.util.Date saleDate = sale.getSaleDate();
                java.util.Calendar saleCal = java.util.Calendar.getInstance();
                saleCal.setTime(saleDate);
                saleCal.set(java.util.Calendar.HOUR_OF_DAY, 0);
                saleCal.set(java.util.Calendar.MINUTE, 0);
                saleCal.set(java.util.Calendar.SECOND, 0);
                saleCal.set(java.util.Calendar.MILLISECOND, 0);
                
                if (dailySales.containsKey(saleCal.getTime())) {
                    dailySales.put(saleCal.getTime(), dailySales.get(saleCal.getTime()) + 1);
                }
            }
            
            // Format dates and add to dataset - use simple date format for visibility
            String[] dateLabels = new String[7];
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM");
            
            for (int i = 0; i < 7; i++) {
                dateLabels[i] = sdf.format(weekDates.get(i));
                int count = dailySales.get(weekDates.get(i));
                dataset.addValue(count, "Sales", dateLabels[i]);
            }
            
            System.out.println("Weekly sales: " + dailySales);
            
        } catch (Exception ex) {
            System.err.println("Error creating sales trend chart: " + ex.getMessage());
            ex.printStackTrace();
            dataset.addValue(0, "Sales", "Error");
        }
        
        JFreeChart lineChart = ChartFactory.createLineChart(
            "Sales Trend - Weekly Product Sales",
            "Date",
            "Number of Sales",
            dataset,
            PlotOrientation.VERTICAL,
            false, true, false
        );
        
        CategoryPlot plot = lineChart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        
        // Set fixed range from 0 to 20 (0-20+)
        plot.getRangeAxis().setRange(0, 20);
        plot.getRangeAxis().setAutoRange(false);
        
        // Single blue line for sales
        Color lineColor = new Color(52, 152, 219);
        org.jfree.chart.renderer.category.LineAndShapeRenderer renderer = 
            (org.jfree.chart.renderer.category.LineAndShapeRenderer) plot.getRenderer();
        
        renderer.setSeriesPaint(0, lineColor);
        renderer.setSeriesStroke(0, new java.awt.BasicStroke(2.5f));
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShape(0, new java.awt.geom.Ellipse2D.Double(-4, -4, 8, 8));
        
        // Item labels not supported in JFreeChart 1.5.x LineAndShapeRenderer
        
        ChartPanel chartPanel = new ChartPanel(lineChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 350));
        chartPanel.setMouseWheelEnabled(true);
        
        // Fix date label visibility - show dates properly
        org.jfree.chart.axis.CategoryAxis domainAxis = lineChart.getCategoryPlot().getDomainAxis();
        domainAxis.setCategoryLabelPositions(org.jfree.chart.axis.CategoryLabelPositions.STANDARD);
        domainAxis.setTickLabelFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 10));
        domainAxis.setLowerMargin(0.05);
        domainAxis.setUpperMargin(0.05);
        domainAxis.setCategoryMargin(0.1);
        
        lineChartPanel.removeAll();
        lineChartPanel.setLayout(new java.awt.BorderLayout());
        lineChartPanel.add(chartPanel, BorderLayout.CENTER);
        lineChartPanel.add(navPanel, BorderLayout.SOUTH);
        lineChartPanel.validate();
    }
    
    private void updateUIBasedOnRole() {
        String role = currentUser.getRole().toString();
        System.out.println("=== ROLE-BASED UI UPDATE ===");
        System.out.println("User: " + currentUser.getUsername());
        System.out.println("Role: " + role);
        
        // Find the chartTabs component
        javax.swing.JTabbedPane chartTabs = null;
        for (java.awt.Component comp : mainPanel.getComponents()) {
            if (comp instanceof javax.swing.JTabbedPane) {
                chartTabs = (javax.swing.JTabbedPane) comp;
                break;
            }
        }
        
        // CASHIER: No statistics, no charts, limited buttons
        if (role.equals("CASHIER")) {
            System.out.println("Applying CASHIER restrictions...");
            // Hide charts
            if (chartTabs != null) {
                chartTabs.setVisible(false);
            }
            
            // Hide admin/manager buttons
            btnUserManagement.setVisible(false);
            btnReports.setVisible(false);
            btnProducts.setVisible(false);
            btnCategories.setVisible(false);
            btnRefresh.setVisible(false);
            
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
            if (chartTabs != null) {
                chartTabs.setVisible(true);
            }
            System.out.println("ADMIN UI: All buttons visible, User Management = true");
        } else if (role.equals("MANAGER")) {
            System.out.println("Applying MANAGER restrictions...");
            // MANAGER: No user management
            btnUserManagement.setVisible(false);
            btnReports.setVisible(true);
            btnProducts.setVisible(true);
            btnCategories.setVisible(true);
            btnProcessSale.setVisible(true);
            if (chartTabs != null) {
                chartTabs.setVisible(true);
            }
            System.out.println("MANAGER UI: User Management = false, Reports/Products/Categories = true");
        }
        System.out.println("=== UI UPDATE COMPLETE ===");
    }
    
    private void startInactivityMonitoring() {
        lastActivityTime = System.currentTimeMillis();
        
        // Check inactivity every 10 seconds
        inactivityTimer = new Timer(10000, new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                checkInactivity();
            }
        });
        inactivityTimer.start();
        System.out.println("✓ Inactivity monitoring started (5-minute timeout)");
    }
    
    private void checkInactivity() {
        long currentTime = System.currentTimeMillis();
        long inactiveTime = currentTime - lastActivityTime;
        long fiveMinutes = 5 * 60 * 1000; // 5 minutes in milliseconds
        
        if (inactiveTime >= fiveMinutes) {
            System.out.println("⚠ User inactive for 5 minutes! Auto-logout...");
            inactivityTimer.stop();
            
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JOptionPane.showMessageDialog(MainDashboard.this,
                        "You have been logged out due to 5 minutes of inactivity.\nClient will now terminate.",
                        "Session Timeout",
                        JOptionPane.WARNING_MESSAGE);
                    
                    terminateClient();
                }
            });
        }
    }
    
    private void setupActivityTracking() {
        // Track mouse movements
        java.awt.event.MouseAdapter mouseTracker = new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                resetInactivityTimer();
            }
            
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                resetInactivityTimer();
            }
        };
        
        // Track keyboard activity
        java.awt.event.KeyAdapter keyTracker = new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                resetInactivityTimer();
            }
        };
        
        // Add listeners to all components recursively
        addListenersRecursively(this.getContentPane(), mouseTracker, keyTracker);
    }
    
    private void addListenersRecursively(java.awt.Container container, 
            java.awt.event.MouseAdapter mouseTracker, 
            java.awt.event.KeyAdapter keyTracker) {
        container.addMouseListener(mouseTracker);
        container.addMouseMotionListener(mouseTracker);
        container.addKeyListener(keyTracker);
        
        for (java.awt.Component comp : container.getComponents()) {
            comp.addMouseListener(mouseTracker);
            comp.addMouseMotionListener(mouseTracker);
            comp.addKeyListener(keyTracker);
            
            if (comp instanceof java.awt.Container) {
                addListenersRecursively((java.awt.Container) comp, mouseTracker, keyTracker);
            }
        }
    }
    
    private void resetInactivityTimer() {
        lastActivityTime = System.currentTimeMillis();
    }
    
    private void handleWindowClose() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to close the application?\nThis will terminate the client.",
            "Exit Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            terminateClient();
        }
    }
    
    private void terminateClient() {
        try {
            // Stop timer
            if (inactivityTimer != null) {
                inactivityTimer.stop();
            }
            
            // Invalidate session on server
            if (userService != null && sessionId != null) {
                userService.logout(sessionId);
                System.out.println("Session invalidated: " + sessionId);
            }
        } catch (Exception ex) {
            System.err.println("Error during logout: " + ex.getMessage());
        } finally {
            System.out.println("Client terminating...");
            System.exit(0); // Terminate the client application
        }
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
        lineChartPanel = new javax.swing.JPanel();

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblUserWelcome)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnLogout)
                .addContainerGap())
        );
        headerPanelLayout.setVerticalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblTitle)
                    .addComponent(lblUserWelcome)
                    .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
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
        
        javax.swing.JButton btnRefresh = new javax.swing.JButton();
        btnRefresh.setBackground(new java.awt.Color(39, 174, 96));
        btnRefresh.setFont(new java.awt.Font("Segoe UI", 1, 14));
        btnRefresh.setForeground(new java.awt.Color(255, 255, 255));
        btnRefresh.setText("Refresh Dashboard");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetInactivityTimer();
                System.out.println("\n*** REFRESHING DASHBOARD ***");
                loadDashboard();
                JOptionPane.showMessageDialog(MainDashboard.this, 
                    "Dashboard refreshed successfully!", 
                    "Refresh Complete", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        // Create week navigation buttons for Sales Trend tab
        navPanel = new javax.swing.JPanel();
        navPanel.setBackground(new java.awt.Color(255, 255, 255));
        
        javax.swing.JButton btnPrevWeek = new javax.swing.JButton();
        btnPrevWeek.setBackground(new java.awt.Color(52, 152, 219));
        btnPrevWeek.setFont(new java.awt.Font("Segoe UI", 1, 12));
        btnPrevWeek.setForeground(new java.awt.Color(255, 255, 255));
        btnPrevWeek.setText("< Prev Week");
        btnPrevWeek.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetInactivityTimer();
                currentWeekCalendar.add(java.util.Calendar.WEEK_OF_YEAR, -1);
                System.out.println("\n*** LOADING PREVIOUS WEEK ***");
                loadDashboard();
            }
        });
        
        javax.swing.JButton btnNextWeek = new javax.swing.JButton();
        btnNextWeek.setBackground(new java.awt.Color(52, 152, 219));
        btnNextWeek.setFont(new java.awt.Font("Segoe UI", 1, 12));
        btnNextWeek.setForeground(new java.awt.Color(255, 255, 255));
        btnNextWeek.setText("Next Week >");
        btnNextWeek.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetInactivityTimer();
                currentWeekCalendar.add(java.util.Calendar.WEEK_OF_YEAR, 1);
                System.out.println("\n*** LOADING NEXT WEEK ***");
                loadDashboard();
            }
        });
        
        navPanel.add(btnPrevWeek);
        navPanel.add(btnNextWeek);

        barChartPanel.setBackground(new java.awt.Color(255, 255, 255));
        barChartPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("System Statistics Overview"));
        barChartPanel.setLayout(new java.awt.BorderLayout());

        pieChartPanel.setBackground(new java.awt.Color(255, 255, 255));
        pieChartPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Stock Status Distribution"));
        pieChartPanel.setLayout(new java.awt.BorderLayout());

        lineChartPanel.setBackground(new java.awt.Color(255, 255, 255));
        lineChartPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Sales Revenue Trend"));
        lineChartPanel.setLayout(new java.awt.BorderLayout());
        
        // Create tabbed pane for charts
        javax.swing.JTabbedPane chartTabs = new javax.swing.JTabbedPane();
        chartTabs.setFont(new java.awt.Font("Segoe UI", 1, 14));
        chartTabs.addTab("Stock Status", barChartPanel);
        chartTabs.addTab("Category Distribution", pieChartPanel);
        chartTabs.addTab("Sales Trend", lineChartPanel);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnProducts, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(btnCategories, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnProcessSale, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnUserManagement, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnReports, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnRefresh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chartTabs, javax.swing.GroupLayout.DEFAULT_SIZE, 900, Short.MAX_VALUE)
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(btnProducts, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnCategories, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnProcessSale, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnUserManagement, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnReports, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chartTabs, javax.swing.GroupLayout.DEFAULT_SIZE, 950, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to logout?\nThis will terminate the client.", 
                "Logout Confirmation", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            terminateClient();
        }
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void btnProductsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProductsActionPerformed
        resetInactivityTimer();
        // CASHIER: View-only products
        if (currentUser.getRole().toString().equals("CASHIER")) {
            new CashierProductView().setVisible(true);
        } else {
            new ProductManagementModule().setVisible(true);
        }
    }//GEN-LAST:event_btnProductsActionPerformed

    private void btnCategoriesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCategoriesActionPerformed
        resetInactivityTimer();
        // CASHIER: View-only categories
        if (currentUser.getRole().toString().equals("CASHIER")) {
            new CashierCategoryView().setVisible(true);
        } else {
            new CategoryManagementModule().setVisible(true);
        }
    }//GEN-LAST:event_btnCategoriesActionPerformed

    private void btnProcessSaleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProcessSaleActionPerformed
        resetInactivityTimer();
        new SaleProcessingModule(currentUser).setVisible(true);
    }//GEN-LAST:event_btnProcessSaleActionPerformed

    private void btnUserManagementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUserManagementActionPerformed
        resetInactivityTimer();
        new UserManagementModule().setVisible(true);
    }//GEN-LAST:event_btnUserManagementActionPerformed

    private void btnReportsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportsActionPerformed
        resetInactivityTimer();
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
                new MainDashboard(null, null).setVisible(true);
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
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnUserManagement;
    private javax.swing.JPanel dashboardPanel;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblUserWelcome;
    private javax.swing.JPanel lineChartPanel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel pieChartPanel;
    // End of variables declaration//GEN-END:variables
}
