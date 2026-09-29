package com.retail.inventory.filter;

import com.retail.inventory.model.User;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Authentication and Role-Based Authorization Filter.
 * Verifies active session and prevents STAFF users from accessing OWNER-only areas (masters, backup).
 */
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String path = uri.substring(contextPath.length());

        // Allow public static resources and login/logout endpoints
        if (path.startsWith("/static/") ||
            path.equals("/login") ||
            path.equals("/login.jsp") ||
            path.equals("/logout") ||
            path.equals("/index.jsp") ||
            path.equals("/")) {
            chain.doFilter(req, res);
            return;
        }

        // Check active session
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (user == null) {
            response.sendRedirect(contextPath + "/login");
            return;
        }

        // Role-based restrictions: STAFF cannot access Master data management or Database Backup
        if (path.startsWith("/masters") || path.startsWith("/backup")) {
            if (!user.isOwner()) {
                request.setAttribute("errorMessage", "Access Denied: Master data and system backup require OWNER privileges.");
                request.getRequestDispatcher("/dashboard").forward(request, response);
                return;
            }
        }

        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {
    }
}
