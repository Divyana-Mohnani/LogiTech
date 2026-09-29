package com.retail.inventory.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Composite View model aggregating Design, Variant, Colour, Condition and Stock details.
 * Optimized for display in tables, reports, search results, and barcode label rendering.
 */
public class StockItemView implements Serializable {
    private static final long serialVersionUID = 1L;

    private int stockId;
    private String variantCode;
    private String designNo;
    private String designName;
    private String category;
    private String gender;
    private String size;
    private String length;
    private String colourCode;
    private String colourName;
    private int quantity;
    private BigDecimal vendorPrice;
    private BigDecimal mrp;
    private int conditionId;
    private String conditionName;
    private String note;
    private Timestamp updatedAt;

    public StockItemView() {
        this.vendorPrice = BigDecimal.ZERO;
        this.mrp = BigDecimal.ZERO;
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

    public String getDesignNo() {
        return designNo;
    }

    public void setDesignNo(String designNo) {
        this.designNo = designNo;
    }

    public String getDesignName() {
        return designName;
    }

    public void setDesignName(String designName) {
        this.designName = designName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getColourCode() {
        return colourCode;
    }

    public void setColourCode(String colourCode) {
        this.colourCode = colourCode;
    }

    public String getColourName() {
        return colourName;
    }

    public void setColourName(String colourName) {
        this.colourName = colourName;
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

    public String getConditionName() {
        return conditionName;
    }

    public void setConditionName(String conditionName) {
        this.conditionName = conditionName;
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

    public BigDecimal getTotalCost() {
        if (vendorPrice == null) return BigDecimal.ZERO;
        return vendorPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public BigDecimal getTotalMrp() {
        if (mrp == null) return BigDecimal.ZERO;
        return mrp.multiply(BigDecimal.valueOf(quantity));
    }

    public String getGenderLabel() {
        if ("M".equalsIgnoreCase(gender)) return "Gents";
        if ("W".equalsIgnoreCase(gender)) return "Ladies";
        if ("B".equalsIgnoreCase(gender)) return "Boys";
        if ("G".equalsIgnoreCase(gender)) return "Girls";
        return gender != null ? gender : "";
    }

    public String getLengthLabel() {
        if (length == null || "NA".equalsIgnoreCase(length)) return "N/A";
        switch (length.toUpperCase()) {
            case "T": return "Trunk";
            case "SH": return "Shorts";
            case "CP": return "Capri";
            case "FP": return "Full pant";
            case "FS": return "Full sleeve";
            case "HS": return "Half sleeve";
            case "SL": return "Sleeveless";
            default: return length;
        }
    }
}
