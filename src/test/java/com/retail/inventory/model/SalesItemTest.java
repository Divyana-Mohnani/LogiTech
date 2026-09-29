package com.retail.inventory.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SalesItem and SalesBill discount & calculation logic.
 */
public class SalesItemTest {

    @Test
    @DisplayName("Should compute line total without discount correctly")
    void testLineTotalWithoutDiscount() {
        SalesItem item = new SalesItem("M/L/B/BK", "Classic Trunk", 2, new BigDecimal("999.00"), BigDecimal.ZERO);
        assertEquals(new BigDecimal("1998.00"), item.getLineTotal());
    }

    @Test
    @DisplayName("Should compute line total with 20% discount (old season clearance)")
    void testLineTotalWithDiscount() {
        SalesItem item = new SalesItem("M/XL/SH/BL", "Classic Trunk", 1, new BigDecimal("1000.00"), new BigDecimal("20.00"));
        assertEquals(new BigDecimal("800.00"), item.getLineTotal());
    }

    @Test
    @DisplayName("Should recalculate totals with 5% GST on bill")
    void testBillTotalsWithGst() {
        SalesBill bill = new SalesBill();
        bill.addItem(new SalesItem("M/L/B/BK", "Classic Trunk", 1, new BigDecimal("1000.00"), BigDecimal.ZERO));

        bill.recalculateTotals(new BigDecimal("5.00")); // 5% GST

        assertEquals(new BigDecimal("1000.00"), bill.getSubtotal());
        assertEquals(new BigDecimal("0.00"), bill.getDiscountAmount());
        assertEquals(new BigDecimal("50.00"), bill.getTaxAmount());
        assertEquals(new BigDecimal("1050.00"), bill.getNetTotal());
    }
}
