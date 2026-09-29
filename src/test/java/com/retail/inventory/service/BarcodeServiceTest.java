package com.retail.inventory.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for ZXing Barcode generation (Code 128).
 */
public class BarcodeServiceTest {

    @Test
    @DisplayName("Should generate valid Code 128 barcode image bytes and Base64 data URI")
    void testBarcodeGeneration() throws Exception {
        String testCode = "M/L/B/BK";
        byte[] imageBytes = BarcodeService.generateBarcodeImage(testCode, 200, 60);

        assertNotNull(imageBytes, "Barcode byte array should not be null");
        assertTrue(imageBytes.length > 0, "Barcode image should contain PNG bytes");

        // Verify Base64 data URI format
        String base64Uri = BarcodeService.generateBarcodeBase64(testCode, 200, 60);
        assertTrue(base64Uri.startsWith("data:image/png;base64,"), "Should produce valid data:image/png URI");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when barcode text is empty")
    void testEmptyBarcode() {
        assertThrows(IllegalArgumentException.class, () -> {
            BarcodeService.generateBarcodeImage("", 200, 60);
        });
    }
}
