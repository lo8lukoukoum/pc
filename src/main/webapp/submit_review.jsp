<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="jsp/header.jsp" />

<section id="submit-review-section">
    <h2>发表评价 (Submit Review)</h2>

    <c:if test="${not empty requestScope.errorMessage}">
        <p style="color:red;"><c:out value="${requestScope.errorMessage}"/></p>
    </c:if>
    <c:if test="${not empty param.reviewError}">
        <p style="color:red;"><c:out value="${param.reviewError}"/></p>
    </c:if>

    <c:if test="${not empty productToReview && not empty orderIdForReview}">
        <div class="product-review-info">
            <h3>您正在评价 (You are reviewing): <c:out value="${productToReview.name}"/></h3>
            <c:if test="${not empty productToReview.imageUrl}">
                <img src="${pageContext.request.contextPath}/${productToReview.imageUrl}" alt="<c:out value="${productToReview.name}"/>" style="width:100px; height:auto; border:1px solid #ddd; margin-bottom:10px;">
            </c:if>
        </div>

        <form action="${pageContext.request.contextPath}/submitReview" method="POST" enctype="multipart/form-data" id="reviewForm">
            <input type="hidden" name="productId" value="${productToReview.id}">
            <input type="hidden" name="orderId" value="${orderIdForReview}">

            <div class="form-group">
                <label for="rating">评分 (Rating):</label>
                <div class="rating-stars">
                    <input type="radio" id="star5" name="rating" value="5" required><label for="star5" title="5 stars">☆</label>
                    <input type="radio" id="star4" name="rating" value="4"><label for="star4" title="4 stars">☆</label>
                    <input type="radio" id="star3" name="rating" value="3"><label for="star3" title="3 stars">☆</label>
                    <input type="radio" id="star2" name="rating" value="2"><label for="star2" title="2 stars">☆</label>
                    <input type="radio" id="star1" name="rating" value="1"><label for="star1" title="1 star">☆</label>
                </div>
            </div>

            <div class="form-group">
                <label for="comment">评论内容 (Comment):</label>
                <textarea id="comment" name="comment" rows="5" required></textarea>
            </div>

            <div class="form-group">
                <label for="reviewImage">上传图片 (Upload Image - Optional):</label>
                <input type="file" id="reviewImage" name="reviewImage" accept="image/*">
            </div>

            <div class="form-group">
                <input type="checkbox" id="isAnonymous" name="isAnonymous" value="true">
                <label for="isAnonymous">匿名评价 (Anonymous Review)</label>
            </div>

            <div class="form-group">
                <button type="submit">提交评价 (Submit Review)</button>
            </div>
        </form>
    </c:if>

    <c:if test="${empty productToReview || empty orderIdForReview}">
        <p>无法加载评价表单，缺少商品或订单信息。(Could not load review form, missing product or order information.)</p>
        <p><a href="${pageContext.request.contextPath}/order_history.jsp">返回订单列表 (Back to Order History)</a></p>
    </c:if>
</section>

<style>
    .product-review-info {
        margin-bottom: 20px;
        padding-bottom: 15px;
        border-bottom: 1px solid #eee;
    }
    .form-group {
        margin-bottom: 15px;
    }
    .form-group label {
        display: block;
        margin-bottom: 5px;
        font-weight: bold;
    }
    .form-group textarea, .form-group input[type="file"] {
        width: 100%;
        padding: 8px;
        border: 1px solid #ccc;
        border-radius: 4px;
        box-sizing: border-box;
    }
    .rating-stars {
        display: inline-block;
        direction: rtl; /* Right to left to make stars fill from right */
    }
    .rating-stars input[type="radio"] {
        display: none; /* Hide the actual radio button */
    }
    .rating-stars label {
        font-size: 2em;
        color: #ddd; /* Default empty star color */
        cursor: pointer;
        padding: 0 0.1em;
        display: inline-block; /* Ensure labels are on the same line */
    }
    /* When a radio button is checked, color it and all previous (visual) stars */
    .rating-stars input[type="radio"]:checked ~ label {
        color: #f5b301; /* Filled star color */
    }
    /* Hover effect: color the star and previous ones */
    .rating-stars label:hover,
    .rating-stars label:hover ~ label {
        color: #f5b301;
    }
    .form-group button {
        padding: 10px 20px;
        background-color: #5cb85c;
        color: white;
        border: none;
        border-radius: 4px;
        cursor: pointer;
    }
    .form-group button:hover {
        background-color: #4cae4c;
    }
</style>

<jsp:include page="jsp/footer.jsp" />
