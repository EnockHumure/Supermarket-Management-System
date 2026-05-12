package service;

import java.io.Serializable;

public class LowStockEvent implements Serializable {
    public static final long serialVersionUID = 1L;
    
    private Long productId;
    private String productName;
    private String barcode;
    private Integer currentStock;
    private Integer reorderLevel;
    private String eventTime;
    
    public LowStockEvent() {
    }
    
    public LowStockEvent(Long productId, String productName, String barcode, 
                        Integer currentStock, Integer reorderLevel, String eventTime) {
        this.productId = productId;
        this.productName = productName;
        this.barcode = barcode;
        this.currentStock = currentStock;
        this.reorderLevel = reorderLevel;
        this.eventTime = eventTime;
    }
    
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    
    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }
    
    public Integer getCurrentStock() { return currentStock; }
    public void setCurrentStock(Integer currentStock) { this.currentStock = currentStock; }
    
    public Integer getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(Integer reorderLevel) { this.reorderLevel = reorderLevel; }
    
    public String getEventTime() { return eventTime; }
    public void setEventTime(String eventTime) { this.eventTime = eventTime; }
}
