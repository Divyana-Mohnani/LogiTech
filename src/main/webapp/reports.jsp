<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reports &amp; Condition Analytics - Retail Apparel</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/app.css">
</head>
<body>
    <jsp:include page="/WEB-INF/common/navbar.jsp" />

    <main class="main-container">
        <div class="page-title-bar">
            <div>
                <h1 class="page-title">Stock Valuation &amp; Condition Analytics</h1>
                <p class="page-subtitle">Identify defective, old season, and dead stock lines to make informed clearance decisions</p>
            </div>
            <button onclick="window.print()" class="btn btn-outline no-print">🖨️ Print Report</button>
        </div>

        <!-- Inventory Condition Breakdown Cards -->
        <section class="metrics-grid">
            <div class="metric-card normal">
                <div class="metric-label">Normal Stock</div>
                <div class="metric-value">${metrics.normalItems} <span style="font-size: 0.95rem; font-weight: normal;">units</span></div>
                <div class="metric-sub">Full retail price</div>
            </div>
            <div class="metric-card defective">
                <div class="metric-label">Defective Stock</div>
                <div class="metric-value" style="color: var(--danger);">${metrics.defectiveItems} <span style="font-size: 0.95rem; font-weight: normal;">units</span></div>
                <div class="metric-sub">Stitching flaw, damage, repair</div>
            </div>
            <div class="metric-card old">
                <div class="metric-label">Old Season Stock</div>
                <div class="metric-value" style="color: var(--warning);">${metrics.oldItems} <span style="font-size: 0.95rem; font-weight: normal;">units</span></div>
                <div class="metric-sub">Earlier lots / seasonal carryover</div>
            </div>
            <div class="metric-card dead">
                <div class="metric-label">Dead Stock Locked Value</div>
                <div class="metric-value" style="color: var(--dead-stock);">₹<fmt:formatNumber value="${metrics.deadStockValue}" pattern="#,##0.00"/></div>
                <div class="metric-sub">${metrics.deadItems} units needing clearance</div>
            </div>
        </section>

        <!-- Condition Filter Tabs -->
        <div class="card no-print" style="padding: 16px;">
            <div style="display: flex; gap: 10px; align-items: center; flex-wrap: wrap;">
                <span style="font-weight: 600; font-size: 0.9rem; color: #475569;">Filter Problem Stock:</span>
                <a href="${pageContext.request.contextPath}/reports" class="btn ${empty selectedCondition ? 'btn-primary' : 'btn-secondary'} btn-sm">All Problem Lines</a>
                <a href="${pageContext.request.contextPath}/reports?condition=2" class="btn ${selectedCondition == 2 ? 'btn-danger' : 'btn-secondary'} btn-sm">Defective Only (Flaws/Damage)</a>
                <a href="${pageContext.request.contextPath}/reports?condition=3" class="btn ${selectedCondition == 3 ? 'btn-primary' : 'btn-secondary'} btn-sm">Old Season Stock</a>
                <a href="${pageContext.request.contextPath}/reports?condition=4" class="btn ${selectedCondition == 4 ? 'btn-primary' : 'btn-secondary'} btn-sm" style="${selectedCondition == 4 ? 'background: var(--dead-stock);' : ''}">Dead Stock (Unwanted)</a>
            </div>
        </div>

        <!-- Problem Stock Table -->
        <div class="card">
            <h2 class="card-title">Problem Stock Action List (${problemLines.size()} lines identified)</h2>
            <div class="table-responsive">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Variant Code</th>
                            <th>Design</th>
                            <th>Category</th>
                            <th>Size / Col</th>
                            <th>Qty</th>
                            <th>Cost</th>
                            <th>MRP</th>
                            <th>Condition</th>
                            <th>Typical Action</th>
                            <th>Special Remark / Preference</th>
                            <th>Print Label</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="line" items="${problemLines}">
                            <tr>
                                <td><span class="code-pill">${line.variantCode}</span></td>
                                <td>${line.designNo} - ${line.designName}</td>
                                <td>${line.category}</td>
                                <td>${line.size} / ${line.colourName}</td>
                                <td><strong style="font-size: 1.05rem;">${line.quantity}</strong></td>
                                <td>₹${line.vendorPrice}</td>
                                <td><strong>₹${line.mrp}</strong></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${line.conditionId == 2}"><span class="badge badge-defective">Defective</span></c:when>
                                        <c:when test="${line.conditionId == 3}"><span class="badge badge-old">Old Season</span></c:when>
                                        <c:when test="${line.conditionId == 4}"><span class="badge badge-dead">Dead Stock</span></c:when>
                                        <c:otherwise><span class="badge">${line.conditionName}</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${line.conditionId == 2}">
                                            <span style="color: #b91c1c; font-weight: 600; font-size: 0.85rem;">Return to vendor or repair</span>
                                        </c:when>
                                        <c:when test="${line.conditionId == 3}">
                                            <span style="color: #b45309; font-weight: 600; font-size: 0.85rem;">Sell first or apply 20% discount</span>
                                        </c:when>
                                        <c:when test="${line.conditionId == 4}">
                                            <span style="color: #6b21a8; font-weight: 600; font-size: 0.85rem;">Clear at markdown / stop reorder</span>
                                        </c:when>
                                        <c:otherwise>Sell at regular price</c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty line.note}">
                                            <span style="font-style: italic; color: #1e293b;">${line.note}</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span style="color: #94a3b8;">None recorded</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/label?stockId=${line.stockId}" class="btn btn-outline btn-sm">🏷️ Label</a>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty problemLines}">
                            <tr>
                                <td colspan="11" style="text-align: center; color: var(--text-muted); padding: 30px;">
                                    No problem stock lines currently found. All inventory is in normal condition!
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
        <p>Retail Apparel Inventory Management System &bull; B.Tech Final Year Project</p>
    </footer>
</body>
</html>
