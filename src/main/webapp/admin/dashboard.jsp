<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/jsp/header.jsp" /> <%-- Use absolute path for main header --%>

<section id="admin-dashboard-section" style="padding: 20px;">
    <h2>管理后台 (Admin Dashboard)</h2>

    <p class="info-message">欢迎, <c:out value="${sessionScope.loggedInUser.username}"/>! 您已登录到管理后台。</p>
    <p>请从以下选项中选择一项进行管理：</p>

    <ul class="admin-links" style="list-style-type: none; padding: 0;">
        <li style="margin-bottom: 10px;">
            <a href="${pageContext.request.contextPath}/admin/products.jsp" style="font-size: 1.2em;">商品管理 (Product Management)</a>
        </li>
        <li style="margin-bottom: 10px;">
            <a href="${pageContext.request.contextPath}/admin/orders.jsp" style="font-size: 1.2em;">订单管理 (Order Management)</a>
        </li>
        <li style="margin-bottom: 10px;">
            <a href="${pageContext.request.contextPath}/admin/customers.jsp" style="font-size: 1.2em;">客户管理 (Customer Management)</a>
        </li>
        <li style="margin-bottom: 10px;">
            <a href="${pageContext.request.contextPath}/admin/reviews.jsp" style="font-size: 1.2em;">审核管理 (Review & Moderation)</a>
        </li>
        <li style="margin-bottom: 10px;">
            <a href="${pageContext.request.contextPath}/admin/stats.jsp" style="font-size: 1.2em;">数据统计 (Data & Statistics)</a>
        </li>
    </ul>
</section>

<jsp:include page="/jsp/footer.jsp" /> <%-- Use absolute path for main footer --%>
