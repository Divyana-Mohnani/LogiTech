<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sales Receipt - ${bill.billNo}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/app.css">
    <style>
        .receipt-container {
            max-width: 480px;
            margin: 20px auto;
            background: #ffffff;
            border: 1px solid #cbd5e1;
            box-shadow: 0 4px 12px rgba(0,0,0,0.06);
            padding: 28px;
            border-radius: 8px;
            font-family: -apple-system, BlinkMacSystemFont, "Courier New", Courier, monospace, sans-serif;
        }
        .receipt-header {
            text-align: center;
            border-bottom: 2px dashed #94a3b8;
            padding-bottom: 16px;
            margin-bottom: 16px;
        }
        .receipt-store-name {
            font-size: 1.4rem;
            font-weight: 800;
            color: #0b2545;
            letter-spacing: 1px;
        }
        .receipt-meta-row {
            display: flex;
            justify-content: space-between;
            font-size: 0.85rem;
            color: #475569;
            margin-bottom: 4px;
        }
        .receipt-table {
            width: 100%;
            border-collapse: collapse;
            font-size: 0.88rem;
            margin: 16px 0;
        }
        .receipt-table th {
            text-align: left;
            border-bottom: 1px solid #000;
            padding: 6px 4px;
            font-size: 0.8rem;
            text-transform: uppercase;
        }
        .receipt-table td {
            padding: 6px 4px;
            border-bottom: 1px dashed #e2e8f0;
        }
        .receipt-totals {
            border-top: 1px dashed #000;
            padding-top: 10px;
            margin-top: 10px;
        }
        .receipt-total-row {
            display: flex;
            justify-content: space-between;
            font-size: 0.9rem;
            margin-bottom: 5px;
        }
        .receipt-grand-total {
            font-size: 1.25rem;
            font-weight: 800;
            border-top: 2px solid #000;
            border-bottom: 2px solid #000;
            padding: 8px 0;
            margin: 8px 0;
            display: flex;
            justify-content: space-between;
        }
        .receipt-footer {
            text-align: center;
            margin-top: 20px;
            font-size: 0.75rem;
            color: #64748b;
            border-top: 1px dashed #cbd5e1;
            padding-top: 14px;
        }

        @media print {
            body {
                background: #fff !important;
            }
            .navbar, .no-print, footer {
                display: none !important;
            }
            .receipt-container {
                border: none !important;
                box-shadow: none !important;
                max-width: 100% !important;
                padding: 0 !important;
                margin: 0 !important;
            }
        }
    </style>
