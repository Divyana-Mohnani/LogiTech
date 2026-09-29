package com.retail.inventory.controller;

import com.retail.inventory.dao.SalesDAO;
import com.retail.inventory.model.SalesBill;
import com.retail.inventory.service.BarcodeService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Controller for displaying and printing customer sales receipts.
 */
@WebServlet(name = "ReceiptServlet", urlPatterns = {"/receipt"})
public class ReceiptServlet extends HttpServlet {

    private final SalesDAO salesDAO = new SalesDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String billNo = request.getParameter("billNo");

        if (billNo == null || billNo.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/billing");
            return;
        }

        SalesBill bill = salesDAO.getBillByNo(billNo.trim());

        if (bill != null) {
            // Generate barcode for invoice number
            String invoiceBarcodeDataUri = BarcodeService.generateStandardLabelBarcode(bill.getBillNo());
            request.setAttribute("bill", bill);
            request.setAttribute("invoiceBarcodeDataUri", invoiceBarcodeDataUri);
            request.setAttribute("newSale", "true".equals(request.getParameter("newSale")));
            request.getRequestDispatcher("/receipt.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "Sales invoice '" + billNo + "' not found.");
            request.getRequestDispatcher("/billing.jsp").forward(request, response);
        }
    }
}
