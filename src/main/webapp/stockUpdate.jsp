<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Stock Update - Retail Apparel Inventory</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/app.css">
</head>
<body>
    <jsp:include page="/WEB-INF/common/navbar.jsp" />

    <main class="main-container">
        <div class="page-title-bar">
            <div>
                <h1 class="page-title">Stock Update</h1>
                <p class="page-subtitle">Record new stock or update existing variant quantities (Synopsis Figure 4)</p>
            </div>
            <a href="${pageContext.request.contextPath}/search" class="btn btn-outline">&larr; Back to Search</a>
        </div>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <c:if test="${not empty sessionScope.flashSuccess}">
            <div class="alert alert-success">
                ${sessionScope.flashSuccess}
                <c:remove var="flashSuccess" scope="session" />
            </div>
        </c:if>

        <div class="card" style="padding: 30px;">
            <form id="stockUpdateForm" action="${pageContext.request.contextPath}/stock" method="post">
                <div class="form-grid-2col">
                    <!-- Left Column -->
                    <div>
                        <!-- Category -->
                        <div class="form-group">
                            <label for="categorySelect">Category <span style="color: red;">*</span></label>
                            <select id="categorySelect" name="category" class="form-control" required>
                                <option value="Swimwear" selected>Swimwear</option>
                                <option value="Footwear">Footwear</option>
                                <option value="Accessories">Accessories</option>
                            </select>
                        </div>

                        <!-- Gender -->
                        <div class="form-group">
                            <label for="genderSelect">Gender <span style="color: red;">*</span></label>
                            <select id="genderSelect" name="gender" class="form-control" required>
                                <option value="M" selected>Gents (M)</option>
                                <option value="W">Ladies (W)</option>
                                <option value="B">Boys (B)</option>
                                <option value="G">Girls (G)</option>
                            </select>
                        </div>

                        <!-- Existing Design Quick Select & Design No -->
                        <div class="form-group">
                            <label for="existingDesignSelect">Select Existing Design (Optional)</label>
                            <select id="existingDesignSelect" class="form-control" style="margin-bottom: 8px;">
                                <option value="">-- Choose existing or type below --</option>
                                <c:forEach var="d" items="${designs}">
                                    <option value="${d.designNo}" data-category="${d.category}">
                                        ${d.designNo} - ${d.designName} (${d.category})
                                    </option>
                                </c:forEach>
                            </select>
                            <label for="designNoInput">Design No. <span style="color: red;">*</span></label>
                            <input type="text" id="designNoInput" name="designNo" class="form-control" value="D1024" placeholder="e.g. D1024, D2210" required>
                        </div>

                        <!-- Colour -->
                        <div class="form-group">
                            <label for="colourSelect">Colour <span style="color: red;">*</span></label>
                            <select id="colourSelect" name="colourCode" class="form-control" required>
                                <c:forEach var="col" items="${colours}">
                                    <option value="${col.colourCode}" ${col.colourCode == 'BK' ? 'selected' : ''}>
                                        ${col.colourName} (${col.colourCode})
                                    </option>
                                </c:forEach>
                            </select>
                        </div>

                        <!-- Size -->
                        <div class="form-group">
                            <label for="sizeSelect">Size <span style="color: red;">*</span></label>
                            <select id="sizeSelect" name="size" class="form-control" required>
                                <option value="S">S - Small</option>
                                <option value="M">M - Medium</option>
                                <option value="L" selected>L - Large</option>
                                <option value="XL">XL - Extra Large</option>
                                <option value="XXL">XXL - Double Large</option>
                                <option value="FS">FS - Free Size</option>
                            </select>
                        </div>
                    </div>

                    <!-- Right Column -->
                    <div>
                        <!-- Swimwear Subtype Toggle (Only visible for Swimwear) -->
                        <div id="swimwearControlsGroup" style="background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 6px; padding: 12px; margin-bottom: 14px;">
                            <label style="font-size: 0.85rem; font-weight: 700; color: #475569; display: block; margin-bottom: 6px;">
                                Swimwear Product Type:
                            </label>
                            <div style="display: flex; gap: 16px;">
                                <label style="font-size: 0.88rem; font-weight: normal; cursor: pointer;">
                                    <input type="radio" name="swimwearType" value="bottom" checked> Bottom (Trunks / Shorts)
                                </label>
                                <label style="font-size: 0.88rem; font-weight: normal; cursor: pointer;">
                                    <input type="radio" name="swimwearType" value="top"> Top (Rashguards / Vests)
                                </label>
                                <label style="font-size: 0.88rem; font-weight: normal; cursor: pointer;">
                                    <input type="radio" name="swimwearType" value="other"> Other (NA for both)
                                </label>
                            </div>
                        </div>

                        <!-- Sleeve Length -->
                        <div class="form-group">
                            <label for="sleeveLengthSelect">
                                Sleeve length
                                <span class="na-indicator">(Greyed out if NA)</span>
                            </label>
                            <select id="sleeveLengthSelect" name="sleeveLength" class="form-control">
                                <option value="NA">NA - Not Applicable</option>
                                <option value="FS">FS - Full sleeve</option>
                                <option value="HS">HS - Half sleeve</option>
                                <option value="SL">SL - Sleeveless</option>
                            </select>
                        </div>

                        <!-- Lower Length -->
                        <div class="form-group">
                            <label for="lowerLengthSelect">
                                Lower length
                                <span class="na-indicator">(Greyed out if NA)</span>
                            </label>
                            <select id="lowerLengthSelect" name="lowerLength" class="form-control">
                                <option value="B" selected>B - Trunk</option>
                                <option value="SH">SH - Shorts</option>
                                <option value="CP">CP - Capri</option>
                                <option value="FP">FP - Full pant</option>
                                <option value="NA">NA - Not Applicable</option>
                            </select>
                        </div>

                        <!-- Quantity -->
                        <div class="form-group">
                            <label for="quantityInput">Quantity <span style="color: red;">*</span></label>
                            <input type="number" id="quantityInput" name="quantity" class="form-control" value="10" min="0" required>
                        </div>

                        <!-- Prices -->
                        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 12px;">
                            <div class="form-group">
                                <label for="vendorPriceInput">Vendor Price (Cost ₹)</label>
                                <input type="number" step="0.01" id="vendorPriceInput" name="vendorPrice" class="form-control" value="450.00" min="0">
                            </div>
                            <div class="form-group">
                                <label for="mrpInput">MRP (Selling ₹) <span style="color: red;">*</span></label>
                                <input type="number" step="0.01" id="mrpInput" name="mrp" class="form-control" value="999.00" min="0" required>
                            </div>
                        </div>

                        <!-- Condition -->
                        <div class="form-group">
                            <label for="conditionSelect">Condition <span style="color: red;">*</span></label>
                            <select id="conditionSelect" name="conditionId" class="form-control" required>
                                <c:forEach var="c" items="${conditions}">
                                    <option value="${c.conditionId}">${c.conditionName}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <!-- Special Note -->
                        <div class="form-group">
                            <label for="noteInput">Special note (Defects / preferences)</label>
                            <input type="text" id="noteInput" name="note" class="form-control" placeholder="e.g. Dark shades preferred, Seam defect, etc.">
                        </div>
                    </div>
                </div>

                <!-- Live Generated Variant Code Banner (Synopsis Figure 4) -->
                <div class="variant-code-banner">
                    <div>
                        <div style="font-size: 0.85rem; color: #94a3b8; text-transform: uppercase;">Generated Variant Code</div>
                        <div id="variantCodeDisplay" class="variant-code-text">M/L/B/BK</div>
                    </div>
                    <div class="form-actions">
                        <button type="submit" class="btn btn-primary" style="padding: 10px 28px; font-size: 1rem;">Save</button>
                        <button type="button" id="clearStockBtn" class="btn btn-secondary" style="padding: 10px 20px;">Clear</button>
                    </div>
                </div>
            </form>
        </div>
    </main>

    <footer>
        <div class="footer-divider">✦ ✦ ✦</div>
        <p>Retail Apparel Inventory Management System &bull; B.Tech Final Year Project</p>
    </footer>

    <script src="${pageContext.request.contextPath}/static/js/stock-update.js"></script>
</body>
</html>