</head>
<body ${newSale ? 'onload="window.print()"' : ''}>
    <jsp:include page="/WEB-INF/common/navbar.jsp" />

    <main class="main-container">
        <div class="page-title-bar no-print">
            <div>
                <h1 class="page-title">Sales Invoice &amp; Tax Receipt</h1>
                <p class="page-subtitle">Invoice: <strong>${bill.billNo}</strong> &bull; Inventory automatically reduced</p>
            </div>
            <div style="display: flex; gap: 10px;">
                <button onclick="window.print()" class="btn btn-primary">🖨️ Print Receipt</button>
                <a href="${pageContext.request.contextPath}/billing" class="btn btn-success">+ New Bill</a>
                <a href="${pageContext.request.contextPath}/sales-history" class="btn btn-outline">Sales Register</a>
            </div>
        </div>

        <c:if test="${newSale}">
            <div class="alert alert-success no-print" style="max-width: 480px; margin: 0 auto 16px auto;">
                ✓ Sale processed successfully! Stock quantities have been updated in inventory.
            </div>
        </c:if>

        <div class="receipt-container">
            <!-- Header -->
            <div class="receipt-header">
                <div class="receipt-store-name">RETAIL APPAREL &amp; INNERWEAR</div>
                <div style="font-size: 0.8rem; color: #475569;">Flagship Store &bull; Outerwear, Lingerie &amp; Accessories</div>
                <div style="font-size: 0.75rem; color: #64748b;">Sitapura Industrial Area, Tonk Road, Jaipur</div>
                <div style="font-size: 0.75rem; color: #64748b; margin-top: 2px;">GSTIN: 08AAACR1234F1Z5 &bull; Phone: +91 141-2770232</div>
            </div>

            <!-- Meta details -->
            <div class="receipt-meta-row">
                <span><strong>Invoice:</strong> ${bill.billNo}</span>
                <span><strong>Date:</strong> <fmt:formatDate value="${bill.billDate}" pattern="dd-MMM-yyyy HH:mm"/></span>
            </div>
            <div class="receipt-meta-row">
                <span><strong>Customer:</strong> ${bill.customerName}</span>
                <span><strong>Phone:</strong> ${not empty bill.customerPhone ? bill.customerPhone : 'N/A'}</span>
            </div>
            <div class="receipt-meta-row">
                <span><strong>Cashier:</strong> ${not empty bill.cashierName ? bill.cashierName : 'Counter Staff'}</span>
                <span><strong>Payment:</strong> ${bill.paymentMode}</span>
            </div>

            <!-- Itemized Table -->
            <table class="receipt-table">
                <thead>
                    <tr>
                        <th>Item &amp; Code</th>
                        <th>Qty</th>
                        <th>MRP</th>
                        <th style="text-align: right;">Total</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="item" items="${bill.items}">
                        <tr>
                            <td>
                                <strong>${item.designName}</strong><br>
                                <span style="font-size: 0.75rem; color: #475569;">${item.variantCode} (${item.size}/${item.colourName})</span>
                                <c:if test="${item.discountPercent > 0}">
                                    <br><span style="font-size: 0.7rem; color: #b45309;">(${item.discountPercent}% Discount)</span>
                                </c:if>
                            </td>
                            <td>${item.quantity}</td>
                            <td>₹${item.unitMrp}</td>
                            <td style="text-align: right;"><strong>₹${item.lineTotal}</strong></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>

            <!-- Totals Breakdown -->
            <div class="receipt-totals">
                <div class="receipt-total-row">
                    <span>Subtotal (${bill.totalQuantity} items):</span>
                    <span>₹<fmt:formatNumber value="${bill.subtotal}" pattern="#,##0.00"/></span>
                </div>
                <c:if test="${bill.discountAmount > 0}">
                    <div class="receipt-total-row" style="color: #15803d;">
                        <span>Discount Savings:</span>
                        <span>- ₹<fmt:formatNumber value="${bill.discountAmount}" pattern="#,##0.00"/></span>
                    </div>
                </c:if>
                <div class="receipt-total-row" style="color: #64748b;">
                    <span>CGST (2.5%) + SGST (2.5%):</span>
                    <span>₹<fmt:formatNumber value="${bill.taxAmount}" pattern="#,##0.00"/></span>
                </div>
                <div class="receipt-grand-total">
                    <span>TOTAL AMOUNT:</span>
                    <span>₹<fmt:formatNumber value="${bill.netTotal}" pattern="#,##0.00"/></span>
                </div>
            </div>

            <!-- Barcode Image of Invoice Number for quick scanning & returns -->
            <div style="text-align: center; margin-top: 16px;">
                <img src="${invoiceBarcodeDataUri}" alt="Invoice ${bill.billNo}" style="height: 48px; max-width: 100%;">
                <div style="font-family: monospace; font-size: 0.8rem; font-weight: 700; color: #334155; margin-top: 2px;">
                    ${bill.billNo}
                </div>
            </div>

            <!-- Footer & Return Policy -->
            <div class="receipt-footer">
                <p>Thank you for shopping with us!</p>
                <p>Goods once sold can be exchanged within 7 days in unused condition along with original barcode tag and bill.</p>
                <p style="margin-top: 6px; font-weight: 600;">*** COMPUTER GENERATED INVOICE ***</p>
            </div>
        </div>
    </main>

    <footer>
        <div class="footer-divider">✦ ✦ ✦</div>
        <p>Retail Apparel Inventory Management System &bull; B.Tech Final Year Project</p>
    </footer>
</body>
</html>
