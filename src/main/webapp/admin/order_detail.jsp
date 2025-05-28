<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/jsp/header.jsp" />

<section id="admin-order-detail-section" style="padding: 20px;">
    <h2>订单详情 - 管理员视图 (Order Details - Admin View)</h2>

    <c:if test="${not empty requestScope.errorMessage}">
        <span class="error-message"><c:out value="${requestScope.errorMessage}"/></span>
    </c:if>
     <c:if test="${not empty param.statusUpdateSuccess}">
        <span class="success-message">订单状态已成功更新！(Order status updated successfully!)</span>
    </c:if>
    <c:if test="${not empty param.statusUpdateError}">
        <span class="error-message">更新订单状态失败: <c:out value="${param.statusUpdateError}"/></span>
    </c:if>


    <c:choose>
        <c:when test="${not empty order}">
            <div class="order-info-container" style="background-color: #f9f9f9; padding: 15px; border-radius: 5px; margin-bottom: 20px; border: 1px solid #eee;">
                <h3>订单号 (Order ID): #<c:out value="${order.id}"/></h3>
                <p><strong>客户 (Customer):</strong> <c:out value="${order.customerUsername}"/> (User ID: <c:out value="${order.userId}"/>)</p>
                <p><strong>下单日期 (Order Date):</strong> <fmt:formatDate value="${order.orderDate}" pattern="yyyy-MM-dd HH:mm:ss"/></p>
                <p><strong>当前状态 (Current Status):</strong> <span style="font-weight:bold; color: <c:choose><c:when test='${order.status == \"已完成\"}'>green</c:when><c:when test='${order.status == \"已取消\"}'>red</c:when><c:otherwise>blue</c:otherwise></c:choose>;"><c:out value="${order.status}"/></span></p>
                <p><strong>总金额 (Total Amount):</strong> <fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="¥"/></p>
                <p><strong>收货地址 (Shipping Address):</strong> <c:out value="${order.shippingAddress}"/></p>
                <p><strong>联系电话 (Contact Phone):</strong> <c:out value="${order.contactPhone}"/></p>
            </div>

            <div class="change-status-form" style="margin-bottom: 20px; padding:15px; border: 1px solid #ddd; background-color:#f5f5f5; border-radius:5px;">
                <h4>更改订单状态 (Change Order Status)</h4>
                <form action="${pageContext.request.contextPath}/admin/updateOrderStatus" method="POST">
                    <input type="hidden" name="orderId" value="${order.id}">
                    <label for="newStatus">新状态 (New Status):</label>
                    <select name="newStatus" id="newStatus">
                        <c:forEach var="statusValue" items="${orderStatusList}">
                            <option value="${statusValue}" ${statusValue == order.status ? 'selected' : ''}>
                                <c:out value="${statusValue}"/>
                            </option>
                        </c:forEach>
                    </select>
                    <button type="submit" class="admin-button update-status">更新状态 (Update Status)</button>
                </form>
            </div>

            <h4>订单商品 (Items in this Order)</h4>
            <c:if test="${not empty orderItemsList}">
                <table class="admin-table order-items-table">
                    <thead>
                        <tr>
                            <th>图片 (Image)</th>
                            <th>商品名称 (Product Name)</th>
                            <th>购买时单价 (Price at Purchase)</th>
                            <th>数量 (Quantity)</th>
                            <th>小计 (Subtotal)</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="item" items="${orderItemsList}">
                            <tr>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty item.productImageUrl}">
                                            <img src="${pageContext.request.contextPath}/${item.productImageUrl}" alt="<c:out value="${item.productName}"/>" style="width:60px; height:auto; border-radius:3px;">
                                        </c:when>
                                        <c:otherwise>
                                            <img src="${pageContext.request.contextPath}/images/placeholder.png" alt="No Image" style="width:60px; height:auto;">
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td><c:out value="${item.productName}"/></td>
                                <td><fmt:formatNumber value="${item.priceAtPurchase}" type="currency" currencySymbol="¥"/></td>
                                <td><c:out value="${item.quantity}"/></td>
                                <td><fmt:formatNumber value="${item.subtotal}" type="currency" currencySymbol="¥"/></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:if>
            <c:if test="${empty orderItemsList}">
                <p>此订单没有商品信息。(No items found for this order.)</p>
            </c:if>

            <p style="margin-top:20px;"><a href="${pageContext.request.contextPath}/admin/viewOrders" class="admin-button button-secondary back-link">返回订单列表 (Back to Order List)</a></p>

        </c:when>
        <c:otherwise>
            <p class="info-message">订单未找到。(Order not found.)</p>
            <p><a href="${pageContext.request.contextPath}/admin/viewOrders" class="admin-button button-secondary back-link">返回订单列表 (Back to Order List)</a></p>
        </c:otherwise>
    </c:choose>
</section>

<style>
    .admin-table { width: 100%; border-collapse: collapse; margin-top: 15px; font-size: 0.9em; }
    .admin-table th, .admin-table td { border: 1px solid #ddd; padding: 8px; text-align: left; vertical-align: middle; }
    .admin-table th { background-color: #f2f2f2; }
    .admin-button { padding: 8px 12px; text-decoration: none; border-radius: 4px; border: none; cursor: pointer; color: white !important; display: inline-block; margin-right: 5px; }
    .admin-button.update-status { background-color: #ffc107; color: #212529 !important; }
    .admin-button.update-status:hover { background-color: #e0a800; }
    .admin-button.back-link { background-color: #6c757d; }
    .admin-button.back-link:hover { background-color: #5a6268; }
    .change-status-form select { padding: 8px; margin-right: 10px; border-radius: 4px; border: 1px solid #ccc; }
</style>

<jsp:include page="/jsp/footer.jsp" />
