package com.retail.inventory.controller;

import com.google.gson.Gson;
import com.retail.inventory.dao.MasterDAO;
import com.retail.inventory.dao.StockDAO;
import com.retail.inventory.model.Colour;
import com.retail.inventory.model.StockCondition;
import com.retail.inventory.model.StockItemView;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Controller for searching inventory by variant code, design number, category, gender, or condition.
 * Supports standard table filtering and quick AJAX lookups from barcode scanners.
 */
@WebServlet(name = "SearchServlet", urlPatterns = {"/search"})
public class SearchServlet extends HttpServlet {

    private final StockDAO stockDAO = new StockDAO();
    private final MasterDAO masterDAO = new MasterDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        // Fast AJAX barcode scanner lookup
        if ("scannerLookup".equals(action)) {
            String code = request.getParameter("code");
            response.setContentType("application/json;charset=UTF-8");
            if (code != null && !code.trim().isEmpty()) {
                StockItemView item = stockDAO.findByVariantCode(code.trim());
                response.getWriter().write(gson.toJson(item));
            } else {
                response.getWriter().write("{}");
            }
            return;
        }

        String q = request.getParameter("q");
        String category = request.getParameter("category");
        String gender = request.getParameter("gender");
        String conditionStr = request.getParameter("conditionId");
        String colourCode = request.getParameter("colourCode");

        Integer conditionId = null;
        if (conditionStr != null && !conditionStr.trim().isEmpty()) {
            try {
                conditionId = Integer.parseInt(conditionStr.trim());
            } catch (NumberFormatException ignored) {}
        }

        List<StockItemView> results = stockDAO.searchStock(q, category, gender, conditionId, colourCode);
        List<Colour> colours = masterDAO.getAllColours();
        List<StockCondition> conditions = masterDAO.getAllConditions();

        request.setAttribute("results", results);
        request.setAttribute("colours", colours);
        request.setAttribute("conditions", conditions);
        request.setAttribute("selectedCategory", category);
        request.setAttribute("selectedGender", gender);
        request.setAttribute("selectedCondition", conditionId);
        request.setAttribute("selectedColour", colourCode);
        request.setAttribute("query", q);

        request.getRequestDispatcher("/search.jsp").forward(request, response);
    }
}
