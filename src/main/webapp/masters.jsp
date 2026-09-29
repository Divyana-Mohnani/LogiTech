<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Master Data Management - Retail Apparel</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/app.css">
</head>
<body>
    <jsp:include page="/WEB-INF/common/navbar.jsp" />

    <main class="main-container">
        <div class="page-title-bar">
            <div>
                <h1 class="page-title">Master Data Management</h1>
                <p class="page-subtitle">Configure Designs, extend Colours and Condition lookup tables (Owner Access Only)</p>
            </div>
            <a href="${pageContext.request.contextPath}/backup" class="btn btn-secondary">💾 Export DB Backup (.sql)</a>
        </div>

        <c:if test="${not empty successMessage}">
            <div class="alert alert-success">${successMessage}</div>
        </c:if>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger">${errorMessage}</div>
        </c:if>

        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 24px;">
            <!-- Design Master -->
            <div class="card">
                <h2 class="card-title">+ Add New Product Design</h2>
                <form action="${pageContext.request.contextPath}/masters" method="post">
                    <input type="hidden" name="action" value="addDesign">

                    <div class="form-group">
                        <label for="designNo">Design Number (e.g. D1026) <span style="color: red;">*</span></label>
                        <input type="text" id="designNo" name="designNo" class="form-control" placeholder="D1026" required>
                    </div>

                    <div class="form-group">
                        <label for="designName">Design / Model Name <span style="color: red;">*</span></label>
                        <input type="text" id="designName" name="designName" class="form-control" placeholder="e.g. HydroPro Racing Jammer" required>
                    </div>

                    <div class="form-group">
                        <label for="category">Category <span style="color: red;">*</span></label>
                        <select id="category" name="category" class="form-control" required>
                            <option value="Swimwear">Swimwear</option>
                            <option value="Footwear">Footwear</option>
                            <option value="Accessories">Accessories</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="description">Description</label>
                        <textarea id="description" name="description" class="form-control" rows="2" placeholder="Fabric, fit and technical specifications"></textarea>
                    </div>

                    <button type="submit" class="btn btn-primary">Add Design</button>
                </form>

                <h3 style="font-size: 1rem; font-weight: 600; margin-top: 24px; margin-bottom: 10px;">Existing Designs (${designs.size()})</h3>
                <div class="table-responsive" style="max-height: 250px; overflow-y: auto;">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Code</th>
                                <th>Name</th>
                                <th>Category</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="d" items="${designs}">
                                <tr>
                                    <td><strong>${d.designNo}</strong></td>
                                    <td>${d.designName}</td>
                                    <td>${d.category}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- Colour Master -->
            <div class="card">
                <h2 class="card-title">+ Extend Colour Master</h2>
                <form action="${pageContext.request.contextPath}/masters" method="post">
                    <input type="hidden" name="action" value="addColour">

                    <div class="form-group">
                        <label for="colourCode">Colour Code (2 letters, e.g. "PR") <span style="color: red;">*</span></label>
                        <input type="text" id="colourCode" name="colourCode" class="form-control" maxlength="5" placeholder="PR" required>
                    </div>

                    <div class="form-group">
                        <label for="colourName">Colour Name (e.g. "Purple") <span style="color: red;">*</span></label>
                        <input type="text" id="colourName" name="colourName" class="form-control" placeholder="Purple" required>
                    </div>

                    <button type="submit" class="btn btn-primary">Add Colour</button>
                </form>

                <h3 style="font-size: 1rem; font-weight: 600; margin-top: 24px; margin-bottom: 10px;">Available Colours (${colours.size()})</h3>
                <div class="table-responsive" style="max-height: 250px; overflow-y: auto;">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Code</th>
                                <th>Colour Name</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="c" items="${colours}">
                                <tr>
                                    <td><span class="code-pill">${c.colourCode}</span></td>
                                    <td>${c.colourName}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </main>

    <footer>
        <div class="footer-divider">✦ ✦ ✦</div>
        <p>Retail Apparel Inventory Management System &bull; B.Tech Final Year Project</p>
    </footer>
</body>
</html>
