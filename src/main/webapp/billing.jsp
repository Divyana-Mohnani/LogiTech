<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>POS Counter Billing - Retail Apparel</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/app.css">
    <style>
        .pos-grid {
            display: grid;
            grid-template-columns: 2fr 1fr;
            gap: 24px;
        }
        @media (max-width: 900px) {
            .pos-grid {
                grid-template-columns: 1fr;
            }
        }
        .pos-scanner-box {
            background: #f8fafc;
            border: 2px solid #0284c7;
            border-radius: 8px;
            padding: 16px;
            margin-bottom: 20px;
        }
        .checkout-summary {
            background: #ffffff;
            border: 1px solid var(--border);
            border-radius: 8px;
            padding: 24px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.05);
            position: sticky;
            top: 20px;
        }
        .summary-row {
            display: flex;
            justify-content: space-between;
            margin-bottom: 10px;
            font-size: 0.95rem;
        }
        .summary-total {
            display: flex;
            justify-content: space-between;
            font-size: 1.4rem;
            font-weight: 800;
            color: #0f172a;
            border-top: 2px solid #e2e8f0;
            padding-top: 14px;
            margin-top: 14px;
        }
        .discount-pill {
            cursor: pointer;
            padding: 3px 8px;
            border-radius: 4px;
            background: #e2e8f0;
            font-size: 0.8rem;
            border: none;
            margin-right: 4px;
        }
        .discount-pill:hover {
            background: #cbd5e1;
        }
    </style>
