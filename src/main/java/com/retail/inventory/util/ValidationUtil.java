package com.retail.inventory.util;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Validation utility enforcing business rules from synopsis Page 13:
 * - Quantity >= 0
 * - Price non-negative
 * - Category-specific NA enforcement
 * - Allowed gender codes
 */
public class ValidationUtil {

    private static final Set<String> VALID_GENDERS = new HashSet<>(Arrays.asList("M", "W", "B", "G"));
    private static final Set<String> VALID_CATEGORIES = new HashSet<>(Arrays.asList("SWIMWEAR", "FOOTWEAR", "ACCESSORIES"));

    /**
     * Validates that quantity is non-negative.
     */
    public static boolean isValidQuantity(int quantity) {
        return quantity >= 0;
    }

    /**
     * Validates that prices are non-negative.
     */
    public static boolean isValidPrice(BigDecimal price) {
        return price != null && price.compareTo(BigDecimal.ZERO) >= 0;
    }

    /**
     * Validates gender code (M, W, B, G).
     */
    public static boolean isValidGender(String gender) {
        return gender != null && VALID_GENDERS.contains(gender.trim().toUpperCase());
    }

    /**
     * Validates category name.
     */
    public static boolean isValidCategory(String category) {
        return category != null && VALID_CATEGORIES.contains(category.trim().toUpperCase());
    }

    /**
     * Checks if sleeve length applies to the category.
     * Footwear and Accessories must be NA.
     */
    public static boolean doesSleeveLengthApply(String category) {
        if (category == null) return false;
        return "SWIMWEAR".equalsIgnoreCase(category.trim());
    }

    /**
     * Checks if lower length applies to the category.
     * Footwear and Accessories must be NA.
     */
    public static boolean doesLowerLengthApply(String category) {
        if (category == null) return false;
        return "SWIMWEAR".equalsIgnoreCase(category.trim());
    }

    /**
     * Verifies that for Footwear or Accessories, length attribute is indeed NA.
     */
    public static boolean isLengthRuleCompliant(String category, String length) {
        if (category == null) return true;
        String cat = category.trim().toUpperCase();
        if ("FOOTWEAR".equals(cat) || "ACCESSORIES".equals(cat)) {
            return length == null || length.trim().isEmpty() || "NA".equalsIgnoreCase(length.trim());
        }
        return true;
    }
}
