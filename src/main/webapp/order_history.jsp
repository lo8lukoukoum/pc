<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="jsp/header.jsp" />

<section id="order-history-section">
    <h2>我的订单 (My Orders)</h2>

    <c:if test="${param.orderSuccess == 'true'}">
        <span class="success-message">订单 #${param.orderId} 已成功提交！(Order #${param.orderId} placed successfully!)</span>
    </c:if>
    <c:if test="${not empty requestScope.errorMessage}">
        <span class="error-message"><c:out value="${requestScope.errorMessage}"/></span>
    </c:if>

    <c:choose>
        <c:when test="${empty orderList}">
            <p class="info-message">您还没有下过任何订单。(You have not placed any orders yet.)</p>
        </c:when>
        <c:otherwise>
            <table class="order-history-table">
                <thead>
                    <tr>
                        <th>订单号 (Order ID)</th>
                        <th>下单日期 (Order Date)</th>
                        <th>总金额 (Total Amount)</th>
                        <th>状态 (Status)</th>
                        <th>操作 (Action)</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="order" items="${orderList}">
                        <tr>
                            <td>#<c:out value="${order.id}"/></td>
                            <td><fmt:formatDate value="${order.orderDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                            <td><fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="¥"/></td>
                            <td><c:out value="${order.status}"/></td>
                            <td><a href="${pageContext.request.contextPath}/orderDetail?orderId=${order.id}">查看详情 (View Details)</a></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            <%-- Placeholder for pagination --%>
            <div style="text-align: center; margin-top: 20px;">
                <p><em>分页功能将在后续实现。(Pagination will be implemented later.)</em></p>
            </div>
        </c:otherwise>
    </c:choose>
</section>

<style>
    .order-history-table {
        width: 100%;
        border-collapse: collapse;
        margin-top: 20px;
    }
    .order-history-table th, .order-history-table td {
        border: 1px solid #ddd;
        padding: 10px;
        text-align: left;
        vertical-align: middle;
    }
    .order-history-table th {
        background-color: #f2f2f2;
    }
    .order-history-table a {
        text-decoration: none;
        color: #007bff;
    }
    .order-history-table a:hover {
        text-decoration: underline;
    }
</style>

<jsp:include page="jsp/footer.jsp" />
