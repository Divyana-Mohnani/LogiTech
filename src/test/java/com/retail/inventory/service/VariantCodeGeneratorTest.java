package com.retail.inventory.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for VariantCodeGenerator.
 * Tests M/L/B/BK format for black trunks for males in size large.
 */
public class VariantCodeGeneratorTest {

    @Test
    @DisplayName("Should generate standard code: M/L/B/BK for black trunks for males in size large")
    void testStandardVariantCodeGeneration() {
        String code = VariantCodeGenerator.generateCode("M", "L", "B", "BK");
        assertEquals("M/L/B/BK", code);

        // Also verify category builder with T -> B normalization
        String code2 = VariantCodeGenerator.generateCodeForCategory("Swimwear", "D1024", "M", "L", "T", "BK");
        assertEquals("M/L/B/BK", code2, "Should produce M/L/B/BK for black trunks for males in size large");
    }

    @Test
    @DisplayName("Should enforce NA length for Footwear: M/9/NA/BK")
    void testFootwearVariantCodeNAEnforcement() {
        String code = VariantCodeGenerator.generateCodeForCategory("Footwear", "D2210", "M", "9", "T", "BK");
        assertEquals("M/9/NA/BK", code, "Footwear must automatically force length to NA regardless of input");
    }

    @Test
    @DisplayName("Should enforce NA length and default FS for Accessories: M/FS/NA/NV")
    void testAccessoriesVariantCodeNAEnforcement() {
        String code = VariantCodeGenerator.generateCodeForCategory("Accessories", "D3050", "M", "", "SH", "NV");
        assertEquals("M/FS/NA/NV", code, "Accessories should enforce NA length and default to Free Size (FS)");
    }

    @Test
    @DisplayName("Should parse 4 components correctly from M/L/B/BK")
    void testParseVariantCode() {
        String[] parts = VariantCodeGenerator.parseCode("M/L/B/BK");
        assertEquals("M", parts[1]);
        assertEquals("L", parts[2]);
        assertEquals("B", parts[3]);
        assertEquals("BK", parts[4]);
    }

    @Test
    @DisplayName("Should sanitize lowercase inputs to uppercase")
    void testSanitizationToUppercase() {
        String code = VariantCodeGenerator.generateCode("m", "l", "b", "bk");
        assertEquals("M/L/B/BK", code);
    }
}
