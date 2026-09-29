package com.retail.inventory.controller;

import com.retail.inventory.dao.StockDAO;
import com.retail.inventory.model.StockItemView;
import com.retail.inventory.service.VendorImportService;
import com.retail.inventory.service.VendorImportService.ImportResult;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;

/**
 * Controller for vendor data file import (CSV & Excel) and scanner verification.
 */
@WebServlet(name = "ImportServlet", urlPatterns = {"/import"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,  // 2 MB
        maxFileSize = 1024 * 1024 * 10,       // 10 MB
        maxRequestSize = 1024 * 1024 * 50     // 50 MB
)
public class ImportServlet extends HttpServlet {

    private final VendorImportService importService = new VendorImportService();
    private final StockDAO stockDAO = new StockDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String scannedCode = request.getParameter("scannedCode");
        if (scannedCode != null && !scannedCode.trim().isEmpty()) {
            StockItemView item = stockDAO.findByVariantCode(scannedCode.trim());
            request.setAttribute("scannedItem", item);
            request.setAttribute("scannedCode", scannedCode.trim());
            if (item == null) {
                request.setAttribute("scanError", "Scanned barcode '" + scannedCode.trim() + "' was not found in system. You can add it as a new variant.");
            }
        }
        request.getRequestDispatcher("/import.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        // Single scanner lookup submission
        if ("scanConfirm".equals(action)) {
            String variantCode = request.getParameter("variantCode");
            String qtyStr = request.getParameter("confirmQuantity");
            try {
                int addQty = Integer.parseInt(qtyStr);
                StockItemView existing = stockDAO.findByVariantCode(variantCode);
                if (existing != null) {
                    stockDAO.addOrUpdateStock(variantCode, addQty, existing.getVendorPrice(), existing.getMrp(), existing.getConditionId(), existing.getNote());
                    request.setAttribute("scanSuccess", "Successfully added " + addQty + " units to " + variantCode);
                } else {
                    request.setAttribute("scanError", "Could not locate variant " + variantCode);
                }
            } catch (Exception e) {
                request.setAttribute("scanError", "Invalid quantity: " + e.getMessage());
            }
            request.getRequestDispatcher("/import.jsp").forward(request, response);
            return;
        }

        // File upload import (CSV or Excel)
        Part filePart = request.getPart("vendorFile");
        if (filePart == null || filePart.getSize() == 0) {
            request.setAttribute("error", "Please select a valid CSV or Excel file to upload.");
            request.getRequestDispatcher("/import.jsp").forward(request, response);
            return;
        }

        String submittedFileName = filePart.getSubmittedFileName();
        String lowerName = (submittedFileName != null) ? submittedFileName.toLowerCase() : "";

        ImportResult result;
        try (InputStream inputStream = filePart.getInputStream()) {
            if (lowerName.endsWith(".csv")) {
                result = importService.importCSV(inputStream);
            } else if (lowerName.endsWith(".xlsx") || lowerName.endsWith(".xls")) {
                result = importService.importExcel(inputStream);
            } else {
                request.setAttribute("error", "Unsupported file format. Please upload .csv, .xlsx, or .xls file.");
                request.getRequestDispatcher("/import.jsp").forward(request, response);
                return;
            }
        }

        request.setAttribute("importResult", result);
        request.setAttribute("uploadSuccess", result.getSuccessCount() > 0);
        request.getRequestDispatcher("/import.jsp").forward(request, response);
    }
}
