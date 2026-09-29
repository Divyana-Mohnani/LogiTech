package com.retail.inventory.controller;

import com.retail.inventory.dao.DesignDAO;
import com.retail.inventory.dao.MasterDAO;
import com.retail.inventory.model.Colour;
import com.retail.inventory.model.Design;
import com.retail.inventory.model.StockCondition;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Controller for Master Data Management (Designs, Colours, Conditions).
 * Restricted to OWNER role by AuthFilter.
 */
@WebServlet(name = "MasterServlet", urlPatterns = {"/masters"})
public class MasterServlet extends HttpServlet {

    private final DesignDAO designDAO = new DesignDAO();
    private final MasterDAO masterDAO = new MasterDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        loadData(request);
        request.getRequestDispatcher("/masters.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("addColour".equals(action)) {
            String code = request.getParameter("colourCode");
            String name = request.getParameter("colourName");
            if (code != null && name != null && !code.trim().isEmpty() && !name.trim().isEmpty()) {
                masterDAO.addColour(new Colour(code.trim().toUpperCase(), name.trim()));
                request.setAttribute("successMessage", "Colour " + name + " (" + code.toUpperCase() + ") added successfully.");
            } else {
                request.setAttribute("errorMessage", "Colour code and name are required.");
            }
        } else if ("addDesign".equals(action)) {
            String designNo = request.getParameter("designNo");
            String designName = request.getParameter("designName");
            String category = request.getParameter("category");
            String description = request.getParameter("description");

            if (designNo != null && designName != null && category != null &&
                !designNo.trim().isEmpty() && !designName.trim().isEmpty() && !category.trim().isEmpty()) {
                Design d = new Design(designNo.trim().toUpperCase(), designName.trim(), category.trim(), description);
                boolean added = designDAO.addDesign(d);
                if (added) {
                    request.setAttribute("successMessage", "Design " + designNo.toUpperCase() + " added successfully.");
                } else {
                    request.setAttribute("errorMessage", "Failed to add design. It may already exist.");
                }
            } else {
                request.setAttribute("errorMessage", "Design No, Name, and Category are required.");
            }
        }

        loadData(request);
        request.getRequestDispatcher("/masters.jsp").forward(request, response);
    }

    private void loadData(HttpServletRequest request) {
        List<Design> designs = designDAO.getAllDesigns();
        List<Colour> colours = masterDAO.getAllColours();
        List<StockCondition> conditions = masterDAO.getAllConditions();

        request.setAttribute("designs", designs);
        request.setAttribute("colours", colours);
        request.setAttribute("conditions", conditions);
    }
}
