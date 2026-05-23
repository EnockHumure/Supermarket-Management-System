package view;

import java.io.File;
import java.io.FileWriter;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.Sale;
import model.User;
import service.ProductService;
import service.SaleService;

public class ReportsModule extends javax.swing.JFrame {

    private User currentUser;
    private SaleService saleService;
    
    public ReportsModule(User user) {
        this.currentUser = user;
        initComponents();
        connectToServer();
        
        // Set default dates to today
        String today = java.time.LocalDate.now().toString();
        txtStartDate.setText(today);
        txtEndDate.setText(today);
    }
    
    private void connectToServer() {
        try {
            saleService = (SaleService) java.rmi.registry.LocateRegistry.getRegistry("127.0.0.1", 5000).lookup("sale-service");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Cannot connect to server: " + ex.getMessage(), 
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void generateDailySalesReport() {
        try {
            String startDateStr = txtStartDate.getText().trim();
            String endDateStr = txtEndDate.getText().trim();
            
            if (startDateStr.isEmpty() || endDateStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select both start and end dates", 
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            java.sql.Date startDate = java.sql.Date.valueOf(startDateStr);
            java.sql.Date endDate = java.sql.Date.valueOf(endDateStr);
            
            System.out.println("[ReportsModule] Start Date: " + startDate);
            System.out.println("[ReportsModule] End Date: " + endDate);
            
            // Add one day to end date to include the entire end day
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(endDate);
            cal.add(java.util.Calendar.DAY_OF_MONTH, 1);
            java.sql.Date endDatePlusOne = new java.sql.Date(cal.getTimeInMillis());
            
            System.out.println("[ReportsModule] End Date + 1: " + endDatePlusOne);
            
            List<Sale> sales = saleService.findSalesByDateRange(startDate, endDatePlusOne);
            
            System.out.println("[ReportsModule] Found " + sales.size() + " sales");
            
            DefaultTableModel model = (DefaultTableModel) tblReport.getModel();
            model.setRowCount(0);
            
            model.setColumnIdentifiers(new String[]{
                "Sale ID", "Date", "Cashier", "Payment Method", "Total Amount"
            });
            
            double totalRevenue = 0;
            int totalTransactions = 0;
            int cashTransactions = 0;
            int cardTransactions = 0;
            
            for (Sale sale : sales) {
                System.out.println("[ReportsModule] Processing sale: ID=" + sale.getSaleId() + 
                    ", Date=" + sale.getSaleDate() + ", Amount=" + sale.getTotalAmount());
                
                model.addRow(new Object[]{
                    sale.getSaleId(),
                    new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(sale.getSaleDate()),
                    sale.getCashier() != null ? sale.getCashier().getFullName() : "N/A",
                    sale.getPaymentMethod(),
                    sale.getTotalAmount()
                });
                
                totalRevenue += sale.getTotalAmount().doubleValue();
                totalTransactions++;
                
                if ("Cash".equals(sale.getPaymentMethod())) {
                    cashTransactions++;
                } else if ("Card".equals(sale.getPaymentMethod())) {
                    cardTransactions++;
                }
            }
            
            // Get monthly revenue from database
            java.sql.Date currentMonthStart = java.sql.Date.valueOf(java.time.LocalDate.now().withDayOfMonth(1).toString());
            java.sql.Date today = java.sql.Date.valueOf(java.time.LocalDate.now().toString());
            cal.setTime(today);
            cal.add(java.util.Calendar.DAY_OF_MONTH, 1);
            java.sql.Date todayPlusOne = new java.sql.Date(cal.getTimeInMillis());
            
            java.math.BigDecimal monthlyRevenue = saleService.getRevenueByDateRange(currentMonthStart, todayPlusOne);
            
            lblTotalRevenue.setText(String.format("%.2f", totalRevenue));
            lblTotalTransactions.setText(String.valueOf(totalTransactions));
            lblMonthlyRevenue.setText(String.format("%.2f", monthlyRevenue.doubleValue()));
            
            if (sales.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No sales found for the selected date range\n" +
                    "Start: " + startDate + "\nEnd: " + endDate, 
                        "Information", JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error generating report: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void generateStockLevelReport() {
        try {
            Registry registry = LocateRegistry.getRegistry("127.0.0.1", 5000);
            ProductService productService = (ProductService) registry.lookup("product-service");
            
            List<model.Product> products = productService.findAllProductRecords();
            
            DefaultTableModel model = (DefaultTableModel) tblReport.getModel();
            model.setRowCount(0);
            
            model.setColumnIdentifiers(new String[]{
                "Product Name", "Barcode", "Category", "Stock Qty", "Reorder Level", "Status"
            });
            
            int okCount = 0;
            int lowStockCount = 0;
            int outOfStockCount = 0;
            
            for (model.Product product : products) {
                String status;
                int stock = product.getStockQuantity();
                int reorderLevel = product.getReorderLevel();
                
                if (stock == 0) {
                    status = "OUT OF STOCK";
                    outOfStockCount++;
                } else if (stock <= reorderLevel) {
                    status = "LOW STOCK";
                    lowStockCount++;
                } else {
                    status = "OK";
                    okCount++;
                }
                
                model.addRow(new Object[]{
                    product.getName(),
                    product.getBarcode(),
                    product.getCategory() != null ? product.getCategory().getName() : "N/A",
                    stock,
                    reorderLevel,
                    status
                });
            }
            
            lblTotalRevenue.setText("OK: " + okCount + " | LOW: " + lowStockCount + " | OUT: " + outOfStockCount);
            lblTotalTransactions.setText(String.valueOf(products.size()));
            
            if (products.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No products found", 
                        "Information", JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error generating stock report: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        headerPanel = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        lblUser = new javax.swing.JLabel();
        btnBack = new javax.swing.JButton();
        dateRangePanel = new javax.swing.JPanel();
        lblStartDate = new javax.swing.JLabel();
        txtStartDate = new javax.swing.JTextField();
        lblEndDate = new javax.swing.JLabel();
        txtEndDate = new javax.swing.JTextField();
        btnGenerate = new javax.swing.JButton();
        btnGenerateStock = new javax.swing.JButton();
        btnToday = new javax.swing.JButton();
        btnMonthly = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblReport = new javax.swing.JTable();
        reportPanel = new javax.swing.JPanel();
        summaryPanel = new javax.swing.JPanel();
        lblTotalRevenueLabel = new javax.swing.JLabel();
        lblTotalRevenue = new javax.swing.JLabel();
        lblTotalTransactionsLabel = new javax.swing.JLabel();
        lblTotalTransactions = new javax.swing.JLabel();
        exportPanel = new javax.swing.JPanel();
        btnExportCSV = new javax.swing.JButton();
        btnExportPDF = new javax.swing.JButton();
        btnExportExcel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Reports");

        mainPanel.setBackground(new java.awt.Color(236, 240, 245));

        headerPanel.setBackground(new java.awt.Color(52, 152, 219));

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setText("Reports");

        lblUser.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblUser.setForeground(new java.awt.Color(255, 255, 255));
        if (currentUser != null) {
            lblUser.setText("User: " + currentUser.getFullName());
        } else {
            lblUser.setText("User: Guest");
        }

        btnBack.setBackground(new java.awt.Color(231, 76, 60));
        btnBack.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnBack.setForeground(new java.awt.Color(255, 255, 255));
        btnBack.setText("BACK");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout headerPanelLayout = new javax.swing.GroupLayout(headerPanel);
        headerPanel.setLayout(headerPanelLayout);
        headerPanelLayout.setHorizontalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblUser)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnBack)
                .addContainerGap())
        );
        headerPanelLayout.setVerticalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTitle)
                    .addComponent(lblUser)
                    .addComponent(btnBack))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        dateRangePanel.setBackground(new java.awt.Color(255, 255, 255));
        dateRangePanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(189, 195, 195)));

        lblStartDate.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblStartDate.setText("Start Date:");

        txtStartDate.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        lblEndDate.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblEndDate.setText("End Date:");

        txtEndDate.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        btnToday.setBackground(new java.awt.Color(46, 204, 113));
        btnToday.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnToday.setForeground(new java.awt.Color(255, 255, 255));
        btnToday.setText("Today");
        btnToday.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTodayActionPerformed(evt);
            }
        });

        btnMonthly.setBackground(new java.awt.Color(241, 196, 15));
        btnMonthly.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnMonthly.setForeground(new java.awt.Color(255, 255, 255));
        btnMonthly.setText("Monthly");
        btnMonthly.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMonthlyActionPerformed(evt);
            }
        });

        btnGenerate.setBackground(new java.awt.Color(52, 152, 219));
        btnGenerate.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnGenerate.setForeground(new java.awt.Color(255, 255, 255));
        btnGenerate.setText("Generate Sales in Range");
        btnGenerate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerateActionPerformed(evt);
            }
        });

        btnGenerateStock.setBackground(new java.awt.Color(155, 89, 182));
        btnGenerateStock.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnGenerateStock.setForeground(new java.awt.Color(255, 255, 255));
        btnGenerateStock.setText("Generate Stock Level");
        btnGenerateStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerateStockActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout dateRangePanelLayout = new javax.swing.GroupLayout(dateRangePanel);
        dateRangePanel.setLayout(dateRangePanelLayout);
        dateRangePanelLayout.setHorizontalGroup(
            dateRangePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dateRangePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblStartDate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtStartDate, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblEndDate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnToday, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnMonthly, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnGenerate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnGenerateStock, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        dateRangePanelLayout.setVerticalGroup(
            dateRangePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dateRangePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(dateRangePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblStartDate)
                    .addComponent(txtStartDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblEndDate)
                    .addComponent(txtEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnToday)
                    .addComponent(btnMonthly)
                    .addComponent(btnGenerate)
                    .addComponent(btnGenerateStock))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        reportPanel.setBackground(new java.awt.Color(255, 255, 255));
        reportPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(189, 195, 195)));

        tblReport.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        tblReport.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4", "Title 5", "Title 6"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblReport);

        javax.swing.GroupLayout reportPanelLayout = new javax.swing.GroupLayout(reportPanel);
        reportPanel.setLayout(reportPanelLayout);
        reportPanelLayout.setHorizontalGroup(
            reportPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(reportPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
                .addContainerGap())
        );
        reportPanelLayout.setVerticalGroup(
            reportPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(reportPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                .addContainerGap())
        );

        summaryPanel.setBackground(new java.awt.Color(255, 255, 255));
        summaryPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(189, 195, 195)));

        lblTotalRevenueLabel.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTotalRevenueLabel.setText("Total Revenue:");

        lblTotalRevenue.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTotalRevenue.setText("0.00");

        lblTotalTransactionsLabel.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTotalTransactionsLabel.setText("Total Transactions:");

        lblTotalTransactions.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTotalTransactions.setText("0");

        lblMonthlyRevenueLabel = new javax.swing.JLabel();
        lblMonthlyRevenueLabel.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblMonthlyRevenueLabel.setText("Monthly Revenue:");

        lblMonthlyRevenue = new javax.swing.JLabel();
        lblMonthlyRevenue.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblMonthlyRevenue.setText("0.00");

        javax.swing.GroupLayout summaryPanelLayout = new javax.swing.GroupLayout(summaryPanel);
        summaryPanel.setLayout(summaryPanelLayout);
        summaryPanelLayout.setHorizontalGroup(
            summaryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(summaryPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(summaryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTotalRevenueLabel)
                    .addComponent(lblMonthlyRevenueLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(summaryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTotalRevenue)
                    .addComponent(lblMonthlyRevenue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblTotalTransactionsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTotalTransactions)
                .addContainerGap())
        );
        summaryPanelLayout.setVerticalGroup(
            summaryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(summaryPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(summaryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotalRevenueLabel)
                    .addComponent(lblTotalRevenue)
                    .addComponent(lblTotalTransactionsLabel)
                    .addComponent(lblTotalTransactions))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(summaryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMonthlyRevenueLabel)
                    .addComponent(lblMonthlyRevenue))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        exportPanel.setBackground(new java.awt.Color(255, 255, 255));
        exportPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(189, 195, 195)));

        btnExportCSV.setBackground(new java.awt.Color(46, 204, 113));
        btnExportCSV.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnExportCSV.setForeground(new java.awt.Color(255, 255, 255));
        btnExportCSV.setText("Export CSV");
        btnExportCSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportCSVActionPerformed(evt);
            }
        });

        btnExportPDF.setBackground(new java.awt.Color(52, 152, 219));
        btnExportPDF.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnExportPDF.setForeground(new java.awt.Color(255, 255, 255));
        btnExportPDF.setText("Export PDF");
        btnExportPDF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportPDFActionPerformed(evt);
            }
        });

        btnExportExcel.setBackground(new java.awt.Color(155, 89, 182));
        btnExportExcel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnExportExcel.setForeground(new java.awt.Color(255, 255, 255));
        btnExportExcel.setText("Export Excel");
        btnExportExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportExcelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout exportPanelLayout = new javax.swing.GroupLayout(exportPanel);
        exportPanel.setLayout(exportPanelLayout);
        exportPanelLayout.setHorizontalGroup(
            exportPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(exportPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnExportCSV, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnExportPDF, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnExportExcel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        exportPanelLayout.setVerticalGroup(
            exportPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(exportPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(exportPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnExportCSV)
                    .addComponent(btnExportPDF)
                    .addComponent(btnExportExcel))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(dateRangePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(reportPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(summaryPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(exportPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dateRangePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(reportPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(summaryPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(exportPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnGenerateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerateActionPerformed
        generateDailySalesReport();
    }//GEN-LAST:event_btnGenerateActionPerformed

    private void btnGenerateStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerateStockActionPerformed
        generateStockLevelReport();
    }//GEN-LAST:event_btnGenerateStockActionPerformed

    private void btnTodayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTodayActionPerformed
        java.time.LocalDate today = java.time.LocalDate.now();
        String todayStr = today.toString();
        
        System.out.println("[Today Button] Current date: " + today);
        System.out.println("[Today Button] Today string: " + todayStr);
        
        txtStartDate.setText(todayStr);
        txtEndDate.setText(todayStr);
        
        // Auto-generate report
        generateDailySalesReport();
    }//GEN-LAST:event_btnTodayActionPerformed

    private void btnMonthlyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMonthlyActionPerformed
        java.time.LocalDate now = java.time.LocalDate.now();
        java.time.LocalDate startOfMonth = now.withDayOfMonth(1);
        
        txtStartDate.setText(startOfMonth.toString());
        txtEndDate.setText(now.toString());
        generateDailySalesReport();
    }//GEN-LAST:event_btnMonthlyActionPerformed

    private void btnExportCSVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportCSVActionPerformed
        try {
            javax.swing.JTable table = tblReport;
            javax.swing.table.TableModel model = table.getModel();
            
            String filePath = "C:\\Users\\humur\\Documents\\NetBeansProjects\\SuperMarketManagementSystem\\reports\\sales_report_" + 
                java.time.LocalDate.now() + ".csv";
            java.io.File file = new java.io.File(filePath);
            
            file.getParentFile().mkdirs();
            
            try (java.io.FileWriter fw = new java.io.FileWriter(file);
                 java.io.BufferedWriter bw = new java.io.BufferedWriter(fw)) {
                
                for (int i = 0; i < model.getColumnCount(); i++) {
                    bw.write(model.getColumnName(i));
                    if (i < model.getColumnCount() - 1) {
                        bw.write(",");
                    }
                }
                bw.write("\n");
                
                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        Object value = model.getValueAt(i, j);
                        if (value != null) {
                            bw.write(value.toString());
                        }
                        if (j < model.getColumnCount() - 1) {
                            bw.write(",");
                        }
                    }
                    bw.write("\n");
                }
                
                bw.flush();
                JOptionPane.showMessageDialog(this, "CSV exported successfully to:\n" + file.getAbsolutePath(), 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error exporting CSV: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnExportCSVActionPerformed

    private void btnExportPDFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportPDFActionPerformed
        com.itextpdf.text.Document document = null;
        try {
            javax.swing.JTable table = tblReport;
            javax.swing.table.TableModel model = table.getModel();
            
            String filePath = "C:\\Users\\humur\\Documents\\NetBeansProjects\\SuperMarketManagementSystem\\reports\\sales_report_" + 
                java.time.LocalDate.now() + ".pdf";
            java.io.File file = new java.io.File(filePath);
            
            file.getParentFile().mkdirs();
            
            document = new com.itextpdf.text.Document();
            java.io.FileOutputStream fos = new java.io.FileOutputStream(filePath);
            
            com.itextpdf.text.pdf.PdfWriter.getInstance(document, fos);
            document.open();
            
            com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 16, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Paragraph title = new com.itextpdf.text.Paragraph("Sales Report", titleFont);
            title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);
            
            com.itextpdf.text.pdf.PdfPTable pdfTable = new com.itextpdf.text.pdf.PdfPTable(model.getColumnCount());
            
            for (int i = 0; i < model.getColumnCount(); i++) {
                com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(model.getColumnName(i), titleFont));
                pdfTable.addCell(cell);
            }
            pdfTable.setHeaderRows(1);
            
            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    Object value = model.getValueAt(i, j);
                    pdfTable.addCell(value != null ? value.toString() : "");
                }
            }
            
            document.add(pdfTable);
            document.close();
            fos.close();
            
            JOptionPane.showMessageDialog(this, "PDF exported successfully to:\n" + file.getAbsolutePath(), 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error exporting PDF: " + ex.getMessage() + "\n\nMake sure iText 5 library is in your lib folder.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (document != null && document.isOpen()) {
                document.close();
            }
        }
    }//GEN-LAST:event_btnExportPDFActionPerformed

    private void btnExportExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportExcelActionPerformed
        try {
            javax.swing.JTable table = tblReport;
            javax.swing.table.TableModel model = table.getModel();
            
            String filePath = "C:\\Users\\humur\\Documents\\NetBeansProjects\\SuperMarketManagementSystem\\reports\\sales_report_" + 
                java.time.LocalDate.now() + ".xls";
            
            java.io.File file = new java.io.File(filePath);
            file.getParentFile().mkdirs();
            
            try (org.apache.poi.hssf.usermodel.HSSFWorkbook workbook = new org.apache.poi.hssf.usermodel.HSSFWorkbook();
                 java.io.FileOutputStream fileOut = new java.io.FileOutputStream(filePath)) {
                
                org.apache.poi.hssf.usermodel.HSSFSheet sheet = workbook.createSheet("Sales Report");
                
                org.apache.poi.hssf.usermodel.HSSFRow header = sheet.createRow(0);
                for (int i = 0; i < model.getColumnCount(); i++) {
                    header.createCell(i).setCellValue(model.getColumnName(i));
                }
                
                for (int i = 0; i < model.getRowCount(); i++) {
                    org.apache.poi.hssf.usermodel.HSSFRow row = sheet.createRow(i + 1);
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        Object value = model.getValueAt(i, j);
                        row.createCell(j).setCellValue(value != null ? value.toString() : "");
                    }
                }
                
                for (int i = 0; i < model.getColumnCount(); i++) {
                    sheet.autoSizeColumn(i);
                }
                
                workbook.write(fileOut);
            }
            
            JOptionPane.showMessageDialog(this, "Excel exported successfully to:\n" + filePath, 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error exporting Excel: " + ex.getMessage() + "\n\nMake sure Apache POI library is in your lib folder.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnExportExcelActionPerformed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ReportsModule.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ReportsModule.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ReportsModule.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ReportsModule.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ReportsModule(null).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnExportCSV;
    private javax.swing.JButton btnExportExcel;
    private javax.swing.JButton btnExportPDF;
    private javax.swing.JButton btnGenerate;
    private javax.swing.JButton btnGenerateStock;
    private javax.swing.JPanel dateRangePanel;
    private javax.swing.JPanel exportPanel;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblEndDate;
    private javax.swing.JLabel lblStartDate;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblMonthlyRevenue;
    private javax.swing.JLabel lblMonthlyRevenueLabel;
    private javax.swing.JLabel lblTotalRevenue;
    private javax.swing.JLabel lblTotalRevenueLabel;
    private javax.swing.JLabel lblTotalTransactions;
    private javax.swing.JLabel lblTotalTransactionsLabel;
    private javax.swing.JLabel lblUser;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel reportPanel;
    private javax.swing.JPanel summaryPanel;
    private javax.swing.JTable tblReport;
    private javax.swing.JTextField txtEndDate;
    private javax.swing.JTextField txtStartDate;
    private javax.swing.JButton btnToday;
    private javax.swing.JButton btnMonthly;
    // End of variables declaration//GEN-END:variables
}