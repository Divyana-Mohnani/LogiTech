package com.retail.inventory.service;

import java.util.Locale;

/**
 * Generates and validates structured variant codes.
 * Format: GENDER/SIZE/LENGTH/COLOUR
 * Example:
 *  - M/L/B/BK (Black trunks for males in size large)
 *  - M/9/NA/BK (Footwear with NA length)
 *  - M/FS/NA/NV (Accessories with Free Size and NA length)
 */
public class VariantCodeGenerator {

    public static final String NA = "NA";

    /**
     * Builds standard 4-part variant code: GENDER/SIZE/LENGTH/COLOUR
     * Example: M/L/B/BK
     */
    public static String generateCode(String gender, String size, String length, String colourCode) {
        String cleanGender = sanitize(gender, NA);
        String cleanSize = sanitize(size, NA);
        String cleanLength = sanitizeLength(length, NA);
        String cleanColour = sanitize(colourCode, NA);

        return String.format("%s/%s/%s/%s", cleanGender, cleanSize, cleanLength, cleanColour);
    }

    /**
     * Overload accepting designNo, outputting standard GENDER/SIZE/LENGTH/COLOUR code (e.g. M/L/B/BK).
     */
    public static String generateCode(String designNo, String gender, String size, String length, String colourCode) {
        return generateCode(gender, size, length, colourCode);
    }

    /**
     * Builds variant code enforcing category-specific NA business logic.
     *
     * Rules:
     * - Accessories: Length is always NA. Size defaults to FS or NA if blank.
     * - Footwear: Length is always NA. Size is numeric or NA.
     * - Swimwear / Bottoms: Trunks/Bottoms map to B.
     */
    public static String generateCodeForCategory(String category, String designNo, String gender, String size, String length, String colourCode) {
        String cat = (category != null) ? category.trim().toLowerCase(Locale.ROOT) : "";

        String effectiveLength = length;
        String effectiveSize = size;

        if (cat.contains("accessor") || cat.contains("footwear")) {
            effectiveLength = NA;
        } else {
            // For swimwear/outerwear trunks, ensure 'B' is used
            if ("T".equalsIgnoreCase(effectiveLength) || "TRUNK".equalsIgnoreCase(effectiveLength)) {
                effectiveLength = "B";
            }
        }

        if (cat.contains("accessor") && (effectiveSize == null || effectiveSize.trim().isEmpty())) {
            effectiveSize = "FS";
        }

        return generateCode(gender, effectiveSize, effectiveLength, colourCode);
    }

    /**
     * Sanitizes length: maps T (legacy trunk) to B (trunks/bottoms).
     */
    private static String sanitizeLength(String val, String fallback) {
        if (val == null || val.trim().isEmpty()) {
            return fallback;
        }
        String v = val.trim().toUpperCase(Locale.ROOT);
        if ("T".equals(v) || "TRUNK".equals(v)) {
            return "B";
        }
        return v;
    }

    /**
     * Sanitizes a code segment: trims, converts to uppercase, replaces empty/null with fallback.
     */
    private static String sanitize(String val, String fallback) {
        if (val == null || val.trim().isEmpty()) {
            return fallback;
        }
        return val.trim().toUpperCase(Locale.ROOT);
    }

    /**
     * Deconstructs a variant code into its component parts.
     * Supports both slash "/" (M/L/B/BK) and hyphen "-" formats.
     * Returns: [designNo, gender, size, length, colourCode]
     */
    public static String[] parseCode(String variantCode) {
        if (variantCode == null || variantCode.trim().isEmpty()) {
            return new String[]{"DEFAULT", NA, NA, NA, NA};
        }

        String code = variantCode.trim();
        String delimiter = code.contains("/") ? "/" : "-";
        String[] parts = code.split(delimiter);

        if (parts.length == 4) {
            // Format: GENDER/SIZE/LENGTH/COLOUR (e.g. M/L/B/BK)
            return new String[]{"DEFAULT", parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim()};
        } else if (parts.length == 5) {
            // Format with design: DESIGN/GENDER/SIZE/LENGTH/COLOUR
            return new String[]{parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim(), parts[4].trim()};
        }

        // Fallback for safety
        String[] result = new String[]{"DEFAULT", NA, NA, NA, NA};
        for (int i = 0; i < parts.length && i < 4; i++) {
            result[i + 1] = parts[i].trim();
        }
        return result;
    }
}
