package com.retail.inventory.controller;

import com.retail.inventory.dao.SalesDAO;
import com.retail.inventory.model.SalesBill;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Controller for viewing store sales registers and reprinting past invoices.
 */
@WebServlet(name = "SalesHistoryServlet", urlPatterns = {"/sales-history"})
public class SalesHistoryServlet extends HttpServlet {

    private final SalesDAO salesDAO = new SalesDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<SalesBill> recentBills = salesDAO.getRecentBills(50);
        Map<String, Object> summary = salesDAO.getDailySalesSummary();

        request.setAttribute("recentBills", recentBills);
        request.setAttribute("summary", summary);

        request.getRequestDispatcher("/salesHistory.jsp").forward(request, response);
    }
}
