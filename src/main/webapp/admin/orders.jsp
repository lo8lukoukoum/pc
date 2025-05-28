<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/jsp/header.jsp" />

<section id="admin-order-list-section" style="padding: 20px;">
    <h2>订单管理 (Order Management)</h2>

    <form action="${pageContext.request.contextPath}/admin/viewOrders" method="GET" style="margin-bottom: 20px;">
        <label for="statusFilter">筛选订单状态 (Filter by Status):</label>
        <select name="statusFilter" id="statusFilter">
            <option value="ALL" ${currentStatusFilter == 'ALL' ? 'selected' : ''}>全部 (All)</option>
            <option value="待付款" ${currentStatusFilter == '待付款' ? 'selected' : ''}>待付款 (Pending Payment)</option>
            <option value="处理中" ${currentStatusFilter == '处理中' ? 'selected' : ''}>处理中 (Processing)</option>
            <option value="待发货" ${currentStatusFilter == '待发货' ? 'selected' : ''}>待发货 (Pending Shipment)</option>
            <option value="已发货" ${currentStatusFilter == '已发货' ? 'selected' : ''}>已发货 (Shipped)</option>
            <option value="已完成" ${currentStatusFilter == '已完成' ? 'selected' : ''}>已完成 (Completed)</option>
            <option value="已取消" ${currentStatusFilter == '已取消' ? 'selected' : ''}>已取消 (Cancelled)</option>
        </select>
        <button type="submit" class="admin-button filter">筛选 (Filter)</button>
    </form>

    <c:if test="${not empty errorMessage}">
        <span class="error-message"><c:out value="${errorMessage}"/></span>
    </c:if>
    <c:if test="${not empty param.error}">
        <span class="error-message"><c:out value="${param.error}"/></span>
    </c:if>

    <c:choose>
        <c:when test="${empty orderList}">
            <p class="info-message">没有符合条件的订单。(No orders match criteria.)</p>
        </c:when>
        <c:otherwise>
            <table class="admin-table">
                <thead>
                    <tr>
                        <th>订单号 (ID)</th>
                        <th>客户 (Customer)</th>
                        <th>下单日期 (Date)</th>
                        <th>总金额 (Total)</th>
                        <th>状态 (Status)</th>
                        <th>收货地址 (Shipping Address)</th>
                        <th>联系电话 (Phone)</th>
                        <th>操作 (Actions)</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="order" items="${orderList}">
                        <tr>
                            <td>#<c:out value="${order.id}"/></td>
                            <td><c:out value="${order.customerUsername}"/> (ID: <c:out value="${order.userId}"/>)</td>
                            <td><fmt:formatDate value="${order.orderDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                            <td><fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="¥"/></td>
                            <td><c:out value="${order.status}"/></td>
                            <td><c:out value="${order.shippingAddress}"/></td>
                            <td><c:out value="${order.contactPhone}"/></td>
                            <td>
                                <a href="${pageContext.request.contextPath}/admin/order_detail.jsp?orderId=${order.id}" class="admin-button view">查看详情 (View)</a>
                                <%-- Placeholder for Change Status --%>
                                <button type="button" class="admin-button change-status" onclick="alert('更改状态功能待实现 (Change status to be implemented for order ${order.id})');">改状态</button>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            <%-- Placeholder for Pagination Controls --%>
            <div style="text-align: center; margin-top: 20px;">
                <p><em>分页功能将在后续实现。(Pagination will be implemented later.)</em></p>
            </div>
        </c:otherwise>
    </c:choose>
</section>

<style>
    .admin-table {
        width: 100%;
        border-collapse: collapse;
        margin-top: 20px;
        font-size: 0.9em;
    }
    .admin-table th, .admin-table td {
        border: 1px solid #ddd;
        padding: 8px;
        text-align: left;
        vertical-align: middle;
    }
    .admin-table th {
        background-color: #f2f2f2;
    }
    .admin-button {
        padding: 5px 10px;
        text-decoration: none;
        border-radius: 3px;
        margin-right: 5px;
        color: white !important;
        display: inline-block;
        border: none;
        cursor: pointer;
    }
    .admin-button.filter {
        background-color: #007bff; /* Blue */
    }
    .admin-button.filter:hover {
        background-color: #0056b3;
    }
    .admin-button.view {
        background-color: #17a2b8; /* Info Teal */
    }
     .admin-button.view:hover {
        background-color: #117a8b;
    }
    .admin-button.change-status {
        background-color: #ffc107; /* Warning Yellow */
        color: #212529 !important;
    }
    .admin-button.change-status:hover {
        background-color: #e0a800;
    }
</style>

<jsp:include page="/jsp/footer.jsp" />
