<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - Retail Apparel Inventory</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/app.css">
</head>
<body>
    <jsp:include page="/WEB-INF/common/navbar.jsp" />

    <main class="main-container">
        <div class="page-title-bar">
            <div>
                <h1 class="page-title">Store Inventory Dashboard</h1>
                <p class="page-subtitle">Real-time apparel variant tracking &amp; condition monitoring</p>
            </div>
            <div style="display: flex; gap: 10px;">
                <a href="${pageContext.request.contextPath}/stock" class="btn btn-primary">+ New Stock Entry</a>
                <a href="${pageContext.request.contextPath}/import" class="btn btn-outline">Import Vendor File</a>
            </div>
        </div>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger">${errorMessage}</div>
        </c:if>

        <!-- KPI Metrics Grid -->
        <section class="metrics-grid">
            <div class="metric-card">
                <div class="metric-label">Total Designs</div>
                <div class="metric-value">${metrics.totalDesigns}</div>
                <div class="metric-sub">Parent design models</div>
            </div>
            <div class="metric-card">
                <div class="metric-label">Tracked Variants</div>
                <div class="metric-value">${metrics.totalVariants}</div>
                <div class="metric-sub">Active color/size SKUs</div>
            </div>
            <div class="metric-card normal">
                <div class="metric-label">Stock in Hand</div>
                <div class="metric-value">${metrics.totalItems} <span style="font-size: 1rem; font-weight: normal;">units</span></div>
                <div class="metric-sub">${metrics.normalItems} units normal sellable</div>
            </div>
            <div class="metric-card">
                <div class="metric-label">Inventory Retail Value</div>
                <div class="metric-value">₹<fmt:formatNumber value="${metrics.totalMrpValue}" pattern="#,##0.00"/></div>
                <div class="metric-sub">Cost: ₹<fmt:formatNumber value="${metrics.totalCostValue}" pattern="#,##0.00"/></div>
            </div>
            <div class="metric-card dead">
                <div class="metric-label">Dead Stock Alert</div>
                <div class="metric-value" style="color: var(--dead-stock);">${metrics.deadItems} <span style="font-size: 1rem; font-weight: normal;">units</span></div>
                <div class="metric-sub">₹<fmt:formatNumber value="${metrics.deadStockValue}" pattern="#,##0.00"/> locked in dead stock</div>
            </div>
        </section>

        <!-- Problem Stock Warning Banner if any -->
        <c:if test="${metrics.defectiveItems > 0 || metrics.deadItems > 0 || metrics.oldItems > 0}">
            <div class="card" style="border-left: 5px solid var(--warning); background: #fffcf5;">
                <div style="display: flex; justify-content: space-between; align-items: center;">
                    <div>
                        <h2 style="font-size: 1.05rem; font-weight: 700; color: #92400e; margin-bottom: 4px;">
                            Action Required: Attention Needed on Problem Stock
                        </h2>
                        <p style="font-size: 0.88rem; color: #78350f;">
                            You have <strong>${metrics.defectiveItems} defective</strong>, <strong>${metrics.oldItems} old season</strong>, and <strong>${metrics.deadItems} dead stock</strong> items recorded.
                        </p>
                    </div>
                    <a href="${pageContext.request.contextPath}/reports" class="btn btn-sm btn-secondary">Review &amp; Clear Stock &rarr;</a>
                </div>
            </div>
        </c:if>

        <!-- Recent Stock Table -->
        <div class="card">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
                <h2 class="card-title" style="margin-bottom: 0; border: none;">Recent Stock Entries</h2>
                <a href="${pageContext.request.contextPath}/search" style="font-size: 0.88rem; font-weight: 600; color: var(--primary);">View All &rarr;</a>
            </div>

            <div class="table-responsive">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Variant Code (SKU)</th>
                            <th>Design</th>
                            <th>Category</th>
                            <th>Gender</th>
                            <th>Size</th>
                            <th>Length</th>
                            <th>Colour</th>
                            <th>Quantity</th>
                            <th>MRP</th>
                            <th>Condition</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="item" items="${recentStock}">
                            <tr>
                                <td><span class="code-pill">${item.variantCode}</span></td>
                                <td><strong>${item.designNo}</strong> - ${item.designName}</td>
                                <td>${item.category}</td>
                                <td>${item.genderLabel}</td>
                                <td>${item.size}</td>
                                <td>${item.lengthLabel}</td>
                                <td>${item.colourName}</td>
                                <td><strong>${item.quantity}</strong></td>
                                <td>₹${item.mrp}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${item.conditionId == 1}"><span class="badge badge-normal">Normal</span></c:when>
                                        <c:when test="${item.conditionId == 2}"><span class="badge badge-defective">Defective</span></c:when>
                                        <c:when test="${item.conditionId == 3}"><span class="badge badge-old">Old</span></c:when>
                                        <c:when test="${item.conditionId == 4}"><span class="badge badge-dead">Dead Stock</span></c:when>
                                        <c:otherwise><span class="badge">${item.conditionName}</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/label?stockId=${item.stockId}" class="btn btn-outline btn-sm" title="Print Barcode Label">🏷️ Print</a>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if empty="${recentStock}">
                            <tr>
                                <td colspan="11" style="text-align: center; color: var(--text-muted); padding: 30px;">
                                    No stock items recorded yet. <a href="${pageContext.request.contextPath}/stock">Add your first stock entry</a>.
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </main>

    <footer>
        <div class="footer-divider">✦ ✦ ✦</div>
        <p>Retail Apparel Inventory Management System &bull; B.Tech Final Year Project &bull; Rajasthan Technical University</p>
    </footer>
</body>
</html>
