package com.retail.inventory.controller;

import com.retail.inventory.service.BackupService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Controller for downloading database SQL backup.
 * Restricted to OWNER role.
 */
@WebServlet(name = "BackupServlet", urlPatterns = {"/backup"})
public class BackupServlet extends HttpServlet {

    private final BackupService backupService = new BackupService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String filename = "inventory_backup_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".sql";

        response.setContentType("application/sql");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        try (PrintWriter writer = response.getWriter()) {
            backupService.exportDatabaseAsSql(writer);
        }
    }
}
