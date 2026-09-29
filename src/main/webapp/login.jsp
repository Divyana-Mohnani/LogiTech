<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Inventory Management System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/app.css">
    <style>
        .login-wrapper {
            display: flex;
            align-items: center;
            justify-content: center;
            min-height: 80vh;
        }
        .login-box {
            background: #ffffff;
            border: 1px solid var(--border);
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.08);
            width: 100%;
            max-width: 420px;
            padding: 32px;
        }
        .login-header {
            text-align: center;
            margin-bottom: 24px;
        }
        .login-title {
            font-size: 1.4rem;
            font-weight: 700;
            color: #0b2545;
        }
        .login-subtitle {
            font-size: 0.85rem;
            color: var(--text-muted);
            margin-top: 4px;
        }
        .demo-credentials {
            background: #f8fafc;
            border: 1px dashed #cbd5e1;
            border-radius: 6px;
            padding: 12px;
            margin-top: 20px;
            font-size: 0.82rem;
            color: #475569;
        }
        .demo-credentials strong {
            color: #0f172a;
        }
    </style>
</head>
<body>
    <div class="login-wrapper">
        <div class="login-box">
            <div class="login-header">
                <div style="font-size: 2.2rem; margin-bottom: 8px;">👔</div>
                <h1 class="login-title">Apparel Retail IMS</h1>
                <p class="login-subtitle">Inventory Management &amp; POS Station</p>
            </div>

            <c:if test="${not empty error}">
                <div class="alert alert-danger">${error}</div>
            </c:if>

            <c:if test="${param.loggedOut == 'true'}">
                <div class="alert alert-success">You have been logged out successfully.</div>
            </c:if>

            <form action="${pageContext.request.contextPath}/login" method="post">
                <div class="form-group">
                    <label for="username">Username</label>
                    <input type="text" id="username" name="username" class="form-control" required placeholder="Enter username" autofocus>
                </div>

                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" class="form-control" required placeholder="Enter password">
                </div>

                <button type="submit" class="btn btn-primary" style="width: 100%; margin-top: 8px; padding: 11px;">
                    Sign In
                </button>
            </form>

            <div class="demo-credentials">
                <strong>System Demo Credentials:</strong><br>
                • <strong>Owner:</strong> <code>owner</code> / <code>owner123</code> (All features &amp; masters)<br>
                • <strong>Staff:</strong> <code>staff</code> / <code>staff123</code> (Stock entry &amp; search)
            </div>
        </div>
    </div>
</body>
</html>
