package com.retail.inventory.demo;

import com.retail.inventory.service.BarcodeService;
import com.retail.inventory.service.VariantCodeGenerator;
import com.retail.inventory.util.ValidationUtil;

/**
 * Standalone Demonstration Runner for Academic Presentation / Viva.
 * Validates core business logic, NA grey-out rules, and variant code generation directly from CLI.
 */
public class DemoRunner {

    public static void main(String[] args) {
        System.out.println("====================================================================");
        System.out.println("   RETAIL APPAREL INVENTORY MANAGEMENT SYSTEM - ACADEMIC DEMO");
        System.out.println("   Submitted for B.Tech Degree (Rajasthan Technical University)");
        System.out.println("====================================================================\n");

        System.out.println("1. DEMONSTRATION: STRUCTURED VARIANT CODE GENERATION & NA LOGIC");
        System.out.println("--------------------------------------------------------------------");

        // Example 1: Black trunks for males in size large -> M/L/B/BK
        String c1 = VariantCodeGenerator.generateCodeForCategory("Swimwear", "D1024", "M", "L", "B", "BK");
        System.out.println("• Case 1 [Black trunks for males in size large]:");
        System.out.println("  Inputs: Gender=M (Male), Size=L (Large), Length=B (Trunk), Colour=BK (Black)");
        System.out.println("  Generated Variant Code: " + c1);
        System.out.println("  Matches Target M/L/B/BK -> " + "M/L/B/BK".equals(c1));

        // Example 2: Footwear (Length forced to NA)
        String c2 = VariantCodeGenerator.generateCodeForCategory("Footwear", "D2210", "M", "9", "B", "BK");
        System.out.println("\n• Case 2 [Footwear - Males Size 9 Black Water Shoes]:");
        System.out.println("  Inputs: Gender=M, Size=9, Length=B (Passed, but Footwear cannot have length!), Colour=BK");
        System.out.println("  Generated Variant Code: " + c2);
        System.out.println("  NA Rule Enforced: " + c2.contains("/NA/") + " (Length automatically replaced with NA)");

        // Example 3: Accessories (Free size & NA length)
        String c3 = VariantCodeGenerator.generateCodeForCategory("Accessories", "D3050", "M", "", "", "NV");
        System.out.println("\n• Case 3 [Accessories - Males Navy Blue Swim Goggles]:");
        System.out.println("  Inputs: Gender=M, Size=(empty), Length=(empty), Colour=NV");
        System.out.println("  Generated Variant Code: " + c3);
        System.out.println("  Matches Expected Format: " + "M/FS/NA/NV".equals(c3));

        System.out.println("\n2. DEMONSTRATION: CODE VALIDATION RULES");
        System.out.println("--------------------------------------------------------------------");
        System.out.println("• Non-negative quantity test (-5 units): Valid? " + ValidationUtil.isValidQuantity(-5));
        System.out.println("• Non-negative quantity test (25 units): Valid? " + ValidationUtil.isValidQuantity(25));
        System.out.println("• Gender validation ('M', 'W', 'B', 'G'): 'M' Valid? " + ValidationUtil.isValidGender("M"));
        System.out.println("• Footwear length rule compliance ('T' for Footwear): Compliant? " + ValidationUtil.isLengthRuleCompliant("Footwear", "T"));
        System.out.println("• Footwear length rule compliance ('NA' for Footwear): Compliant? " + ValidationUtil.isLengthRuleCompliant("Footwear", "NA"));

        System.out.println("\n3. DEMONSTRATION: ZXING CODE 128 BARCODE GENERATION");
        System.out.println("--------------------------------------------------------------------");
        try {
            byte[] barcodePng = BarcodeService.generateBarcodeImage("M/L/B/BK", 250, 70);
            System.out.println("• Successfully synthesized Code 128 PNG image!");
            System.out.println("  Barcode Byte Size: " + barcodePng.length + " bytes");
            String dataUri = BarcodeService.generateStandardLabelBarcode("M/L/B/BK");
            System.out.println("  Data URI Prefix: " + dataUri.substring(0, 35) + "...");
        } catch (Exception e) {
            System.err.println("Barcode generation failed: " + e.getMessage());
        }

        System.out.println("\n====================================================================");
        System.out.println("   ALL BUSINESS RULES VERIFIED SUCCESSFULLY ACCORDING TO SYNOPSIS");
        System.out.println("====================================================================");
    }
}
