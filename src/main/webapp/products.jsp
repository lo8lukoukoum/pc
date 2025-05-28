<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:include page="jsp/header.jsp" />

<section id="product-list-section">
    <c:choose>
        <c:when test="${not empty requestScope.searchQuery}">
            <h2>搜索结果: "<c:out value="${requestScope.searchQuery}"/>" (Search Results for: "<c:out value="${requestScope.searchQuery}"/>")</h2>
        </c:when>
        <c:otherwise>
            <h2>商品列表 (Product List)</h2>
        </c:otherwise>
    </c:choose>

    <c:if test="${not empty errorMessage}">
        <p style="color:red;"><c:out value="${errorMessage}"/></p>
    </c:if>

    <c:choose>
        <c:when test="${empty productList}">
            <p>当前没有可供展示的商品。(No products available at the moment.)</p>
        </c:when>
        <c:otherwise>
            <div class="product-grid">
                <c:forEach var="product" items="${productList}">
                    <div class="product-item">
                        <a href="${pageContext.request.contextPath}/productDetail?id=${product.id}">
                            <c:choose>
                                <c:when test="${not empty product.imageUrl}">
                                    <img src="${pageContext.request.contextPath}/${product.imageUrl}" alt="<c:out value="${product.name}"/>" style="width:150px; height:150px; object-fit: cover;">
                                </c:when>
                                <c:otherwise>
                                    <img src="${pageContext.request.contextPath}/images/placeholder.png" alt="No Image Available" style="width:150px; height:150px; object-fit: cover;">
                                </c:otherwise>
                            </c:choose>
                            <h3><c:out value="${product.name}"/></h3>
                        </a>
                        <p class="price"><fmt:formatNumber value="${product.price}" type="currency" currencySymbol="¥"/></p>
                        <c:if test="${not empty product.description}">
                            <p class="description"><c:out value="${fn:substring(product.description, 0, 50)}"/>...</p>
                        </c:if>
                        <a href="${pageContext.request.contextPath}/productDetail?id=${product.id}" class="details-button">查看详情 (View Details)</a>
                        
                        <c:if test="${product.status == '上架' && product.stock > 0}">
                            <form action="${pageContext.request.contextPath}/addToCart" method="POST" style="display: inline; margin-top:5px;">
                                <input type="hidden" name="productId" value="${product.id}">
                                <input type="hidden" name="quantity" value="1"> <%-- Default quantity to 1 for quick add --%>
                                <button type="submit" class="add-to-cart-button">加入购物车 (Add to Cart)</button>
                            </form>
                        </c:if>
                        <c:if test="${product.status != '上架' || product.stock <= 0}">
                             <button type="button" class="add-to-cart-button" disabled style="margin-top:5px;">
                                <c:choose>
                                    <c:when test="${product.stock <= 0}">暂时无货</c:when>
                                    <c:otherwise>已下架</c:otherwise>
                                </c:choose>
                             </button>
                        </c:if>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>

    <div class="pagination" style="text-align: center; margin-top: 20px;">
        <!-- Placeholder for Pagination Controls -->
        <p><em>分页功能将在后续实现。(Pagination will be implemented later.)</em></p>
    </div>

</section>

<style>
    .product-grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
        gap: 20px;
        padding: 20px;
    }
    .product-item {
        border: 1px solid #ddd;
        padding: 15px;
        text-align: center;
        background-color: #f9f9f9;
    }
    .product-item img {
        max-width: 100%;
        height: auto;
        margin-bottom: 10px;
        border-radius: 4px;
    }
    .product-item h3 {
        font-size: 1.2em;
        margin-bottom: 5px;
    }
    .product-item .price {
        font-size: 1.1em;
        color: #333;
        font-weight: bold;
        margin-bottom: 10px;
    }
    .product-item .description {
        font-size: 0.9em;
        color: #666;
        margin-bottom: 10px;
        height: 40px; /* Adjusted height for description */
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
    }
    .product-item button, .product-item .details-button, .product-item .add-to-cart-button {
        padding: 8px 15px;
        background-color: #5cb85c;
        color: white !important;
        border: none;
        border-radius: 4px;
        cursor: pointer;
        text-decoration: none;
        display: inline-block;
        font-size: 0.9em;
        /* Ensure buttons in product item don't cause excessive height */
        vertical-align: middle; 
    }
    .product-item button:hover, .product-item .details-button:hover, .product-item .add-to-cart-button:hover {
        background-color: #4cae4c;
        color: white !important;
    }
    .product-item .add-to-cart-button:disabled {
        background-color: #ccc;
        cursor: not-allowed;
    }
    .product-item a {
        text-decoration: none;
        color: inherit;
    }
</style>

<jsp:include page="jsp/footer.jsp" />
