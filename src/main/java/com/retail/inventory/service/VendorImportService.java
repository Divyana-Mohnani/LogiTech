package com.retail.inventory.service;

import com.retail.inventory.dao.DesignDAO;
import com.retail.inventory.dao.MasterDAO;
import com.retail.inventory.dao.StockDAO;
import com.retail.inventory.dao.VariantDAO;
import com.retail.inventory.model.Colour;
import com.retail.inventory.model.Design;
import com.retail.inventory.model.StockCondition;
import com.retail.inventory.model.Variant;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service to process Vendor data files (CSV and Excel formats).
 * Maps columns, auto-creates missing designs/variants, and registers incoming stock.
 */
public class VendorImportService {
    private static final Logger LOGGER = Logger.getLogger(VendorImportService.class.getName());

    private final DesignDAO designDAO = new DesignDAO();
    private final VariantDAO variantDAO = new VariantDAO();
    private final StockDAO stockDAO = new StockDAO();
    private final MasterDAO masterDAO = new MasterDAO();

    public static class ImportResult {
        private int totalRows = 0;
        private int successCount = 0;
        private int failureCount = 0;
        private final List<String> errorMessages = new ArrayList<>();

        public int getTotalRows() { return totalRows; }
        public int getSuccessCount() { return successCount; }
        public int getFailureCount() { return failureCount; }
        public List<String> getErrorMessages() { return errorMessages; }

        public void addSuccess() { totalRows++; successCount++; }
        public void addFailure(String error) { totalRows++; failureCount++; errorMessages.add(error); }
    }

