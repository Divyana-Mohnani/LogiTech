package com.retail.inventory.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Line item model in a customer sales bill.
 */
public class SalesItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private int itemId;
    private String billNo;
    private String variantCode;
    private String designName;
    private int quantity;
    private BigDecimal unitMrp;
    private BigDecimal discountPercent;
    private BigDecimal lineTotal;

    // Helper display fields
    private String size;
    private String colourName;

    public SalesItem() {
        this.quantity = 1;
        this.unitMrp = BigDecimal.ZERO;
        this.discountPercent = BigDecimal.ZERO;
        this.lineTotal = BigDecimal.ZERO;
    }

    public SalesItem(String variantCode, String designName, int quantity, BigDecimal unitMrp, BigDecimal discountPercent) {
        this.variantCode = variantCode;
        this.designName = designName;
        this.quantity = quantity;
        this.unitMrp = unitMrp != null ? unitMrp : BigDecimal.ZERO;
        this.discountPercent = discountPercent != null ? discountPercent : BigDecimal.ZERO;
        calculateLineTotal();
    }

    public void calculateLineTotal() {
        BigDecimal total = this.unitMrp.multiply(BigDecimal.valueOf(this.quantity));
        if (this.discountPercent.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discountMultiplier = BigDecimal.ONE.subtract(this.discountPercent.divide(BigDecimal.valueOf(100)));
            total = total.multiply(discountMultiplier);
        }
        this.lineTotal = total.setScale(2, java.math.RoundingMode.HALF_UP);
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getVariantCode() {
        return variantCode;
    }

    public void setVariantCode(String variantCode) {
        this.variantCode = variantCode;
    }

    public String getDesignName() {
        return designName;
    }

    public void setDesignName(String designName) {
        this.designName = designName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        calculateLineTotal();
    }

    public BigDecimal getUnitMrp() {
        return unitMrp;
    }

    public void setUnitMrp(BigDecimal unitMrp) {
        this.unitMrp = unitMrp;
        calculateLineTotal();
    }

    public BigDecimal getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(BigDecimal discountPercent) {
        this.discountPercent = discountPercent;
        calculateLineTotal();
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(BigDecimal lineTotal) {
        this.lineTotal = lineTotal;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColourName() {
        return colourName;
    }

    public void setColourName(String colourName) {
        this.colourName = colourName;
    }
}
