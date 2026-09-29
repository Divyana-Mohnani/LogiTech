<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Vendor Import &amp; Barcode Scanner - Retail Apparel</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/app.css">
    <style>
        .scanner-card {
            background: #f0fdf4;
            border: 2px dashed #22c55e;
            padding: 24px;
            border-radius: 8px;
            margin-bottom: 28px;
        }
        .scanner-input {
            font-size: 1.25rem;
            font-family: monospace;
            padding: 12px 16px;
            border: 2px solid #16a34a;
            border-radius: 6px;
            width: 100%;
        }
    </style>
</head>
<body>
    <jsp:include page="/WEB-INF/common/navbar.jsp" />

    <main class="main-container">
        <div class="page-title-bar">
            <div>
                <h1 class="page-title">Vendor Import &amp; Scanner Station</h1>
                <p class="page-subtitle">Import supplier batch files (CSV/Excel) or scan incoming goods via USB barcode scanner</p>
            </div>
            <a href="${pageContext.request.contextPath}/search" class="btn btn-outline">&larr; View Inventory</a>
        </div>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <c:if test="${not empty scanSuccess}">
            <div class="alert alert-success">${scanSuccess}</div>
        </c:if>

        <c:if test="${not empty scanError}">
            <div class="alert alert-danger">${scanError}</div>
        </c:if>

        <!-- SECTION 1: USB Barcode Scanner Terminal -->
        <div class="scanner-card">
            <div style="display: flex; align-items: center; gap: 12px; margin-bottom: 12px;">
                <span style="font-size: 1.8rem;">📦</span>
                <div>
                    <h2 style="font-size: 1.15rem; font-weight: 700; color: #166534; margin: 0;">Shop-Floor USB Barcode Scanner</h2>
                    <p style="font-size: 0.82rem; color: #15803d; margin: 0;">
                        Point your USB barcode scanner at the product tag. The scanner types the code and submits automatically.
                    </p>
                </div>
            </div>

            <form action="${pageContext.request.contextPath}/import" method="get" style="display: flex; gap: 12px;">
                <input type="text" name="scannedCode" class="scanner-input"
                       placeholder="Scan barcode or type variant code (e.g. M/L/B/BK)..."
                       value="${scannedCode}" autofocus autocomplete="off">
                <button type="submit" class="btn btn-success" style="padding: 0 24px; font-size: 1rem;">Lookup Code</button>
            </form>

            <c:if test="${not empty scannedItem}">
                <div style="margin-top: 18px; background: #ffffff; border: 1px solid #bbf7d0; border-radius: 6px; padding: 18px;">
                    <h3 style="font-size: 1rem; color: #166534; margin-bottom: 10px;">
                        Found Variant: <span class="code-pill">${scannedItem.variantCode}</span>
                    </h3>
                    <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: 12px; font-size: 0.88rem; margin-bottom: 16px;">
                        <div><strong>Design:</strong> ${scannedItem.designNo} - ${scannedItem.designName}</div>
                        <div><strong>Category:</strong> ${scannedItem.category}</div>
                        <div><strong>Size / Colour:</strong> ${scannedItem.size} / ${scannedItem.colourName}</div>
                        <div><strong>Current Quantity:</strong> ${scannedItem.quantity} units</div>
                        <div><strong>Vendor Price:</strong> ₹${scannedItem.vendorPrice}</div>
                        <div><strong>MRP:</strong> ₹${scannedItem.mrp}</div>
                    </div>

                    <form action="${pageContext.request.contextPath}/import" method="post" style="display: flex; align-items: center; gap: 14px; background: #f8fafc; padding: 12px; border-radius: 6px;">
                        <input type="hidden" name="action" value="scanConfirm">
                        <input type="hidden" name="variantCode" value="${scannedItem.variantCode}">
                        <label for="confirmQuantity" style="font-weight: 600; font-size: 0.9rem;">Confirm Incoming Quantity:</label>
                        <input type="number" id="confirmQuantity" name="confirmQuantity" class="form-control" style="width: 120px;" value="1" min="1" required>
                        <button type="submit" class="btn btn-primary">Add to Inventory</button>
                    </form>
                </div>
            </c:if>
        </div>

        <!-- SECTION 2: Batch Vendor File Import (CSV & Excel) -->
        <div class="card">
            <h2 class="card-title">Bulk Vendor File Import (CSV / Excel)</h2>
            <p style="font-size: 0.88rem; color: var(--text-muted); margin-bottom: 18px;">
                Upload a shipment spreadsheet provided by the apparel manufacturer. The system automatically registers designs, generates missing variant codes, and records stock quantities.
            </p>

            <c:if test="${not empty importResult}">
                <div class="alert ${uploadSuccess ? 'alert-success' : 'alert-danger'}">
                    <strong>Import Summary:</strong> Processed ${importResult.totalRows} rows.
                    <strong>${importResult.successCount} successful</strong>, ${importResult.failureCount} failed.
                    <c:if test="${not empty importResult.errorMessages}">
                        <ul style="margin-top: 8px; margin-left: 20px;">
                            <c:forEach var="err" items="${importResult.errorMessages}">
                                <li>${err}</li>
                            </c:forEach>
                        </ul>
                    </c:if>
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/import" method="post" enctype="multipart/form-data">
                <div class="form-group" style="margin-bottom: 20px;">
                    <label for="vendorFile">Select CSV or Excel File (.csv, .xlsx, .xls)</label>
                    <input type="file" id="vendorFile" name="vendorFile" class="form-control" accept=".csv, .xlsx, .xls" required>
                </div>

                <div style="display: flex; justify-content: space-between; align-items: center;">
                    <button type="submit" class="btn btn-primary">Upload &amp; Process Vendor File</button>
                    <span style="font-size: 0.82rem; color: var(--text-muted);">
                        Supported formats: Apache Commons CSV, Apache POI (.xlsx, .xls)
                    </span>
                </div>
            </form>

            <div style="margin-top: 24px; background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 6px; padding: 14px;">
                <h4 style="font-size: 0.85rem; color: #475569; margin-bottom: 6px;">Expected File Column Headers:</h4>
                <code style="font-size: 0.8rem; color: #0369a1; word-break: break-all;">
                    barcode, design_no, category, design_name, gender, size, length, colour_code, quantity, vendor_price, mrp, condition, note
                </code>
            </div>
        </div>
    </main>

    <footer>
        <div class="footer-divider">✦ ✦ ✦</div>
        <p>Retail Apparel Inventory Management System &bull; B.Tech Final Year Project</p>
    </footer>
</body>
</html>
