<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="jsp/header.jsp" />

<section id="checkout-section">
    <h2>结算中心 (Checkout)</h2>

    <c:if test="${not empty requestScope.errorMessage}">
        <span class="error-message"><c:out value="${requestScope.errorMessage}"/></span>
    </c:if>
    <c:if test="${not empty param.orderError}">
        <span class="error-message"><c:out value="${param.orderError}"/></span>
    </c:if>

    <div class="checkout-container">
        <div class="order-summary-panel">
            <h3>订单摘要 (Order Summary)</h3>
            <c:if test="${not empty cartItems}">
                <table class="summary-table">
                    <thead>
                        <tr>
                            <th>商品 (Product)</th>
                            <th>数量 (Qty)</th>
                            <th>单价 (Price)</th>
                            <th>小计 (Subtotal)</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="item" items="${cartItems}">
                            <tr>
                                <td><c:out value="${item.productName}"/></td>
                                <td><c:out value="${item.quantity}"/></td>
                                <td><fmt:formatNumber value="${item.productPrice}" type="currency" currencySymbol="¥"/></td>
                                <td><fmt:formatNumber value="${item.subtotal}" type="currency" currencySymbol="¥"/></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                <h4 class="summary-total">订单总计 (Total): <fmt:formatNumber value="${cartTotal}" type="currency" currencySymbol="¥"/></h4>
            </c:if>
            <c:if test="${empty cartItems}">
                <p class="info-message">您的购物车是空的，无法结算。(Your cart is empty. Cannot proceed to checkout.)</p>
                <p><a href="${pageContext.request.contextPath}/products.jsp" class="button">去购物 (Go Shopping)</a></p>
            </c:if>
        </div>

        <div class="shipping-details-panel">
            <h3>收货信息 (Shipping Details)</h3>
            <c:if test="${not empty cartItems}"> <%-- Only show form if cart is not empty --%>
                <form action="${pageContext.request.contextPath}/createOrder" method="POST">
                    <div class="form-group">
                        <label for="recipientName">收货人姓名:</label>
                        <input type="text" id="recipientName" name="recipientName" value="<c:out value='${sessionScope.loggedInUser.username}'/>" required>
                    </div>
                    <div class="form-group">
                        <label for="shippingAddress">收货地址:</label>
                        <textarea id="shippingAddress" name="shippingAddress" rows="3" required></textarea>
                    </div>
                    <div class="form-group">
                        <label for="contactPhone">联系电话:</label>
                        <input type="tel" id="contactPhone" name="contactPhone" value="<c:out value='${userPhoneNumber}'/>" required>
                    </div>
                    <div class="form-group">
                        <button type="submit" class="button-primary place-order-button">提交订单 (Place Order)</button>
                    </div>
                </form>
            </c:if>
        </div>
    </div>
</section>

<style>
    .checkout-container {
        display: flex;
        flex-wrap: wrap;
        gap: 30px;
        margin-top: 20px;
    }
    .order-summary-panel, .shipping-details-panel {
        border: 1px solid #ddd;
        padding: 20px;
        border-radius: 5px;
        background-color: #f9f9f9;
    }
    .order-summary-panel {
        flex: 1;
        min-width: 300px;
    }
    .shipping-details-panel {
        flex: 1.5;
        min-width: 350px;
    }
    .summary-table {
        width: 100%;
        border-collapse: collapse;
        margin-bottom: 15px;
    }
    .summary-table th, .summary-table td {
        border-bottom: 1px solid #eee;
        padding: 8px 0;
        text-align: left;
    }
    .summary-table th:last-child, .summary-table td:last-child {
        text-align: right;
    }
    .summary-total {
        text-align: right;
        font-size: 1.3em;
        color: #333;
    }
    .shipping-details-panel label {
        display: block;
        margin-bottom: 5px;
        font-weight: bold;
    }
    .shipping-details-panel input[type="text"],
    .shipping-details-panel input[type="tel"],
    .shipping-details-panel textarea {
        width: 100%;
        padding: 8px;
        margin-bottom: 15px;
        border: 1px solid #ccc;
        border-radius: 4px;
        box-sizing: border-box;
    }
    .place-order-button {
        padding: 12px 25px;
        background-color: #5cb85c;
        color: white;
        border: none;
        border-radius: 4px;
        cursor: pointer;
        font-size: 1.1em;
    }
    .place-order-button:hover {
        background-color: #4cae4c;
    }
</style>

<jsp:include page="jsp/footer.jsp" />
