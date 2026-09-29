package com.retail.inventory.controller;

import com.retail.inventory.dao.MasterDAO;
import com.retail.inventory.dao.StockDAO;
import com.retail.inventory.model.StockCondition;
import com.retail.inventory.model.StockItemView;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Controller for executive reports and condition analytics (Defective, Old, Dead Stock).
 */
@WebServlet(name = "ReportServlet", urlPatterns = {"/reports"})
public class ReportServlet extends HttpServlet {

    private final StockDAO stockDAO = new StockDAO();
    private final MasterDAO masterDAO = new MasterDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String conditionFilterStr = request.getParameter("condition");
        Integer conditionFilter = null;
        if (conditionFilterStr != null && !conditionFilterStr.trim().isEmpty()) {
            try {
                conditionFilter = Integer.parseInt(conditionFilterStr.trim());
            } catch (NumberFormatException ignored) {}
        }

        Map<String, Object> metrics = stockDAO.getInventoryMetrics();
        List<StockItemView> problemLines = stockDAO.getProblemStock(conditionFilter);
        List<StockCondition> conditions = masterDAO.getAllConditions();

        request.setAttribute("metrics", metrics);
        request.setAttribute("problemLines", problemLines);
        request.setAttribute("conditions", conditions);
        request.setAttribute("selectedCondition", conditionFilter);

        request.getRequestDispatcher("/reports.jsp").forward(request, response);
    }
}
