<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="jsp/header.jsp" />

<section id="login-form">
    <h2>用户登录 (User Login)</h2>

    <c:if test="${param.logoutSuccess == 'true'}">
        <span class="success-message">您已成功退出登录。(You have successfully logged out.)</span>
    </c:if>

    <c:if test="${param.registrationSuccess == 'true'}">
        <span class="success-message">注册成功！现在可以登录了。(Registration successful! You can now log in.)</span>
    </c:if>

    <c:if test="${not empty requestScope.errorMessage}">
        <span class="error-message"><c:out value="${requestScope.errorMessage}"/></span>
    </c:if>
     <%-- Adding general authError message display using new CSS classes --%>
    <c:if test="${not empty param.authError}">
        <span class="error-message">
            <c:choose>
                <c:when test="${param.authError == 'true'}">请先登录。(Please login first.)</c:when>
                <c:when test="${param.authError == 'adminRequired'}">此区域需要管理员权限，请以管理员身份登录。(Admin access required. Please login as admin.)</c:when>
                <c:when test="${param.authError == 'cart'}">请登录后查看购物车。(Please login to view your cart.)</c:when>
                <c:when test="${param.authError == 'checkoutAccess'}">请登录后结算。(Please login to proceed to checkout.)</c:when>
                <c:when test="${param.authError == 'orderHistoryAccess'}">请登录后查看订单历史。(Please login to view order history.)</c:when>
                <c:when test="${param.authError == 'orderDetailAccess'}">请登录后查看订单详情。(Please login to view order details.)</c:when>
                <c:when test="${param.authError == 'reviewFormAccess'}">请登录后评价商品。(Please login to review products.)</c:when>
                <c:when test="${param.authError == 'submitReviewAccess'}">请登录后提交评价。(Please login to submit reviews.)</c:when>
                <c:when test="${param.authError == 'cartUpdate'}">请登录后更新购物车。(Please login to update your cart.)</c:when>
                <c:when test="${param.authError == 'orderCreation'}">请登录后创建订单。(Please login to create orders.)</c:when>
                 <c:when test="${param.authError == 'adminOnly'}">此功能仅限管理员。(This function is for admins only.)</c:when>
                <c:otherwise>您需要登录才能访问此页面。(You need to login to access this page.)</c:otherwise>
            </c:choose>
        </span>
    </c:if>

    <form action="${pageContext.request.contextPath}/login" method="POST">
        <p>
            <label for="username">用户名:</label>
            <input type="text" id="username" name="username" required>
        </p>
        <p>
            <label for="password">密码:</label>
            <input type="password" id="password" name="password" required>
        </p>
        <p>
            <button type="submit">登录 (Login)</button>
        </p>
    </form>

    <p>
        还没有账户? <a href="${pageContext.request.contextPath}/register.jsp">立即注册 (Register here)</a>
    </p>
</section>

<jsp:include page="jsp/footer.jsp" />
