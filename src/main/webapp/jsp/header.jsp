<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>小爱鲜花销售系统</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <!-- Additional CSS files can be linked here -->
</head>
<body>
    <header>
        <h1><a href="${pageContext.request.contextPath}/index.jsp">小爱鲜花网</a></h1>
        <nav>
            <ul>
                <li><a href="${pageContext.request.contextPath}/index.jsp">首页 (Home)</a></li>
                <li><a href="${pageContext.request.contextPath}/products.jsp">鲜花商品 (Products)</a></li>
                <li><a href="${pageContext.request.contextPath}/cart.jsp">购物车 (Cart)</a></li>
                <li><a href="${pageContext.request.contextPath}/login.jsp">登录 (Login)</a></li>
                <li><a href="${pageContext.request.contextPath}/register.jsp">注册 (Register)</a></li>
                <!-- Later: Add logic for logout or user profile link -->
            </ul>
        </nav>
    </header>
    <main> <!-- Opening main content tag, to be closed in the page that includes the footer -->
