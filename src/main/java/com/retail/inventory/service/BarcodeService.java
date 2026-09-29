package com.retail.inventory.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Barcode Service using ZXing library to generate Code 128 barcodes.
 * Code 128 can encode alphanumeric characters and slashes directly (e.g. M/L/B/BK).
 */
public class BarcodeService {
    private static final Logger LOGGER = Logger.getLogger(BarcodeService.class.getName());

    /**
     * Generates Code 128 barcode as a byte array of PNG image.
     */
    public static byte[] generateBarcodeImage(String barcodeText, int width, int height) throws Exception {
        if (barcodeText == null || barcodeText.trim().isEmpty()) {
            throw new IllegalArgumentException("Barcode text cannot be empty");
        }

        Code128Writer barcodeWriter = new Code128Writer();
        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.MARGIN, 2);

        BitMatrix bitMatrix = barcodeWriter.encode(barcodeText.trim(), BarcodeFormat.CODE_128, width, height, hints);

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", bos);
            return bos.toByteArray();
        }
    }

    /**
     * Generates a Base64 data URI string suitable for inline <img> rendering in JSP/HTML:
     * data:image/png;base64,...
     */
    public static String generateBarcodeBase64(String barcodeText, int width, int height) {
        try {
            byte[] imageBytes = generateBarcodeImage(barcodeText, width, height);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to generate barcode for: " + barcodeText, e);
            return "";
        }
    }

    /**
     * Convenience method with standard label dimensions (250 x 80 px).
     */
    public static String generateStandardLabelBarcode(String barcodeText) {
        return generateBarcodeBase64(barcodeText, 250, 70);
    }
}
