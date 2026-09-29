package com.retail.inventory.controller;

import com.retail.inventory.dao.DesignDAO;
import com.retail.inventory.dao.MasterDAO;
import com.retail.inventory.dao.StockDAO;
import com.retail.inventory.dao.VariantDAO;
import com.retail.inventory.model.Colour;
import com.retail.inventory.model.Design;
import com.retail.inventory.model.StockCondition;
import com.retail.inventory.model.Variant;
import com.retail.inventory.service.VariantCodeGenerator;
import com.retail.inventory.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Controller for the Stock Update screen (matches Synopsis Figure 4).
 * Enforces dynamic category NA logic and automatic variant code generation.
 */
@WebServlet(name = "StockServlet", urlPatterns = {"/stock"})
public class StockServlet extends HttpServlet {

    private final DesignDAO designDAO = new DesignDAO();
    private final MasterDAO masterDAO = new MasterDAO();
    private final VariantDAO variantDAO = new VariantDAO();
    private final StockDAO stockDAO = new StockDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        // Quick AJAX preview of generated code
        if ("previewCode".equals(action)) {
            String category = request.getParameter("category");
            String designNo = request.getParameter("designNo");
            String gender = request.getParameter("gender");
            String size = request.getParameter("size");
            String length = request.getParameter("length");
            String colourCode = request.getParameter("colourCode");

            String code = VariantCodeGenerator.generateCodeForCategory(category, designNo, gender, size, length, colourCode);
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write(code);
            return;
        }

        loadFormData(request);
        request.getRequestDispatcher("/stockUpdate.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String category = request.getParameter("category");
        String designNo = request.getParameter("designNo");
        String gender = request.getParameter("gender");
        String size = request.getParameter("size");
        String sleeveLength = request.getParameter("sleeveLength");
        String lowerLength = request.getParameter("lowerLength");
        String lengthChoice = request.getParameter("lengthChoice"); // "sleeve", "lower", or "na"
        String colourCode = request.getParameter("colourCode");
        String quantityStr = request.getParameter("quantity");
        String vendorPriceStr = request.getParameter("vendorPrice");
        String mrpStr = request.getParameter("mrp");
        String conditionIdStr = request.getParameter("conditionId");
        String note = request.getParameter("note");

        // Determine effective length attribute
        String swimwearType = request.getParameter("swimwearType");
        if (swimwearType == null) swimwearType = request.getParameter("lengthChoice");
        if (swimwearType == null) swimwearType = "bottom";

        String length = "NA";
        if ("Swimwear".equalsIgnoreCase(category) || "Outerwear".equalsIgnoreCase(category)) {
            if ("top".equalsIgnoreCase(swimwearType) && sleeveLength != null && !sleeveLength.trim().isEmpty()) {
                length = sleeveLength.trim();
            } else if ("bottom".equalsIgnoreCase(swimwearType) && lowerLength != null && !lowerLength.trim().isEmpty()) {
                length = lowerLength.trim();
            }
        }

        // Generate variant code according to synopsis specification
        String variantCode = VariantCodeGenerator.generateCodeForCategory(category, designNo, gender, size, length, colourCode);

        // Validation rules (Synopsis Page 13)
        int quantity = 0;
        try {
            quantity = Integer.parseInt(quantityStr);
            if (!ValidationUtil.isValidQuantity(quantity)) {
                throw new NumberFormatException();
            }
        } catch (Exception e) {
            forwardWithError(request, response, "Quantity must be a valid whole number and cannot be negative.");
            return;
        }

        BigDecimal vendorPrice = BigDecimal.ZERO;
        BigDecimal mrp = BigDecimal.ZERO;
        try {
            if (vendorPriceStr != null && !vendorPriceStr.trim().isEmpty()) {
                vendorPrice = new BigDecimal(vendorPriceStr.trim());
            }
            if (mrpStr != null && !mrpStr.trim().isEmpty()) {
                mrp = new BigDecimal(mrpStr.trim());
            }
            if (!ValidationUtil.isValidPrice(vendorPrice) || !ValidationUtil.isValidPrice(mrp)) {
                throw new IllegalArgumentException();
            }
        } catch (Exception e) {
            forwardWithError(request, response, "Vendor price and MRP must be valid non-negative numbers.");
            return;
        }

        int conditionId = 1;
        try {
            conditionId = Integer.parseInt(conditionIdStr);
        } catch (Exception ignored) {}

        // Server-side NA rule enforcement
        if (!ValidationUtil.isLengthRuleCompliant(category, length)) {
            forwardWithError(request, response, "Invalid attributes: Length must be NA for category " + category);
            return;
        }

        // 1. Create Design if it doesn't already exist
        Design design = designDAO.getDesignByNo(designNo);
        if (design == null) {
            design = new Design(designNo, "Design " + designNo, category, "Auto-created on stock entry");
            designDAO.addDesign(design);
        }

        // 2. Create Variant if it doesn't already exist
        if (!variantDAO.exists(variantCode)) {
            Variant v = new Variant(variantCode, designNo, gender, size, length, colourCode);
            variantDAO.addVariant(v);
        }

        // 3. Persist or increment stock line
        boolean success = stockDAO.addOrUpdateStock(variantCode, quantity, vendorPrice, mrp, conditionId, note);

        if (success) {
            request.getSession().setAttribute("flashSuccess", "Stock saved successfully! Variant Code: " + variantCode);
            response.sendRedirect(request.getContextPath() + "/stock?success=true");
        } else {
            forwardWithError(request, response, "Failed to save stock record to the database.");
        }
    }

    private void forwardWithError(HttpServletRequest request, HttpServletResponse response, String errorMsg) throws ServletException, IOException {
        request.setAttribute("error", errorMsg);
        loadFormData(request);
        request.getRequestDispatcher("/stockUpdate.jsp").forward(request, response);
    }

    private void loadFormData(HttpServletRequest request) {
        List<Design> designs = designDAO.getAllDesigns();
        List<Colour> colours = masterDAO.getAllColours();
        List<StockCondition> conditions = masterDAO.getAllConditions();

        request.setAttribute("designs", designs);
        request.setAttribute("colours", colours);
        request.setAttribute("conditions", conditions);
    }
}
