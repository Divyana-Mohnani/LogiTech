package com.retail.inventory.controller;

import com.retail.inventory.dao.StockDAO;
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
 * Controller for the main executive dashboard displaying stock overview and alerts.
 */
@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet {

    private final StockDAO stockDAO = new StockDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, Object> metrics = stockDAO.getInventoryMetrics();
        List<StockItemView> recentStock = stockDAO.searchStock(null, null, null, null, null);
        List<StockItemView> problemStock = stockDAO.getProblemStock(null);

        // Limit recent stock to top 8 items
        if (recentStock.size() > 8) {
            recentStock = recentStock.subList(0, 8);
        }
        if (problemStock.size() > 5) {
            problemStock = problemStock.subList(0, 5);
        }

        request.setAttribute("metrics", metrics);
        request.setAttribute("recentStock", recentStock);
        request.setAttribute("problemStock", problemStock);

        request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
