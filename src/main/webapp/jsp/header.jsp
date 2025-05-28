<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
                <li><a href="${pageContext.request.contextPath}/products.jsp">鲜花商品 (Products)</a></li> <%-- Assuming products.jsp will exist --%>
                <li><a href="${pageContext.request.contextPath}/cart.jsp">购物车 (Cart)</a></li> <%-- Assuming cart.jsp will exist --%>
                <c:choose>
                    <c:when test="${sessionScope.loggedInUser != null}">
                        <li><span>欢迎, <c:out value="${sessionScope.loggedInUser.username}"/>!</span></li>
                        <li><a href="${pageContext.request.contextPath}/logout">退出 (Logout)</a></li>
                        <c:if test="${sessionScope.loggedInUser.role == 'admin'}">
                            <li><a href="${pageContext.request.contextPath}/admin/dashboard.jsp">管理后台 (Admin Panel)</a></li>
                        </c:if>
                        <%-- Optional: Link to a user profile page --%>
                        <%-- <li><a href="${pageContext.request.contextPath}/my_profile.jsp">个人中心 (Profile)</a></li> --%>
                    </c:when>
                    <c:otherwise>
                        <li><a href="${pageContext.request.contextPath}/login.jsp">登录 (Login)</a></li>
                        <li><a href="${pageContext.request.contextPath}/register.jsp">注册 (Register)</a></li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </nav>
        <div class="search-bar">
            <form action="${pageContext.request.contextPath}/products" method="GET">
                <input type="text" name="searchQuery" placeholder="搜索商品 (Search products)..." value="<c:out value='${param.searchQuery}'/>">
                <button type="submit">搜索 (Search)</button>
            </form>
        </div>
    </header>
    <main> <!-- Opening main content tag, to be closed in the page that includes the footer -->