</head>
<body>
    <jsp:include page="/WEB-INF/common/navbar.jsp" />

    <main class="main-container">
        <div class="page-title-bar">
            <div>
                <h1 class="page-title">POS Counter Billing Station</h1>
                <p class="page-subtitle">Scan item barcodes to cart &bull; Automatic stock reduction upon checkout</p>
            </div>
            <div style="display: flex; gap: 10px;">
                <a href="${pageContext.request.contextPath}/sales-history" class="btn btn-outline">📜 Sales Register</a>
                <a href="${pageContext.request.contextPath}/search" class="btn btn-outline">🔍 Search Stock</a>
            </div>
        </div>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <c:if test="${not empty sessionScope.cartError}">
            <div class="alert alert-danger">
                ${sessionScope.cartError}
                <c:remove var="cartError" scope="session" />
            </div>
        </c:if>

        <c:if test="${not empty sessionScope.cartSuccess}">
            <div class="alert alert-success">
                ${sessionScope.cartSuccess}
                <c:remove var="cartSuccess" scope="session" />
            </div>
        </c:if>

        <div class="pos-grid">
            <!-- Left: Barcode Scanner & Active Cart Items -->
            <div>
                <!-- Barcode Scan Form -->
                <div class="pos-scanner-box">
                    <form action="${pageContext.request.contextPath}/billing" method="post" style="display: flex; gap: 12px; align-items: center;">
                        <input type="hidden" name="action" value="addItem">
                        <div style="flex: 2;">
                            <label style="font-weight: 700; font-size: 0.88rem; color: #0369a1; display: block; margin-bottom: 4px;">
                                📷 Scan Barcode / Enter Variant Code:
                            </label>
                            <input type="text" name="barcode" class="form-control"
                                   placeholder="Scan tag or type M/L/B/BK..."
                                   autofocus autocomplete="off" required style="font-size: 1.1rem; font-family: monospace;">
                        </div>
                        <div style="width: 90px;">
                            <label style="font-weight: 600; font-size: 0.88rem; display: block; margin-bottom: 4px;">Qty:</label>
                            <input type="number" name="quantity" class="form-control" value="1" min="1" style="font-size: 1.05rem;">
                        </div>
                        <div style="width: 120px;">
                            <label style="font-weight: 600; font-size: 0.88rem; display: block; margin-bottom: 4px;">Discount %:</label>
                            <input type="number" step="1" name="discountPercent" class="form-control" value="0" min="0" max="100">
                        </div>
                        <div style="padding-top: 22px;">
                            <button type="submit" class="btn btn-primary" style="padding: 10px 20px;">+ Add Item</button>
                        </div>
                    </form>
                    <div style="margin-top: 8px; font-size: 0.78rem; color: #64748b;">
                        Quick discounts:
                        <span class="discount-pill" onclick="document.querySelector('input[name=discountPercent]').value='0'">0%</span>
                        <span class="discount-pill" onclick="document.querySelector('input[name=discountPercent]').value='10'">10% Regular</span>
                        <span class="discount-pill" onclick="document.querySelector('input[name=discountPercent]').value='20'">20% Old Season</span>
                        <span class="discount-pill" onclick="document.querySelector('input[name=discountPercent]').value='50'">50% Dead Stock Clearance</span>
                    </div>
                </div>

                <!-- Current Bill Cart Table -->
                <div class="card">
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
                        <h2 class="card-title" style="margin-bottom: 0; border: none;">
                            Billed Items (${sessionScope.posCart.items.size()} items in cart)
                        </h2>
                        <c:if test="${not empty sessionScope.posCart.items}">
                            <form action="${pageContext.request.contextPath}/billing" method="post" style="margin: 0;">
                                <input type="hidden" name="action" value="clearCart">
                                <button type="submit" class="btn btn-outline btn-sm" onclick="return confirm('Clear entire cart?');">Clear Cart</button>
                            </form>
                        </c:if>
                    </div>

                    <div class="table-responsive">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>#</th>
                                    <th>Variant Code</th>
                                    <th>Description</th>
                                    <th>Size / Col</th>
                                    <th>Unit MRP</th>
                                    <th>Qty</th>
                                    <th>Discount</th>
                                    <th>Line Total</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="item" items="${sessionScope.posCart.items}" varStatus="status">
                                    <tr>
                                        <td>${status.index + 1}</td>
                                        <td><span class="code-pill">${item.variantCode}</span></td>
                                        <td><strong>${item.designName}</strong></td>
                                        <td>${item.size} / ${item.colourName}</td>
                                        <td>₹${item.unitMrp}</td>
                                        <td><strong style="font-size: 1.05rem;">${item.quantity}</strong></td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${item.discountPercent > 0}">
                                                    <span class="badge badge-old">${item.discountPercent}% OFF</span>
                                                </c:when>
                                                <c:otherwise>0%</c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td><strong>₹${item.lineTotal}</strong></td>
                                        <td>
                                            <form action="${pageContext.request.contextPath}/billing" method="post" style="margin: 0;">
                                                <input type="hidden" name="action" value="removeItem">
                                                <input type="hidden" name="index" value="${status.index}">
                                                <button type="submit" class="btn btn-danger btn-sm" title="Remove Item">✕</button>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty sessionScope.posCart.items}">
                                    <tr>
                                        <td colspan="9" style="text-align: center; padding: 40px; color: var(--text-muted);">
                                            No items in current bill. Scan a product barcode or enter a variant code above to start billing.
                                        </td>
                                    </tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <!-- Right: Customer Info & Checkout Payment Summary -->
            <div>
                <div class="checkout-summary">
                    <h2 style="font-size: 1.15rem; font-weight: 700; color: #0f172a; margin-bottom: 16px; border-bottom: 1px solid #e2e8f0; padding-bottom: 10px;">
                        Customer &amp; Payment
                    </h2>

                    <form action="${pageContext.request.contextPath}/billing" method="post">
                        <input type="hidden" name="action" value="checkout">

                        <div class="form-group">
                            <label for="custName">Customer Name</label>
                            <input type="text" id="custName" name="customerName" class="form-control" placeholder="Walk-in Customer" value="${sessionScope.posCart.customerName}">
                        </div>

                        <div class="form-group">
                            <label for="custPhone">Customer Mobile</label>
                            <input type="text" id="custPhone" name="customerPhone" class="form-control" placeholder="10-digit mobile" maxlength="15">
                        </div>

                        <div class="form-group">
                            <label for="payMode">Payment Mode</label>
                            <select id="payMode" name="paymentMode" class="form-control">
                                <option value="UPI" selected>UPI (GPay / PhonePe / Paytm)</option>
                                <option value="CASH">Cash</option>
                                <option value="CARD">Credit / Debit Card</option>
                            </select>
                        </div>

                        <div style="margin-top: 20px; padding-top: 14px; border-top: 1px dashed #cbd5e1;">
                            <div class="summary-row">
                                <span>Gross MRP Subtotal:</span>
                                <span>₹<fmt:formatNumber value="${sessionScope.posCart.subtotal}" pattern="#,##0.00"/></span>
                            </div>
                            <div class="summary-row" style="color: #15803d;">
                                <span>Total Savings / Discount:</span>
                                <span>- ₹<fmt:formatNumber value="${sessionScope.posCart.discountAmount}" pattern="#,##0.00"/></span>
                            </div>
                            <div class="summary-row" style="color: #64748b;">
                                <span>GST (5% Apparel Tax):</span>
                                <span>+ ₹<fmt:formatNumber value="${sessionScope.posCart.taxAmount}" pattern="#,##0.00"/></span>
                            </div>
                            <div class="summary-total">
                                <span>Net Total:</span>
                                <span>₹<fmt:formatNumber value="${sessionScope.posCart.netTotal}" pattern="#,##0.00"/></span>
                            </div>
                        </div>

                        <button type="submit" class="btn btn-success" style="width: 100%; margin-top: 22px; padding: 14px; font-size: 1.1rem; font-weight: 700;"
                                ${empty sessionScope.posCart.items ? 'disabled' : ''}>
                            💳 Complete Sale &amp; Print Receipt
                        </button>
                    </form>
                </div>
            </div>
        </div>
    </main>

    <footer>
        <div class="footer-divider">✦ ✦ ✦</div>
        <p>Retail Apparel Inventory Management System &bull; POS Retail Module &bull; B.Tech Final Year Project</p>
    </footer>
</body>
</html>
