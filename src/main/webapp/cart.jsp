<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="jsp/header.jsp" />

<section id="shopping-cart-section">
    <h2>购物车 (Shopping Cart)</h2>

    <c:if test="${param.addSuccess == 'true'}">
        <span class="success-message">商品已成功添加到购物车！(Product successfully added to cart!)</span>
    </c:if>
    <c:if test="${param.updateSuccess == 'true'}">
        <span class="success-message">购物车已更新。(Cart updated successfully.)</span>
    </c:if>
    <c:if test="${param.removeSuccess == 'true'}">
        <span class="success-message">商品已从购物车移除。(Product removed from cart.)</span>
    </c:if>
    <c:if test="${not empty param.cartError}">
        <span class="error-message"><c:out value="${param.cartError}"/></span>
    </c:if>
    <c:if test="${not empty requestScope.errorMessage}">
        <span class="error-message"><c:out value="${requestScope.errorMessage}"/></span>
    </c:if>

    <c:choose>
        <c:when test="${empty cartItems}">
            <p class="info-message">您的购物车是空的。(Your cart is empty.)</p>
            <p><a href="${pageContext.request.contextPath}/products.jsp" class="button">继续购物 (Continue Shopping)</a></p>
        </c:when>
        <c:otherwise>
            <table class="cart-table">
                <thead>
                    <tr>
                        <th>图片 (Image)</th>
                        <th>商品 (Product)</th>
                        <th>单价 (Price)</th>
                        <th>数量 (Quantity)</th>
                        <th>小计 (Subtotal)</th>
                        <th>操作 (Action)</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="item" items="${cartItems}">
                        <tr>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty item.imageUrl}">
                                        <img src="${pageContext.request.contextPath}/${item.imageUrl}" alt="<c:out value="${item.productName}"/>" style="width:80px; height:auto;">
                                    </c:when>
                                    <c:otherwise>
                                        <img src="${pageContext.request.contextPath}/images/placeholder.png" alt="No Image" style="width:80px; height:auto;">
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td><a href="${pageContext.request.contextPath}/productDetail?id=${item.productId}"><c:out value="${item.productName}"/></a></td>
                            <td><fmt:formatNumber value="${item.productPrice}" type="currency" currencySymbol="¥"/></td>
                            <td>
                                <form action="${pageContext.request.contextPath}/updateCart" method="POST" style="display:inline;">
                                    <input type="hidden" name="cartItemId" value="${item.cartItemId}">
                                    <input type="hidden" name="action" value="update">
                                    <input type="number" name="quantity" value="${item.quantity}" min="1" max="99" style="width: 50px;" onchange="this.form.submit()">
                                    <%-- <button type="submit" class="update-btn">更新 (Update)</button> --%>
                                    <%-- Auto-submit onchange, or keep the button --%>
                                </form>
                            </td>
                            <td><fmt:formatNumber value="${item.subtotal}" type="currency" currencySymbol="¥"/></td>
                            <td>
                                <form action="${pageContext.request.contextPath}/updateCart" method="POST" style="display:inline;">
                                    <input type="hidden" name="cartItemId" value="${item.cartItemId}">
                                    <input type="hidden" name="action" value="remove">
                                    <button type="submit" class="remove-btn">删除 (Delete)</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>

            <div class="cart-summary">
                <h3>总计 (Total): <fmt:formatNumber value="${cartTotal}" type="currency" currencySymbol="¥"/></h3>
                <a href="${pageContext.request.contextPath}/checkout.jsp" class="checkout-button">去结算 (Proceed to Checkout)</a>
                <a href="${pageContext.request.contextPath}/products.jsp" class="continue-shopping-button">继续购物 (Continue Shopping)</a>
            </div>
        </c:otherwise>
    </c:choose>
</section>

<style>
    .cart-table {
        width: 100%;
        border-collapse: collapse;
        margin-top: 20px;
    }
    .cart-table th, .cart-table td {
        border: 1px solid #ddd;
        padding: 10px;
        text-align: left;
        vertical-align: middle;
    }
    .cart-table th {
        background-color: #f2f2f2;
    }
    .cart-table img {
        border-radius: 4px;
    }
    .cart-table .update-btn, .cart-table .remove-btn {
        padding: 5px 10px;
        border: none;
        border-radius: 4px;
        cursor: pointer;
        color: white;
    }
    .cart-table .update-btn {
        background-color: #5bc0de; /* Info blue */
    }
    .cart-table .update-btn:hover {
        background-color: #31b0d5;
    }
    .cart-table .remove-btn {
        background-color: #d9534f; /* Danger red */
    }
    .cart-table .remove-btn:hover {
        background-color: #c9302c;
    }
    .cart-summary {
        margin-top: 20px;
        text-align: right;
    }
    .cart-summary h3 {
        margin-bottom: 15px;
    }
    .checkout-button, .continue-shopping-button {
        padding: 10px 20px;
        text-decoration: none;
        border-radius: 4px;
        margin-left: 10px;
    }
    .checkout-button {
        background-color: #5cb85c; /* Success green */
        color: white;
    }
    .checkout-button:hover {
        background-color: #4cae4c;
    }
    .continue-shopping-button {
        background-color: #f0ad4e; /* Warning orange */
        color: white;
    }
    .continue-shopping-button:hover {
        background-color: #ec971f;
    }
</style>

<jsp:include page="jsp/footer.jsp" />
