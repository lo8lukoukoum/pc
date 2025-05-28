<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="jsp/header.jsp" />

<section id="product-detail-section">
    <c:choose>
        <c:when test="${not empty product}">
            <h2>商品详情 (Product Details)</h2>
            <div class="product-detail-container">
                <div class="product-image-lg">
                    <c:choose>
                        <c:when test="${not empty product.imageUrl}">
                            <img src="${pageContext.request.contextPath}/${product.imageUrl}" alt="<c:out value="${product.name}"/>" style="max-width: 400px; height: auto;">
                        </c:when>
                        <c:otherwise>
                            <img src="${pageContext.request.contextPath}/images/placeholder_lg.png" alt="No Image Available" style="max-width: 400px; height: auto;">
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="product-info">
                    <h3><c:out value="${product.name}"/></h3>
                    <p class="price">价格 (Price): <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="¥"/></p>
                    <p>描述 (Description): <c:out value="${product.description}"/></p>
                    <p>库存 (Stock): <c:out value="${product.stock}"/> 件</p>
                    <p>状态 (Status): <c:out value="${product.status}"/></p>
                    <p>浏览量 (Views): <c:out value="${product.views}"/></p>
                    <p>销量 (Sales): <c:out value="${product.salesCount}"/></p>
                    <p>上架日期 (Listed): <fmt:formatDate value="${product.creationDate}" pattern="yyyy-MM-dd HH:mm:ss"/></p>
                    <%-- <p>分类 (Category): <c:out value="${product.categoryName}"/> </p> --%>
                    
                    <c:if test="${product.status == '上架'}">
                        <form action="${pageContext.request.contextPath}/addToCart" method="POST" style="margin-top: 20px;">
                            <input type="hidden" name="productId" value="${product.id}">
                            <label for="quantity">数量 (Quantity):</label>
                            <input type="number" id="quantity" name="quantity" value="1" min="1" max="${product.stock}" style="width: 60px;" <c:if test="${product.stock == 0}">disabled</c:if>>
                            <button type="submit" <c:if test="${product.stock <= 0}">disabled</c:if>>
                                <c:choose>
                                    <c:when test="${product.stock > 0}">加入购物车 (Add to Cart)</c:when>
                                    <c:otherwise>暂时无货 (Out of Stock)</c:otherwise>
                                </c:choose>
                            </button>
                        </form>
                    </c:if>
                    <c:if test="${product.status != '上架'}">
                        <p style="color:red; font-weight:bold; margin-top:20px;">该商品当前已下架。(This product is currently unavailable.)</p>
                    </c:if>
                </div>
            </div>

            <div id="product-reviews" style="margin-top: 30px;">
                <h4>用户评价 (Customer Reviews)</h4>
                <c:choose>
                    <c:when test="${not empty reviewList}">
                        <c:forEach var="review" items="${reviewList}">
                            <div class="review-item" style="border: 1px solid #eee; padding: 10px; margin-bottom: 10px; border-radius: 5px;">
                                <p>
                                    <strong>评价者 (Reviewer):</strong> 
                                    <c:choose>
                                        <c:when test="${review.anonymous}">匿名用户 (Anonymous)</c:when>
                                        <c:otherwise><c:out value="${review.username}"/></c:otherwise>
                                    </c:choose>
                                </p>
                                <p>
                                    <strong>评分 (Rating):</strong> 
                                    <c:forEach begin="1" end="${review.rating}">★</c:forEach><c:forEach begin="${review.rating + 1}" end="5">☆</c:forEach>
                                    (<c:out value="${review.rating}"/>/5)
                                </p>
                                <p><strong>评论 (Comment):</strong> <c:out value="${review.comment}"/></p>
                                <c:if test="${not empty review.imageUrl}">
                                    <p>
                                        <img src="${pageContext.request.contextPath}/${review.imageUrl}" alt="Review Image" style="max-width:200px; max-height:200px; border-radius:4px;">
                                    </p>
                                </c:if>
                                <p style="font-size:0.9em; color:#777;"><em><fmt:formatDate value="${review.reviewDate}" pattern="yyyy-MM-dd HH:mm"/></em></p>
                            </div>
                            <hr style="border:0; border-top:1px solid #f0f0f0;">
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <p>暂无评价。(No reviews yet.)</p>
                    </c:otherwise>
                </c:choose>
            </div>

        </c:when>
        <c:otherwise>
             <h2>商品未找到 (Product Not Found)</h2>
             <p>抱歉，您请求的商品不存在或已被下架。(Sorry, the product you requested does not exist or has been removed.)</p>
             <p><a href="${pageContext.request.contextPath}/products.jsp">返回商品列表 (Back to Product List)</a></p>
        </c:otherwise>
    </c:choose>
</section>

<style>
    .product-detail-container {
        display: flex;
        gap: 30px;
        margin-top: 20px;
    }
    .product-image-lg img {
        border: 1px solid #ddd;
        border-radius: 5px;
    }
    .product-info {
        flex-grow: 1;
    }
    .product-info h3 {
        font-size: 1.8em;
        margin-bottom: 10px;
    }
    .product-info .price {
        font-size: 1.5em;
        color: #d9534f;
        font-weight: bold;
        margin-bottom: 15px;
    }
    .product-info p {
        margin-bottom: 10px;
        line-height: 1.6;
    }
    .product-info button {
        padding: 10px 20px;
        background-color: #5cb85c;
        color: white;
        border: none;
        border-radius: 4px;
        cursor: pointer;
        font-size: 1em;
    }
    .product-info button:hover {
        background-color: #4cae4c;
    }
    .product-info button:disabled {
        background-color: #ccc;
        cursor: not-allowed;
    }
</style>

<jsp:include page="jsp/footer.jsp" />
                            </c:choose>
                        </button>
                    </form>
                </div>
            </div>

            <div id="product-reviews" style="margin-top: 30px;">
                <h4>用户评价 (Product Reviews)</h4>
                <p><em>评价功能将在后续实现。(Reviews will be implemented later.)</em></p>
            </div>

        </c:when>
        <c:otherwise>
             <h2>商品未找到 (Product Not Found)</h2>
             <p>抱歉，您请求的商品不存在或已被下架。(Sorry, the product you requested does not exist or has been removed.)</p>
             <p><a href="${pageContext.request.contextPath}/products.jsp">返回商品列表 (Back to Product List)</a></p>
        </c:otherwise>
    </c:choose>
</section>

<style>
    .product-detail-container {
        display: flex;
        gap: 30px;
        margin-top: 20px;
    }
    .product-image-lg img {
        border: 1px solid #ddd;
        border-radius: 5px;
    }
    .product-info {
        flex-grow: 1;
    }
    .product-info h3 {
        font-size: 1.8em;
        margin-bottom: 10px;
    }
    .product-info .price {
        font-size: 1.5em;
        color: #d9534f;
        font-weight: bold;
        margin-bottom: 15px;
    }
    .product-info p {
        margin-bottom: 10px;
        line-height: 1.6;
    }
    .product-info button {
        padding: 10px 20px;
        background-color: #5cb85c;
        color: white;
        border: none;
        border-radius: 4px;
        cursor: pointer;
        font-size: 1em;
    }
    .product-info button:hover {
        background-color: #4cae4c;
    }
    .product-info button:disabled {
        background-color: #ccc;
        cursor: not-allowed;
    }
</style>

<jsp:include page="jsp/footer.jsp" />
