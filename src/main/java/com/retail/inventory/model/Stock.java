package com.retail.inventory.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Stock model representing a specific stock line for a variant.
 * Tracks quantities, purchase/vendor price, MRP selling price, condition, and optional notes.
 */
public class Stock implements Serializable {
    private static final long serialVersionUID = 1L;

    private int stockId;
    private String variantCode;
    private int quantity;
    private BigDecimal vendorPrice;
    private BigDecimal mrp;
    private int conditionId;
    private String note;
    private Timestamp updatedAt;

    // Joined helper models
    private Variant variant;
    private StockCondition condition;

    public Stock() {
        this.vendorPrice = BigDecimal.ZERO;
        this.mrp = BigDecimal.ZERO;
    }

    public Stock(String variantCode, int quantity, BigDecimal vendorPrice, BigDecimal mrp, int conditionId, String note) {
        this.variantCode = variantCode;
        this.quantity = quantity;
        this.vendorPrice = vendorPrice;
        this.mrp = mrp;
        this.conditionId = conditionId;
        this.note = note;
    }

    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    public String getVariantCode() {
        return variantCode;
    }

    public void setVariantCode(String variantCode) {
        this.variantCode = variantCode;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getVendorPrice() {
        return vendorPrice;
    }

    public void setVendorPrice(BigDecimal vendorPrice) {
        this.vendorPrice = vendorPrice;
    }

    public BigDecimal getMrp() {
        return mrp;
    }

    public void setMrp(BigDecimal mrp) {
        this.mrp = mrp;
    }

    public int getConditionId() {
        return conditionId;
    }

    public void setConditionId(int conditionId) {
        this.conditionId = conditionId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Variant getVariant() {
        return variant;
    }

    public void setVariant(Variant variant) {
        this.variant = variant;
    }

    public StockCondition getCondition() {
        return condition;
    }

    public void setCondition(StockCondition condition) {
        this.condition = condition;
    }

    /**
     * Total stock value at MRP
     */
    public BigDecimal getTotalMrpValue() {
        if (mrp == null) return BigDecimal.ZERO;
        return mrp.multiply(BigDecimal.valueOf(quantity));
    }

    /**
     * Total inventory cost at Vendor Price
     */
    public BigDecimal getTotalCostValue() {
        if (vendorPrice == null) return BigDecimal.ZERO;
        return vendorPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
