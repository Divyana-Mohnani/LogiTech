package com.retail.inventory.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ValidationUtil enforcing business validation rules (Page 13).
 */
public class ValidationUtilTest {

    @Test
    @DisplayName("Should validate quantity is non-negative")
    void testQuantityValidation() {
        assertTrue(ValidationUtil.isValidQuantity(0));
        assertTrue(ValidationUtil.isValidQuantity(100));
        assertFalse(ValidationUtil.isValidQuantity(-1));
        assertFalse(ValidationUtil.isValidQuantity(-50));
    }

    @Test
    @DisplayName("Should validate price is non-negative")
    void testPriceValidation() {
        assertTrue(ValidationUtil.isValidPrice(BigDecimal.ZERO));
        assertTrue(ValidationUtil.isValidPrice(new BigDecimal("999.00")));
        assertFalse(ValidationUtil.isValidPrice(new BigDecimal("-10.00")));
        assertFalse(ValidationUtil.isValidPrice(null));
    }

    @Test
    @DisplayName("Should validate gender codes M, W, B, G")
    void testGenderValidation() {
        assertTrue(ValidationUtil.isValidGender("M"));
        assertTrue(ValidationUtil.isValidGender("W"));
        assertTrue(ValidationUtil.isValidGender("B"));
        assertTrue(ValidationUtil.isValidGender("G"));
        assertFalse(ValidationUtil.isValidGender("X"));
        assertFalse(ValidationUtil.isValidGender(""));
        assertFalse(ValidationUtil.isValidGender(null));
    }

    @Test
    @DisplayName("Should enforce NA length rule for Footwear and Accessories")
    void testLengthRuleCompliance() {
        assertTrue(ValidationUtil.isLengthRuleCompliant("Footwear", "NA"));
        assertTrue(ValidationUtil.isLengthRuleCompliant("Footwear", ""));
        assertTrue(ValidationUtil.isLengthRuleCompliant("Footwear", null));
        assertFalse(ValidationUtil.isLengthRuleCompliant("Footwear", "T"));

        assertTrue(ValidationUtil.isLengthRuleCompliant("Accessories", "NA"));
        assertFalse(ValidationUtil.isLengthRuleCompliant("Accessories", "FS"));

        assertTrue(ValidationUtil.isLengthRuleCompliant("Swimwear", "T"));
        assertTrue(ValidationUtil.isLengthRuleCompliant("Swimwear", "FS"));
    }
}
