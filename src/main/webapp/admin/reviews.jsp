<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/jsp/header.jsp" />

<section id="admin-review-list-section" style="padding: 20px;">
    <h2>评价审核 (Review Moderation)</h2>

    <form action="${pageContext.request.contextPath}/admin/viewReviews" method="GET" style="margin-bottom: 20px;">
        <label for="statusFilter">筛选状态 (Filter by Status):</label>
        <select name="statusFilter" id="statusFilter">
            <option value="ALL" ${currentStatusFilter == 'ALL' ? 'selected' : ''}>全部 (All)</option>
            <option value="待审核" ${currentStatusFilter == '待审核' ? 'selected' : ''}>待审核 (Pending)</option>
            <option value="已批准" ${currentStatusFilter == '已批准' ? 'selected' : ''}>已批准 (Approved)</option>
            <option value="已拒绝" ${currentStatusFilter == '已拒绝' ? 'selected' : ''}>已拒绝 (Rejected)</option>
        </select>
        <button type="submit" class="admin-button filter">筛选 (Filter)</button>
    </form>

    <c:if test="${not empty errorMessage}">
        <span class="error-message"><c:out value="${errorMessage}"/></span>
    </c:if>
    <c:if test="${param.statusUpdateSuccess == 'true'}">
        <span class="success-message">评价 ID ${param.reviewId} 的状态已更新为: <strong><c:out value="${param.newStatus}"/></strong>.</span>
    </c:if>
    <c:if test="${not empty param.statusUpdateError}">
        <span class="error-message">更新评价状态失败 (ID: ${param.reviewId}): <c:out value="${param.statusUpdateError}"/></span>
    </c:if>

    <c:choose>
        <c:when test="${empty reviewList}">
            <p class="info-message">没有符合条件的评价。(No reviews match criteria.)</p>
        </c:when>
        <c:otherwise>
            <table class="admin-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>商品 (Product)</th>
                        <th>用户 (User)</th>
                        <th>评分 (Rating)</th>
                        <th style="min-width: 200px;">评论 (Comment)</th>
                        <th>图片 (Image)</th>
                        <th>日期 (Date)</th>
                        <th>状态 (Status)</th>
                        <th>操作 (Actions)</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="review" items="${reviewList}">
                        <tr>
                            <td><c:out value="${review.id}"/></td>
                            <td><a href="${pageContext.request.contextPath}/productDetail?id=${review.productId}" target="_blank"><c:out value="${review.productName}"/> (ID: ${review.productId})</a></td>
                            <td>
                                <c:choose>
                                    <c:when test="${review.anonymous}">匿名用户 (Anonymous)</c:when>
                                    <c:otherwise><c:out value="${review.username}"/> (ID: ${review.userId})</c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:forEach begin="1" end="${review.rating}">★</c:forEach><c:forEach begin="${review.rating + 1}" end="5">☆</c:forEach>
                            </td>
                            <td><c:out value="${review.comment}"/></td>
                            <td>
                                <c:if test="${not empty review.imageUrl}">
                                    <a href="${pageContext.request.contextPath}/${review.imageUrl}" target="_blank">
                                        <img src="${pageContext.request.contextPath}/${review.imageUrl}" alt="Review Image" style="width:80px; height:auto; border-radius:3px;">
                                    </a>
                                </c:if>
                            </td>
                            <td><fmt:formatDate value="${review.reviewDate}" pattern="yyyy-MM-dd HH:mm"/></td>
                            <td><c:out value="${review.status}"/></td>
                            <td>
                                <form action="${pageContext.request.contextPath}/admin/updateReviewStatus" method="POST" style="display:inline-block; margin-bottom: 5px;">
                                    <input type="hidden" name="reviewId" value="${review.id}">
                                    <c:choose>
                                        <c:when test="${review.status == '待审核'}">
                                            <input type="hidden" name="newStatus" value="已批准">
                                            <button type="submit" class="admin-button approve">批准 (Approve)</button>
                                        </c:when>
                                        <c:when test="${review.status == '已拒绝'}">
                                             <input type="hidden" name="newStatus" value="已批准">
                                            <button type="submit" class="admin-button approve">重新批准 (Re-Approve)</button>
                                        </c:when>
                                        <c:when test="${review.status == '已批准'}">
                                            <%-- No direct approve button, maybe a "Pending" button if needed --%>
                                        </c:when>
                                    </c:choose>
                                </form>
                                <form action="${pageContext.request.contextPath}/admin/updateReviewStatus" method="POST" style="display:inline-block;">
                                    <input type="hidden" name="reviewId" value="${review.id}">
                                    <c:choose>
                                        <c:when test="${review.status == '待审核' || review.status == '已批准'}">
                                            <input type="hidden" name="newStatus" value="已拒绝">
                                            <button type="submit" class="admin-button reject">拒绝 (Reject)</button>
                                        </c:when>
                                         <c:when test="${review.status == '已拒绝'}">
                                            <%-- No direct reject button if already rejected --%>
                                        </c:when>
                                    </c:choose>
                                </form>
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
    .admin-table { width: 100%; border-collapse: collapse; margin-top: 20px; font-size: 0.9em; }
    .admin-table th, .admin-table td { border: 1px solid #ddd; padding: 8px; text-align: left; vertical-align: middle; }
    .admin-table th { background-color: #f2f2f2; }
    .admin-button { padding: 5px 10px; text-decoration: none; border-radius: 3px; margin-right: 5px; color: white !important; display: inline-block; border: none; cursor: pointer; }
    .admin-button.filter { background-color: #007bff; } /* Blue */
    .admin-button.filter:hover { background-color: #0056b3; }
    .admin-button.approve { background-color: #28a745; } /* Green */
    .admin-button.approve:hover { background-color: #1e7e34; }
    .admin-button.reject { background-color: #dc3545; } /* Red */
    .admin-button.reject:hover { background-color: #bd2130; }
</style>

<jsp:include page="/jsp/footer.jsp" />
