<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sales Register - Retail Apparel Inventory</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/app.css">
</head>
<body>
    <jsp:include page="/WEB-INF/common/navbar.jsp" />

    <main class="main-container">
        <div class="page-title-bar">
            <div>
                <h1 class="page-title">Store Sales Register</h1>
                <p class="page-subtitle">Track daily counter revenue, order transactions, and reprint receipts</p>
            </div>
            <a href="${pageContext.request.contextPath}/billing" class="btn btn-primary">+ New Customer Bill</a>
        </div>

        <!-- Today's Performance Summary -->
        <section class="metrics-grid">
            <div class="metric-card normal">
                <div class="metric-label">Today's Revenue</div>
                <div class="metric-value">₹<fmt:formatNumber value="${summary.todayRevenue}" pattern="#,##0.00"/></div>
                <div class="metric-sub">Across all payment modes</div>
            </div>
            <div class="metric-card">
                <div class="metric-label">Today's Invoices</div>
                <div class="metric-value">${summary.todayBills}</div>
                <div class="metric-sub">Customer transactions completed</div>
            </div>
            <div class="metric-card">
                <div class="metric-label">Units Sold Today</div>
                <div class="metric-value">${summary.todayUnits} <span style="font-size: 1rem; font-weight: normal;">pcs</span></div>
                <div class="metric-sub">Deducted from warehouse inventory</div>
            </div>
        </section>

        <!-- Sales Bills Table -->
        <div class="card">
            <h2 class="card-title">Recent Invoices (${recentBills.size()} bills recorded)</h2>
            <div class="table-responsive">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Invoice No</th>
                            <th>Date &amp; Time</th>
                            <th>Customer</th>
                            <th>Phone</th>
                            <th>Payment Mode</th>
                            <th>Cashier</th>
                            <th>Net Total</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="b" items="${recentBills}">
                            <tr>
                                <td><span class="code-pill">${b.billNo}</span></td>
                                <td><fmt:formatDate value="${b.billDate}" pattern="dd-MMM-yyyy HH:mm"/></td>
                                <td><strong>${b.customerName}</strong></td>
                                <td>${not empty b.customerPhone ? b.customerPhone : '-'}</td>
                                <td>
                                    <span class="badge ${b.paymentMode == 'UPI' ? 'badge-normal' : (b.paymentMode == 'CARD' ? 'badge-dead' : 'badge-old')}">
                                        ${b.paymentMode}
                                    </span>
                                </td>
                                <td>${b.cashierName}</td>
                                <td><strong style="font-size: 1.05rem;">₹${b.netTotal}</strong></td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/receipt?billNo=${b.billNo}" class="btn btn-outline btn-sm">
                                        🧾 View Receipt
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty recentBills}">
                            <tr>
                                <td colspan="8" style="text-align: center; color: var(--text-muted); padding: 30px;">
                                    No sales invoices recorded yet. Open the <a href="${pageContext.request.contextPath}/billing">POS Counter</a> to create your first bill!
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