    /**
     * Imports records from a CSV file stream.
     */
    public ImportResult importCSV(InputStream inputStream) {
        ImportResult result = new ImportResult();
        try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).setIgnoreHeaderCase(true).setTrim(true).build())) {

            for (CSVRecord record : csvParser) {
                try {
                    processRow(record.toMap(), result);
                } catch (Exception e) {
                    result.addFailure("Row " + record.getRecordNumber() + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed reading CSV", e);
            result.addFailure("Failed reading CSV file: " + e.getMessage());
        }
        return result;
    }

    /**
     * Imports records from an Excel file stream (.xlsx, .xls).
     */
    public ImportResult importExcel(InputStream inputStream) {
        ImportResult result = new ImportResult();
        DataFormatter formatter = new DataFormatter();

        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            if (!rowIterator.hasNext()) {
                result.addFailure("Excel sheet is completely empty");
                return result;
            }

            // Read header row
            Row headerRow = rowIterator.next();
            Map<Integer, String> headerMap = new HashMap<>();
            for (Cell cell : headerRow) {
                String val = formatter.formatCellValue(cell).trim().toLowerCase();
                if (!val.isEmpty()) {
                    headerMap.put(cell.getColumnIndex(), val);
                }
            }

            // Read data rows
            int rowNum = 1;
            while (rowIterator.hasNext()) {
                rowNum++;
                Row row = rowIterator.next();
                Map<String, String> rowData = new HashMap<>();
                for (Map.Entry<Integer, String> entry : headerMap.entrySet()) {
                    Cell cell = row.getCell(entry.getKey());
                    rowData.put(entry.getValue(), formatter.formatCellValue(cell).trim());
                }

                // Check if row is empty
                boolean allEmpty = true;
                for (String v : rowData.values()) {
                    if (!v.isEmpty()) { allEmpty = false; break; }
                }
                if (allEmpty) continue;

                try {
                    processRow(rowData, result);
                } catch (Exception e) {
                    result.addFailure("Excel Row " + rowNum + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed reading Excel workbook", e);
            result.addFailure("Failed reading Excel file: " + e.getMessage());
        }
        return result;
    }

    /**
     * Common processing logic for each row (CSV or Excel).
     */
    private void processRow(Map<String, String> row, ImportResult result) {
        // Look up fields case-insensitively
        String barcode = getVal(row, "barcode", "variant_code", "sku", "code");
        String designNo = getVal(row, "design_no", "designno", "design", "model");
        String category = getVal(row, "category", "type");
        String designName = getVal(row, "design_name", "product_name", "name");
        String gender = getVal(row, "gender");
        String size = getVal(row, "size");
        String length = getVal(row, "length");
        String colourCode = getVal(row, "colour_code", "colour", "color");
        String qtyStr = getVal(row, "quantity", "qty");
        String vendorPriceStr = getVal(row, "vendor_price", "cost", "purchase_price");
        String mrpStr = getVal(row, "mrp", "price", "retail_price");
        String conditionName = getVal(row, "condition", "stock_condition");
        String note = getVal(row, "note", "special_note", "remark");

        // Derive missing barcode or construct structured variant code
        if ((barcode == null || barcode.isEmpty()) && designNo != null && !designNo.isEmpty()) {
            barcode = VariantCodeGenerator.generateCodeForCategory(category, designNo, gender, size, length, colourCode);
        }

        if (barcode == null || barcode.isEmpty()) {
            result.addFailure("Missing barcode/variant_code and insufficient attributes to generate one");
            return;
        }

        barcode = barcode.trim().toUpperCase();

        // Parse variant parts from code if attributes were omitted
        String[] parsed = VariantCodeGenerator.parseCode(barcode);
        if (designNo == null || designNo.isEmpty()) designNo = parsed[0];
        if (gender == null || gender.isEmpty()) gender = parsed[1];
        if (size == null || size.isEmpty()) size = parsed[2];
        if (length == null || length.isEmpty()) length = parsed[3];
        if (colourCode == null || colourCode.isEmpty()) colourCode = parsed[4];

        if (category == null || category.isEmpty()) {
            category = "Swimwear"; // Default fallback
        }
        if (designName == null || designName.isEmpty()) {
            designName = "Design " + designNo;
        }

        // 1. Ensure Design exists
        Design existingDesign = designDAO.getDesignByNo(designNo);
        if (existingDesign == null) {
            Design newDesign = new Design(designNo, designName, category, "Imported from vendor data file");
            designDAO.addDesign(newDesign);
        }

        // 2. Ensure Colour exists
        if (masterDAO.getColourByCode(colourCode) == null) {
            masterDAO.addColour(new Colour(colourCode, "Colour " + colourCode));
        }

        // 3. Ensure Variant exists
        if (!variantDAO.exists(barcode)) {
            Variant v = new Variant(barcode, designNo, gender, size, length, colourCode);
            variantDAO.addVariant(v);
        }

        // 4. Parse quantities & prices
        int quantity = 1;
        if (qtyStr != null && !qtyStr.isEmpty()) {
            try {
                quantity = (int) Double.parseDouble(qtyStr);
            } catch (NumberFormatException ignored) {}
        }

        BigDecimal vendorPrice = BigDecimal.ZERO;
        if (vendorPriceStr != null && !vendorPriceStr.isEmpty()) {
            try {
                vendorPrice = new BigDecimal(vendorPriceStr.replaceAll("[^0-9.]", ""));
            } catch (Exception ignored) {}
        }

        BigDecimal mrp = vendorPrice.multiply(new BigDecimal("2.0"));
        if (mrpStr != null && !mrpStr.isEmpty()) {
            try {
                mrp = new BigDecimal(mrpStr.replaceAll("[^0-9.]", ""));
            } catch (Exception ignored) {}
        }

        int conditionId = 1; // Default Normal
        if (conditionName != null && !conditionName.isEmpty()) {
            StockCondition sc = masterDAO.getConditionByName(conditionName);
            if (sc != null) {
                conditionId = sc.getConditionId();
            }
        }

        // 5. Add or increment stock
        boolean saved = stockDAO.addOrUpdateStock(barcode, quantity, vendorPrice, mrp, conditionId, note);
        if (saved) {
            result.addSuccess();
        } else {
            result.addFailure("Failed to persist stock record for: " + barcode);
        }
    }

    private String getVal(Map<String, String> map, String... keys) {
        for (String key : keys) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(key) && entry.getValue() != null && !entry.getValue().trim().isEmpty()) {
                    return entry.getValue().trim();
                }
            }
        }
        return null;
    }
}
