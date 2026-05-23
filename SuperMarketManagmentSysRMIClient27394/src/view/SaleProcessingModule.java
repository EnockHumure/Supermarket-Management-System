package view;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.Product;
import model.Sale;
import model.SaleItem;
import model.User;
import service.ProductService;
import service.SaleService;
import util.ReceiptGenerator;
import java.awt.Desktop;
import java.io.File;

public class SaleProcessingModule extends javax.swing.JFrame {

    private User currentUser;
    private ProductService productService;
    private SaleService saleService;
    private List<Product> cartProducts = new ArrayList<>();
    private List<Integer> cartQuantities = new ArrayList<>();
    
    public SaleProcessingModule(User user) {
        this.currentUser = user;
        initComponents();
        connectToServer();
        loadAllProducts();
    }
    
    private void connectToServer() {
        try {
            Registry registry = LocateRegistry.getRegistry("127.0.0.1", 5000);
            productService = (ProductService) registry.lookup("product-service");
            saleService = (SaleService) registry.lookup("sale-service");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Cannot connect to server: " + ex.getMessage(), 
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadAllProducts() {
        try {
            List<Product> products = productService.findAllProductRecords();
            DefaultTableModel model = (DefaultTableModel) tblProducts.getModel();
            model.setRowCount(0);
            
            for (Product product : products) {
                if (product.getStockQuantity() > 0) {
                    model.addRow(new Object[]{
                        product.getProductId(),
                        product.getName(),
                        product.getBarcode(),
                        product.getPrice(),
                        product.getStockQuantity()
                    });
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading products: " + ex.getMessage(), 
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
        searchPanel = new javax.swing.JPanel();
        txtSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        productsPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblProducts = new javax.swing.JTable();
        addBtn = new javax.swing.JButton();
        cartPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblCart = new javax.swing.JTable();
        removeBtn = new javax.swing.JButton();
        lblTotal = new javax.swing.JLabel();
        paymentPanel = new javax.swing.JPanel();
        lblPaymentMethod = new javax.swing.JLabel();
        cmbPaymentMethod = new javax.swing.JComboBox<>();
        btnProcessSale = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Process Sale");

        mainPanel.setBackground(new java.awt.Color(236, 240, 245));

        headerPanel.setBackground(new java.awt.Color(52, 152, 219));

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 20));
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setText("Process Sale");

        lblUser.setFont(new java.awt.Font("Segoe UI", 0, 14));
        lblUser.setForeground(new java.awt.Color(255, 255, 255));
        lblUser.setText("Cashier: " + currentUser.getFullName());

        btnBack.setBackground(new java.awt.Color(231, 76, 60));
        btnBack.setFont(new java.awt.Font("Segoe UI", 1, 12));
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

        searchPanel.setBackground(new java.awt.Color(255, 255, 255));
        searchPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(189, 195, 195)));

        txtSearch.setFont(new java.awt.Font("Segoe UI", 0, 14));

        btnSearch.setBackground(new java.awt.Color(52, 152, 219));
        btnSearch.setFont(new java.awt.Font("Segoe UI", 1, 12));
        btnSearch.setForeground(new java.awt.Color(255, 255, 255));
        btnSearch.setText("SEARCH");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout searchPanelLayout = new javax.swing.GroupLayout(searchPanel);
        searchPanel.setLayout(searchPanelLayout);
        searchPanelLayout.setHorizontalGroup(
            searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSearch, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                .addContainerGap())
        );
        searchPanelLayout.setVerticalGroup(
            searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearch))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        productsPanel.setBackground(new java.awt.Color(255, 255, 255));
        productsPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(189, 195, 195)));

        tblProducts.setFont(new java.awt.Font("Segoe UI", 0, 12));
        tblProducts.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Name", "Barcode", "Price", "Stock"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblProducts.setRowHeight(25);
        jScrollPane1.setViewportView(tblProducts);

        addBtn.setBackground(new java.awt.Color(46, 204, 113));
        addBtn.setFont(new java.awt.Font("Segoe UI", 1, 12));
        addBtn.setForeground(new java.awt.Color(255, 255, 255));
        addBtn.setText("ADD TO CART");
        addBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout productsPanelLayout = new javax.swing.GroupLayout(productsPanel);
        productsPanel.setLayout(productsPanelLayout);
        productsPanelLayout.setHorizontalGroup(
            productsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(productsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(productsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                    .addComponent(addBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        productsPanelLayout.setVerticalGroup(
            productsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(productsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addBtn)
                .addContainerGap())
        );

        cartPanel.setBackground(new java.awt.Color(255, 255, 255));
        cartPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(189, 195, 195)));

        tblCart.setFont(new java.awt.Font("Segoe UI", 0, 12));
        tblCart.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Product", "Price", "Quantity", "Subtotal"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblCart.setRowHeight(25);
        jScrollPane2.setViewportView(tblCart);

        removeBtn.setBackground(new java.awt.Color(231, 76, 60));
        removeBtn.setFont(new java.awt.Font("Segoe UI", 1, 12));
        removeBtn.setForeground(new java.awt.Color(255, 255, 255));
        removeBtn.setText("REMOVE");
        removeBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout cartPanelLayout = new javax.swing.GroupLayout(cartPanel);
        cartPanel.setLayout(cartPanelLayout);
        cartPanelLayout.setHorizontalGroup(
            cartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cartPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(cartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                    .addComponent(removeBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        cartPanelLayout.setVerticalGroup(
            cartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cartPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(removeBtn)
                .addContainerGap())
        );

        lblTotal.setFont(new java.awt.Font("Segoe UI", 1, 20));
        lblTotal.setText("0.00");

        paymentPanel.setBackground(new java.awt.Color(255, 255, 255));
        paymentPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(189, 195, 195)));

        lblPaymentMethod.setFont(new java.awt.Font("Segoe UI", 1, 14));
        lblPaymentMethod.setText("Payment Method:");

        cmbPaymentMethod.setFont(new java.awt.Font("Segoe UI", 0, 14));
        cmbPaymentMethod.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Cash", "Card" }));

        javax.swing.GroupLayout paymentPanelLayout = new javax.swing.GroupLayout(paymentPanel);
        paymentPanel.setLayout(paymentPanelLayout);
        paymentPanelLayout.setHorizontalGroup(
            paymentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paymentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblPaymentMethod)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbPaymentMethod, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        paymentPanelLayout.setVerticalGroup(
            paymentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paymentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(paymentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPaymentMethod)
                    .addComponent(cmbPaymentMethod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        btnProcessSale.setBackground(new java.awt.Color(52, 152, 219));
        btnProcessSale.setFont(new java.awt.Font("Segoe UI", 1, 16));
        btnProcessSale.setForeground(new java.awt.Color(255, 255, 255));
        btnProcessSale.setText("PROCESS SALE");
        btnProcessSale.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProcessSaleActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(searchPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(productsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(paymentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnProcessSale, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(productsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(cartPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(paymentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnProcessSale)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        String searchTerm = txtSearch.getText().trim();
        if (searchTerm.isEmpty()) {
            loadAllProducts();
            return;
        }

        try {
            DefaultTableModel model = (DefaultTableModel) tblProducts.getModel();
            model.setRowCount(0);
            
            Product product = productService.findProductByBarcode(searchTerm);
            
            if (product != null && product.getStockQuantity() > 0) {
                model.addRow(new Object[]{
                    product.getProductId(),
                    product.getName(),
                    product.getBarcode(),
                    product.getPrice(),
                    product.getStockQuantity()
                });
            } else {
                List<Product> products = productService.searchProductsByName(searchTerm);
                
                if (products != null && !products.isEmpty()) {
                    for (Product p : products) {
                        if (p.getStockQuantity() > 0) {
                            model.addRow(new Object[]{
                                p.getProductId(),
                                p.getName(),
                                p.getBarcode(),
                                p.getPrice(),
                                p.getStockQuantity()
                            });
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "No product found matching: " + searchTerm,
                            "Search Result", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Search error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSearchActionPerformed

    private void addBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addBtnActionPerformed
        int selectedRow = tblProducts.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a product to add to cart",
                    "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Long productId = Long.parseLong(tblProducts.getValueAt(selectedRow, 0).toString());
            Product product = productService.findProductById(productId);
            
            if (product == null) {
                JOptionPane.showMessageDialog(this, "Product not found",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int quantity = Integer.parseInt(JOptionPane.showInputDialog(this,
                    "Enter quantity for: " + product.getName(),
                    "Quantity", JOptionPane.QUESTION_MESSAGE));

            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be greater than zero",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (quantity > product.getStockQuantity()) {
                JOptionPane.showMessageDialog(this, "Insufficient stock. Available: " + product.getStockQuantity(),
                        "Stock Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int existingIndex = -1;
            for (int i = 0; i < cartProducts.size(); i++) {
                if (cartProducts.get(i).getProductId().equals(productId)) {
                    existingIndex = i;
                    break;
                }
            }

            if (existingIndex >= 0) {
                int currentQty = cartQuantities.get(existingIndex);
                cartQuantities.set(existingIndex, currentQty + quantity);
            } else {
                cartProducts.add(product);
                cartQuantities.add(quantity);
            }

            loadCart();
            JOptionPane.showMessageDialog(this, "Product added to cart!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid quantity",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding to cart: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_addBtnActionPerformed

    private void removeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeBtnActionPerformed
        int selectedRow = tblCart.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a product to remove from cart",
                    "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        cartProducts.remove(selectedRow);
        cartQuantities.remove(selectedRow);
        loadCart();
    }//GEN-LAST:event_removeBtnActionPerformed

    private void loadCart() {
        DefaultTableModel model = (DefaultTableModel) tblCart.getModel();
        model.setRowCount(0);
        
        BigDecimal subtotal = BigDecimal.ZERO;
        
        for (int i = 0; i < cartProducts.size(); i++) {
            Product product = cartProducts.get(i);
            int quantity = cartQuantities.get(i);
            BigDecimal itemSubtotal = product.getPrice().multiply(BigDecimal.valueOf(quantity));
            
            model.addRow(new Object[]{
                product.getName(),
                product.getPrice(),
                quantity,
                itemSubtotal
            });
            
            subtotal = subtotal.add(itemSubtotal);
        }
        
        lblTotal.setText(String.format("%.2f", subtotal));
    }

    private void btnProcessSaleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProcessSaleActionPerformed
        if (cartProducts.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty. Add products first.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            BigDecimal subtotal = BigDecimal.ZERO;
            for (int i = 0; i < cartProducts.size(); i++) {
                Product product = cartProducts.get(i);
                int quantity = cartQuantities.get(i);
                subtotal = subtotal.add(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
            }
            
            BigDecimal discountPercent = BigDecimal.ZERO;
            if (currentUser.getRole().toString().equals("ADMIN")) {
                try {
                    String discountText = JOptionPane.showInputDialog(this, "Enter discount % (0-100):",
                            "Discount", JOptionPane.QUESTION_MESSAGE);
                    if (discountText != null && !discountText.isEmpty()) {
                        discountPercent = new BigDecimal(discountText);
                        if (discountPercent.compareTo(BigDecimal.ZERO) < 0 || discountPercent.compareTo(new BigDecimal("100")) > 0) {
                            discountPercent = BigDecimal.ZERO;
                        }
                    }
                } catch (Exception e) {
                    discountPercent = BigDecimal.ZERO;
                }
            }
            
            BigDecimal discountAmount = subtotal.multiply(discountPercent).divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
            BigDecimal afterDiscount = subtotal.subtract(discountAmount);
            BigDecimal vatAmount = afterDiscount.multiply(new BigDecimal("0.18")).setScale(2, java.math.RoundingMode.HALF_UP);
            BigDecimal grandTotal = afterDiscount.add(vatAmount);
            
            String customerName = JOptionPane.showInputDialog(this,
                    "Enter customer name (optional):",
                    "Customer Name",
                    JOptionPane.QUESTION_MESSAGE);
            
            Sale sale = new Sale();
            sale.setCashier(currentUser);
            sale.setPaymentMethod(cmbPaymentMethod.getSelectedItem().toString());
            sale.setSaleDate(new java.util.Date());
            
            sale.setSubtotal(subtotal);
            sale.setDiscountPercent(discountPercent);
            sale.setDiscountAmount(discountAmount);
            sale.setVatAmount(vatAmount);
            sale.setTotalAmount(grandTotal);

            List<SaleItem> saleItems = new ArrayList<>();
            for (int i = 0; i < cartProducts.size(); i++) {
                Product product = cartProducts.get(i);
                int quantity = cartQuantities.get(i);
                
                SaleItem item = new SaleItem();
                item.setProduct(product);
                item.setQuantity(quantity);
                item.setUnitPrice(product.getPrice());
                item.setSubTotal(product.getPrice().multiply(java.math.BigDecimal.valueOf(quantity)));
                saleItems.add(item);
            }

            Sale savedSale = saleService.processSale(sale, saleItems);
            
            if (savedSale != null) {
                System.out.println("\n*** SALE PROCESSED SUCCESSFULLY ***");
                System.out.println("Sale ID: " + savedSale.getSaleId());
                System.out.println("Sale Date: " + savedSale.getSaleDate());
                System.out.println("Subtotal: " + savedSale.getSubtotal());
                System.out.println("Discount: " + savedSale.getDiscountPercent() + "% (-" + savedSale.getDiscountAmount() + ")");
                System.out.println("VAT: " + savedSale.getVatAmount());
                System.out.println("Grand Total: " + savedSale.getTotalAmount());
                System.out.println("***********************************\n");
                
                String receiptPath = ReceiptGenerator.generateReceipt(savedSale, saleItems, customerName);
                
                int choice = JOptionPane.showConfirmDialog(this, 
                    "Sale processed successfully!\n" +
                    "Subtotal: " + String.format("%.2f RWF", savedSale.getSubtotal()) + "\n" +
                    "Discount: -" + String.format("%.2f RWF", savedSale.getDiscountAmount()) + "\n" +
                    "VAT (18%): +" + String.format("%.2f RWF", savedSale.getVatAmount()) + "\n" +
                    "Grand Total: " + String.format("%.2f RWF", savedSale.getTotalAmount()) + "\n\n" +
                    "Would you like to open the receipt?",
                    "Success", 
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);
                
                if (choice == JOptionPane.YES_OPTION && receiptPath != null) {
                    try {
                        File receiptFile = new File(receiptPath);
                        if (receiptFile.exists()) {
                            Desktop.getDesktop().open(receiptFile);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, 
                            "Receipt saved at: " + receiptPath + "\nCouldn't open automatically.",
                            "Info", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                
                cartProducts.clear();
                cartQuantities.clear();
                loadCart();
                loadAllProducts();
            }
        } catch (RemoteException ex) {
            JOptionPane.showMessageDialog(this, "Sale error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error processing sale: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnProcessSaleActionPerformed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SaleProcessingModule.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SaleProcessingModule.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SaleProcessingModule.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SaleProcessingModule.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SaleProcessingModule(null).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addBtn;
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnProcessSale;
    private javax.swing.JButton btnSearch;
    private javax.swing.JPanel cartPanel;
    private javax.swing.JComboBox<String> cmbPaymentMethod;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblPaymentMethod;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lblUser;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel paymentPanel;
    private javax.swing.JPanel productsPanel;
    private javax.swing.JButton removeBtn;
    private javax.swing.JPanel searchPanel;
    private javax.swing.JTable tblCart;
    private javax.swing.JTable tblProducts;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
