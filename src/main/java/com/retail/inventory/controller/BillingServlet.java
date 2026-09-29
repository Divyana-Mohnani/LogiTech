package com.retail.inventory.controller;

import com.google.gson.Gson;
import com.retail.inventory.dao.SalesDAO;
import com.retail.inventory.dao.StockDAO;
import com.retail.inventory.model.SalesBill;
import com.retail.inventory.model.SalesItem;
import com.retail.inventory.model.StockItemView;
import com.retail.inventory.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for POS Counter Billing & Real-time stock reduction checkout.
 */
@WebServlet(name = "BillingServlet", urlPatterns = {"/billing"})
public class BillingServlet extends HttpServlet {

    private final StockDAO stockDAO = new StockDAO();
    private final SalesDAO salesDAO = new SalesDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        // Fast AJAX Barcode scanner response
        if ("scanLookup".equals(action)) {
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

        // Ensure session cart exists
        getOrCreateCart(request.getSession());

        request.getRequestDispatcher("/billing.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        SalesBill cart = getOrCreateCart(session);
        String action = request.getParameter("action");

        if ("addItem".equals(action)) {
            String barcode = request.getParameter("barcode");
            String qtyStr = request.getParameter("quantity");
            String discountStr = request.getParameter("discountPercent");

            if (barcode != null && !barcode.trim().isEmpty()) {
                StockItemView item = stockDAO.findByVariantCode(barcode.trim());
                if (item != null) {
                    int addQty = 1;
                    try {
                        if (qtyStr != null) addQty = Math.max(1, Integer.parseInt(qtyStr.trim()));
                    } catch (NumberFormatException ignored) {}

                    BigDecimal disc = BigDecimal.ZERO;
                    try {
                        if (discountStr != null) disc = new BigDecimal(discountStr.trim());
                    } catch (Exception ignored) {}

                    // Check if already in cart
                    boolean existingFound = false;
                    for (SalesItem si : cart.getItems()) {
                        if (si.getVariantCode().equalsIgnoreCase(item.getVariantCode())) {
                            si.setQuantity(si.getQuantity() + addQty);
                            si.setDiscountPercent(disc);
                            si.calculateLineTotal();
                            existingFound = true;
                            break;
                        }
                    }

                    if (!existingFound) {
                        SalesItem newItem = new SalesItem(item.getVariantCode(), item.getDesignName(), addQty, item.getMrp(), disc);
                        newItem.setSize(item.getSize());
                        newItem.setColourName(item.getColourName());
                        cart.addItem(newItem);
                    }

                    cart.recalculateTotals(new BigDecimal("5.00")); // Standard 5% GST for apparel
                    session.setAttribute("cartSuccess", "Added " + item.getVariantCode() + " to cart.");
                } else {
                    session.setAttribute("cartError", "Product with barcode '" + barcode + "' not found in inventory.");
                }
            }

        } else if ("removeItem".equals(action)) {
            String idxStr = request.getParameter("index");
            try {
                int idx = Integer.parseInt(idxStr);
                if (idx >= 0 && idx < cart.getItems().size()) {
                    cart.getItems().remove(idx);
                    cart.recalculateTotals(new BigDecimal("5.00"));
                }
            } catch (Exception ignored) {}

        } else if ("clearCart".equals(action)) {
            cart.getItems().clear();
            cart.recalculateTotals(new BigDecimal("5.00"));

        } else if ("checkout".equals(action)) {
            if (cart.getItems().isEmpty()) {
                request.setAttribute("error", "Cart is empty. Scan an item first.");
                request.getRequestDispatcher("/billing.jsp").forward(request, response);
                return;
            }

            String customerName = request.getParameter("customerName");
            String customerPhone = request.getParameter("customerPhone");
            String paymentMode = request.getParameter("paymentMode");

            cart.setCustomerName(customerName != null && !customerName.trim().isEmpty() ? customerName.trim() : "Walk-in Customer");
            cart.setCustomerPhone(customerPhone != null ? customerPhone.trim() : "");
            cart.setPaymentMode(paymentMode != null ? paymentMode : "CASH");

            User currentUser = (User) session.getAttribute("currentUser");
            if (currentUser != null) {
                cart.setCashierId(currentUser.getUserId());
            }

            try {
                // Perform atomic transactional checkout and stock deduction
                salesDAO.saveSalesBill(cart);

                String billedNo = cart.getBillNo();
                // Clear session cart
                session.removeAttribute("posCart");

                // Redirect to receipt view
                response.sendRedirect(request.getContextPath() + "/receipt?billNo=" + billedNo + "&newSale=true");
                return;

            } catch (Exception e) {
                request.setAttribute("error", "Checkout Failed: " + e.getMessage());
                request.getRequestDispatcher("/billing.jsp").forward(request, response);
                return;
            }
        }

        response.sendRedirect(request.getContextPath() + "/billing");
    }

    private SalesBill getOrCreateCart(HttpSession session) {
        SalesBill cart = (SalesBill) session.getAttribute("posCart");
        if (cart == null) {
            cart = new SalesBill();
            cart.recalculateTotals(new BigDecimal("5.00"));
            session.setAttribute("posCart", cart);
        }
        return cart;
    }
}
