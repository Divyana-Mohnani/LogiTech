<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Barcode Label Generator - Retail Apparel Inventory</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/app.css">
</head>
<body>
    <jsp:include page="/WEB-INF/common/navbar.jsp" />

    <main class="main-container">
        <div class="page-title-bar no-print">
            <div>
                <h1 class="page-title">Barcode Label Generator</h1>
                <p class="page-subtitle">Code 128 barcode labels with MRP for job-worker &amp; internal inventory (ZXing)</p>
            </div>
            <div style="display: flex; gap: 10px;">
                <c:if test="${not empty item}">
                    <button onclick="window.print()" class="btn btn-primary">🖨️ Print Labels</button>
                </c:if>
                <a href="${pageContext.request.contextPath}/search" class="btn btn-outline">&larr; Back to Inventory</a>
            </div>
        </div>

        <c:choose>
            <c:when test="${not empty item}">
                <!-- Label print preview & sheet settings -->
                <div class="card no-print" style="padding: 18px;">
                    <form action="${pageContext.request.contextPath}/label" method="get" style="display: flex; align-items: center; gap: 16px;">
                        <input type="hidden" name="stockId" value="${item.stockId}">
                        <label for="copiesInput" style="font-weight: 600;">Number of labels to print:</label>
                        <input type="number" id="copiesInput" name="copies" class="form-control" style="width: 100px;" min="1" max="100" value="${copies}">
                        <button type="submit" class="btn btn-secondary">Update Copies</button>
                        <span style="font-size: 0.85rem; color: var(--text-muted);">
                            Fits standard thermal sticker roll or A4 sticker sheet.
                        </span>
                    </form>
                </div>

                <!-- Printable labels grid -->
                <div class="labels-grid">
                    <c:forEach begin="1" end="${copies}" var="i">
                        <div class="barcode-label">
                            <div class="label-title">RETAIL APPAREL STORE</div>
                            <div style="font-size: 0.8rem; font-weight: 600; color: #334155;">${item.designName}</div>
                            
                            <!-- ZXing generated Code 128 barcode image -->
                            <img src="${barcodeDataUri}" alt="Barcode ${item.variantCode}" class="label-barcode-img">
                            
                            <div class="label-code">${item.variantCode}</div>
                            
                            <div class="label-meta">
                                <span>Size: <strong>${item.size}</strong></span>
                                <span>Col: <strong>${item.colourName}</strong></span>
                                <span>Sex: <strong>${item.genderLabel}</strong></span>
                            </div>

                            <div class="label-mrp">MRP: ₹${item.mrp}</div>
                            <div style="font-size: 0.68rem; color: #64748b;">(Inclusive of all taxes)</div>
                        </div>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <!-- Selector if no item preselected -->
                <div class="card">
                    <h2 class="card-title">Select an item to generate barcode labels</h2>
                    <p style="font-size: 0.9rem; color: var(--text-muted); margin-bottom: 20px;">
                        Goods stitched by job workers arrive without vendor barcodes. Select a variant below to generate and print internal scannable Code 128 barcode labels.
                    </p>

                    <div class="table-responsive">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Variant Code</th>
                                    <th>Design</th>
                                    <th>Category</th>
                                    <th>Size</th>
                                    <th>Colour</th>
                                    <th>MRP</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="v" items="${allItems}">
                                    <tr>
                                        <td><span class="code-pill">${v.variantCode}</span></td>
                                        <td>${v.designNo} - ${v.designName}</td>
                                        <td>${v.category}</td>
                                        <td>${v.size}</td>
                                        <td>${v.colourName}</td>
                                        <td>₹${v.mrp}</td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/label?stockId=${v.stockId}&copies=4" class="btn btn-primary btn-sm">
                                                Generate Labels
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
    </main>

    <footer>
        <div class="footer-divider">✦ ✦ ✦</div>
        <p>Retail Apparel Inventory Management System &bull; B.Tech Final Year Project</p>
    </footer>
</body>
</html>
