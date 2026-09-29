<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Search Inventory - Retail Apparel Inventory</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/app.css">
</head>
<body>
    <jsp:include page="/WEB-INF/common/navbar.jsp" />

    <main class="main-container">
        <div class="page-title-bar">
            <div>
                <h1 class="page-title">Search &amp; Filter Inventory</h1>
                <p class="page-subtitle">Search by design number, variant code fragment, or filter by category &amp; condition</p>
            </div>
            <a href="${pageContext.request.contextPath}/stock" class="btn btn-primary">+ Add Stock</a>
        </div>

        <!-- Filter Card -->
        <div class="card" style="padding: 20px;">
            <form action="${pageContext.request.contextPath}/search" method="get">
                <div style="display: grid; grid-template-columns: 2fr 1fr 1fr 1fr 1fr auto; gap: 12px; align-items: end;">
                    <!-- Keyword search -->
                    <div class="form-group" style="margin-bottom: 0;">
                        <label for="searchQuery">Search Keyword / Barcode</label>
                        <input type="text" id="searchQuery" name="q" class="form-control"
                               placeholder="Scan barcode or type code/design/note..."
                               value="${query}">
                    </div>

                    <!-- Category Filter -->
                    <div class="form-group" style="margin-bottom: 0;">
                        <label for="categoryFilter">Category</label>
                        <select id="categoryFilter" name="category" class="form-control">
                            <option value="">All Categories</option>
                            <option value="Swimwear" ${selectedCategory == 'Swimwear' ? 'selected' : ''}>Swimwear</option>
                            <option value="Footwear" ${selectedCategory == 'Footwear' ? 'selected' : ''}>Footwear</option>
                            <option value="Accessories" ${selectedCategory == 'Accessories' ? 'selected' : ''}>Accessories</option>
                        </select>
                    </div>

                    <!-- Gender Filter -->
                    <div class="form-group" style="margin-bottom: 0;">
                        <label for="genderFilter">Gender</label>
                        <select id="genderFilter" name="gender" class="form-control">
                            <option value="">All Genders</option>
                            <option value="M" ${selectedGender == 'M' ? 'selected' : ''}>Gents (M)</option>
                            <option value="W" ${selectedGender == 'W' ? 'selected' : ''}>Ladies (W)</option>
                            <option value="B" ${selectedGender == 'B' ? 'selected' : ''}>Boys (B)</option>
                            <option value="G" ${selectedGender == 'G' ? 'selected' : ''}>Girls (G)</option>
                        </select>
                    </div>

                    <!-- Condition Filter -->
                    <div class="form-group" style="margin-bottom: 0;">
                        <label for="conditionFilter">Condition</label>
                        <select id="conditionFilter" name="conditionId" class="form-control">
                            <option value="">All Conditions</option>
                            <c:forEach var="c" items="${conditions}">
                                <option value="${c.conditionId}" ${selectedCondition == c.conditionId ? 'selected' : ''}>${c.conditionName}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <!-- Colour Filter -->
                    <div class="form-group" style="margin-bottom: 0;">
                        <label for="colourFilter">Colour</label>
                        <select id="colourFilter" name="colourCode" class="form-control">
                            <option value="">All Colours</option>
                            <c:forEach var="col" items="${colours}">
                                <option value="${col.colourCode}" ${selectedColour == col.colourCode ? 'selected' : ''}>${col.colourName}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <div style="display: flex; gap: 8px;">
                        <button type="submit" class="btn btn-primary" style="padding: 9px 16px;">Search</button>
                        <a href="${pageContext.request.contextPath}/search" class="btn btn-secondary" style="padding: 9px 14px;">Reset</a>
                    </div>
                </div>
            </form>
        </div>

        <!-- Search Results Table -->
        <div class="card">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
                <h2 class="card-title" style="margin-bottom: 0; border: none;">
                    Results (${results.size()} stock records found)
                </h2>
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
                            <th>Qty</th>
                            <th>Cost</th>
                            <th>MRP</th>
                            <th>Condition</th>
                            <th>Special Note</th>
                            <th>Print</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="item" items="${results}">
                            <tr>
                                <td><span class="code-pill">${item.variantCode}</span></td>
                                <td>
                                    <strong>${item.designNo}</strong><br>
                                    <span style="font-size: 0.8rem; color: var(--text-muted);">${item.designName}</span>
                                </td>
                                <td>${item.category}</td>
                                <td>${item.genderLabel}</td>
                                <td><strong>${item.size}</strong></td>
                                <td>${item.lengthLabel}</td>
                                <td>${item.colourName}</td>
                                <td><span style="font-size: 1.05rem; font-weight: 700;">${item.quantity}</span></td>
                                <td>₹${item.vendorPrice}</td>
                                <td><strong>₹${item.mrp}</strong></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${item.conditionId == 1}"><span class="badge badge-normal">Normal</span></c:when>
                                        <c:when test="${item.conditionId == 2}"><span class="badge badge-defective">Defective</span></c:when>
                                        <c:when test="${item.conditionId == 3}"><span class="badge badge-old">Old</span></c:when>
                                        <c:when test="${item.conditionId == 4}"><span class="badge badge-dead">Dead stock</span></c:when>
                                        <c:otherwise><span class="badge">${item.conditionName}</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty item.note}">
                                            <span style="font-style: italic; font-size: 0.85rem; color: #475569;">"${item.note}"</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span style="color: #cbd5e1;">-</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/label?stockId=${item.stockId}" class="btn btn-outline btn-sm" title="Generate Barcode Label">🏷️ Label</a>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty results}">
                            <tr>
                                <td colspan="13" style="text-align: center; padding: 40px; color: var(--text-muted);">
                                    No variants or stock records match your query. Try clearing your search filters.
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
