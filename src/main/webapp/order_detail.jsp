<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="jsp/header.jsp" />

<section id="order-detail-section">
    <h2>订单详情 (Order Details)</h2>

    <c:if test="${not empty requestScope.errorMessage}">
        <p style="color:red;"><c:out value="${requestScope.errorMessage}"/></p>
    </c:if>

    <c:choose>
        <c:when test="${not empty order}">
            <div class="order-info-container">
                <h3>订单号 (Order ID): #<c:out value="${order.id}"/></h3>
                <p><strong>下单日期 (Order Date):</strong> <fmt:formatDate value="${order.orderDate}" pattern="yyyy-MM-dd HH:mm:ss"/></p>
                <p><strong>订单状态 (Status):</strong> <c:out value="${order.status}"/></p>
                <p><strong>总金额 (Total Amount):</strong> <fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="¥"/></p>
                <p><strong>收货地址 (Shipping Address):</strong> <c:out value="${order.shippingAddress}"/></p>
                <p><strong>联系电话 (Contact Phone):</strong> <c:out value="${order.contactPhone}"/></p>
            </div>

            <h4>订单商品 (Items in this Order)</h4>
            <c:if test="${not empty orderItemsList}">
                <table class="order-items-table">
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
                                            <img src="${pageContext.request.contextPath}/${item.productImageUrl}" alt="<c:out value="${item.productName}"/>" style="width:80px; height:auto;">
                                        </c:when>
                                        <c:otherwise>
                                            <img src="${pageContext.request.contextPath}/images/placeholder.png" alt="No Image" style="width:80px; height:auto;">
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td><c:out value="${item.productName}"/></td>
                                <td><fmt:formatNumber value="${item.priceAtPurchase}" type="currency" currencySymbol="¥"/></td>
                                <td><c:out value="${item.quantity}"/></td>
                                <td><fmt:formatNumber value="${item.subtotal}" type="currency" currencySymbol="¥"/></td>
                                <td>
                                    <c:if test="${order.status == '已完成'}">
                                        <a href="${pageContext.request.contextPath}/loadReviewForm?productId=${item.productId}&orderId=${order.id}">评价商品 (Review Product)</a>
                                    </c:if>
                                    <c:if test="${order.status != '已完成'}">
                                        <em>(Review after order completion)</em>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:if>
            <c:if test="${empty orderItemsList}">
                <p>此订单没有商品信息。(No items found for this order.)</p>
            </c:if>

            <p style="margin-top:20px;"><a href="${pageContext.request.contextPath}/orderHistory" class="back-link">返回订单列表 (Back to Order History)</a></p>

        </c:when>
        <c:otherwise>
            <p>订单未找到或您没有权限查看此订单。(Order not found or you do not have permission to view this order.)</p>
            <p><a href="${pageContext.request.contextPath}/orderHistory">返回订单列表 (Back to Order History)</a></p>
        </c:otherwise>
    </c:choose>
</section>

<style>
    .order-info-container {
        background-color: #f9f9f9;
        padding: 15px;
        border-radius: 5px;
        margin-bottom: 20px;
        border: 1px solid #eee;
    }
    .order-info-container h3 {
        margin-top: 0;
    }
    .order-info-container p {
        margin: 5px 0;
    }
    .order-items-table {
        width: 100%;
        border-collapse: collapse;
        margin-top: 15px;
    }
    .order-items-table th, .order-items-table td {
        border: 1px solid #ddd;
        padding: 10px;
        text-align: left;
        vertical-align: middle;
    }
    .order-items-table th {
        background-color: #f2f2f2;
    }
    .order-items-table img {
        border-radius: 4px;
    }
    .back-link {
        display: inline-block;
        padding: 8px 15px;
        background-color: #007bff;
        color: white;
        text-decoration: none;
        border-radius: 4px;
    }
    .back-link:hover {
        background-color: #0056b3;
    }
</style>

<jsp:include page="jsp/footer.jsp" />
