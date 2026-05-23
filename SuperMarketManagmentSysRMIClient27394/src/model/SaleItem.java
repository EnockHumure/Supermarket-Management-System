package model;

import java.io.Serializable;
import java.math.BigDecimal;

public class SaleItem implements Serializable {
    
    public static final long serialVersionUID = 1L;
    
    private Long saleItemId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subTotal;
    private Sale sale;
    private Product product;

    public SaleItem() {
    }

    public SaleItem(Long saleItemId, Integer quantity, BigDecimal unitPrice, 
                   BigDecimal subTotal, Sale sale, Product product) {
        this.saleItemId = saleItemId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subTotal = subTotal;
        this.sale = sale;
        this.product = product;
    }

    public Long getSaleItemId() { return saleItemId; }
    public void setSaleItemId(Long saleItemId) { this.saleItemId = saleItemId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public BigDecimal getSubTotal() { return subTotal; }
    public void setSubTotal(BigDecimal subTotal) { this.subTotal = subTotal; }

    public Sale getSale() { return sale; }
    public void setSale(Sale sale) { this.sale = sale; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
}
