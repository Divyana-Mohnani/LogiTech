package com.retail.inventory.controller;

import com.retail.inventory.service.BarcodeService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Directly streams a Code 128 PNG image for any given variant code.
 * Example usage: &lt;img src="/inventory/barcode-image?code=M/L/B/BK"&gt;
 */
@WebServlet(name = "BarcodeImageServlet", urlPatterns = {"/barcode-image"})
public class BarcodeImageServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String code = request.getParameter("code");
        String widthStr = request.getParameter("w");
        String heightStr = request.getParameter("h");

        if (code == null || code.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing barcode parameter");
            return;
        }

        int width = 250;
        int height = 70;
        try {
            if (widthStr != null) width = Integer.parseInt(widthStr);
            if (heightStr != null) height = Integer.parseInt(heightStr);
        } catch (NumberFormatException ignored) {}

        try {
            byte[] imageBytes = BarcodeService.generateBarcodeImage(code.trim(), width, height);
            response.setContentType("image/png");
            response.setContentLength(imageBytes.length);
            try (OutputStream out = response.getOutputStream()) {
                out.write(imageBytes);
                out.flush();
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generating barcode: " + e.getMessage());
        }
    }
}
