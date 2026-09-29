<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<header>
    <nav class="navbar">
        <a href="${pageContext.request.contextPath}/dashboard" class="brand">
            <span>RETAIL IMS</span>
            <span class="brand-badge">Apparel &amp; Innerwear</span>
        </a>
        <ul class="nav-links">
            <li><a href="${pageContext.request.contextPath}/dashboard" class="${pageContext.request.requestURI.endsWith('dashboard.jsp') ? 'active' : ''}">Dashboard</a></li>
            <li><a href="${pageContext.request.contextPath}/stock" class="${pageContext.request.requestURI.endsWith('stockUpdate.jsp') ? 'active' : ''}">Stock Update</a></li>
            <li><a href="${pageContext.request.contextPath}/search" class="${pageContext.request.requestURI.endsWith('search.jsp') ? 'active' : ''}">Search Inventory</a></li>
            <li><a href="${pageContext.request.contextPath}/import" class="${pageContext.request.requestURI.endsWith('import.jsp') ? 'active' : ''}">Vendor Import & Scanner</a></li>
            <li><a href="${pageContext.request.contextPath}/label" class="${pageContext.request.requestURI.endsWith('label.jsp') ? 'active' : ''}">Barcode Labels</a></li>
            <li><a href="${pageContext.request.contextPath}/billing" class="${pageContext.request.requestURI.endsWith('billing.jsp') ? 'active' : ''}">POS Billing</a></li>
            <li><a href="${pageContext.request.contextPath}/sales-history" class="${pageContext.request.requestURI.endsWith('salesHistory.jsp') ? 'active' : ''}">Sales History</a></li>
            <li><a href="${pageContext.request.contextPath}/reports" class="${pageContext.request.requestURI.endsWith('reports.jsp') ? 'active' : ''}">Reports & Alerts</a></li>
            <c:if test="${sessionScope.currentUser != null && sessionScope.currentUser.owner}">
                <li><a href="${pageContext.request.contextPath}/masters" class="${pageContext.request.requestURI.endsWith('masters.jsp') ? 'active' : ''}">Masters</a></li>
            </c:if>
        </ul>
        <div class="user-info">
            <c:choose>
                <c:when test="${sessionScope.currentUser != null}">
                    <span>${sessionScope.currentUser.fullName}</span>
                    <c:choose>
                        <c:when test="${sessionScope.currentUser.owner}">
                            <span class="role-badge role-owner">OWNER</span>
                            <a href="${pageContext.request.contextPath}/backup" class="btn btn-outline btn-sm no-print" title="Backup Database SQL">Export DB</a>
                        </c:when>
                        <c:otherwise>
                            <span class="role-badge role-staff">STAFF</span>
                        </c:otherwise>
                    </c:choose>
                    <a href="${pageContext.request.contextPath}/logout" class="btn-logout no-print">Logout</a>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/login" class="btn btn-primary btn-sm">Login</a>
                </c:otherwise>
            </c:choose>
        </div>
    </nav>
</header>
