package com.retail.inventory.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Master Sales Invoice / Bill model for customer POS checkout.
 */
public class SalesBill implements Serializable {
    private static final long serialVersionUID = 1L;

    private String billNo;
    private String customerName;
    private String customerPhone;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount; // GST
    private BigDecimal netTotal;
    private String paymentMode;   // CASH, UPI, CARD
    private Integer cashierId;
    private String cashierName;
    private Timestamp billDate;

    private List<SalesItem> items = new ArrayList<>();

    public SalesBill() {
        this.customerName = "Walk-in Customer";
        this.customerPhone = "";
        this.subtotal = BigDecimal.ZERO;
        this.discountAmount = BigDecimal.ZERO;
        this.taxAmount = BigDecimal.ZERO;
        this.netTotal = BigDecimal.ZERO;
        this.paymentMode = "CASH";
    }

    public void recalculateTotals(BigDecimal taxRatePercent) {
        BigDecimal sumSubtotal = BigDecimal.ZERO;
        BigDecimal sumDiscount = BigDecimal.ZERO;

        for (SalesItem item : items) {
            BigDecimal rawTotal = item.getUnitMrp().multiply(BigDecimal.valueOf(item.getQuantity()));
            sumSubtotal = sumSubtotal.add(rawTotal);
            BigDecimal itemDiscount = rawTotal.subtract(item.getLineTotal());
            sumDiscount = sumDiscount.add(itemDiscount);
        }

        this.subtotal = sumSubtotal.setScale(2, java.math.RoundingMode.HALF_UP);
        this.discountAmount = sumDiscount.setScale(2, java.math.RoundingMode.HALF_UP);

        BigDecimal taxable = this.subtotal.subtract(this.discountAmount);
        if (taxRatePercent != null && taxRatePercent.compareTo(BigDecimal.ZERO) > 0) {
            this.taxAmount = taxable.multiply(taxRatePercent.divide(BigDecimal.valueOf(100))).setScale(2, java.math.RoundingMode.HALF_UP);
        } else {
            this.taxAmount = BigDecimal.ZERO;
        }

        this.netTotal = taxable.add(this.taxAmount).setScale(2, java.math.RoundingMode.HALF_UP);
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getNetTotal() {
        return netTotal;
    }

    public void setNetTotal(BigDecimal netTotal) {
        this.netTotal = netTotal;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public Integer getCashierId() {
        return cashierId;
    }

    public void setCashierId(Integer cashierId) {
        this.cashierId = cashierId;
    }

    public String getCashierName() {
        return cashierName;
    }

    public void setCashierName(String cashierName) {
        this.cashierName = cashierName;
    }

    public Timestamp getBillDate() {
        return billDate;
    }

    public void setBillDate(Timestamp billDate) {
        this.billDate = billDate;
    }

    public List<SalesItem> getItems() {
        return items;
    }

    public void setItems(List<SalesItem> items) {
        this.items = items;
    }

    public void addItem(SalesItem item) {
        this.items.add(item);
    }

    public int getTotalQuantity() {
        int total = 0;
        for (SalesItem item : items) {
            total += item.getQuantity();
        }
        return total;
    }
}
