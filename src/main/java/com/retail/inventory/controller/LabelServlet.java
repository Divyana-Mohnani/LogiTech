package com.retail.inventory.controller;

import com.retail.inventory.dao.StockDAO;
import com.retail.inventory.model.StockItemView;
import com.retail.inventory.service.BarcodeService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Controller for generating printable Code 128 barcode labels with MRP for job-worker goods.
 */
@WebServlet(name = "LabelServlet", urlPatterns = {"/label"})
public class LabelServlet extends HttpServlet {

    private final StockDAO stockDAO = new StockDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String stockIdStr = request.getParameter("stockId");
        String variantCode = request.getParameter("variantCode");
        String copiesStr = request.getParameter("copies");

        int copies = 1;
        if (copiesStr != null && !copiesStr.trim().isEmpty()) {
            try {
                copies = Math.max(1, Integer.parseInt(copiesStr.trim()));
            } catch (NumberFormatException ignored) {}
        }

        StockItemView item = null;
        if (stockIdStr != null && !stockIdStr.trim().isEmpty()) {
            try {
                item = stockDAO.getStockViewById(Integer.parseInt(stockIdStr.trim()));
            } catch (NumberFormatException ignored) {}
        } else if (variantCode != null && !variantCode.trim().isEmpty()) {
            item = stockDAO.findByVariantCode(variantCode.trim());
        }

        if (item != null) {
            String barcodeDataUri = BarcodeService.generateStandardLabelBarcode(item.getVariantCode());
            request.setAttribute("item", item);
            request.setAttribute("barcodeDataUri", barcodeDataUri);
            request.setAttribute("copies", copies);
        } else {
            // Provide list for the user to select which item to generate labels for
            List<StockItemView> allItems = stockDAO.searchStock(null, null, null, null, null);
            request.setAttribute("allItems", allItems);
        }

        request.getRequestDispatcher("/label.jsp").forward(request, response);
    }
}
