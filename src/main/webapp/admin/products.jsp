<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/jsp/header.jsp" />

<section id="admin-product-list-section" style="padding: 20px;">
    <h2>商品管理 (Product Management)</h2>

    <p style="margin-bottom: 15px;">
        <a href="${pageContext.request.contextPath}/admin/product_form.jsp" class="admin-button add-new">添加新商品 (Add New Product)</a>
    </p>

    <c:if test="${not empty errorMessage}">
        <span class="error-message"><c:out value="${errorMessage}"/></span>
    </c:if>
    <c:if test="${param.saveSuccess == 'true'}">
        <span class="success-message">商品已成功保存！(Product saved successfully!)</span>
    </c:if>
    <c:if test="${not empty param.deleteSuccess}">
        <span class="success-message">商品 ID ${param.productId} 已成功删除。(Product ID ${param.productId} deleted successfully.)</span>
    </c:if>
    <c:if test="${not empty param.deleteError}">
        <span class="error-message"><c:out value="${param.deleteError}"/></span>
    </c:if>

    <c:choose>
        <c:when test="${empty productList}">
            <p class="info-message">没有找到商品。(No products found.)</p>
        </c:when>
        <c:otherwise>
            <table class="admin-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>图片 (Image)</th>
                        <th>商品名称 (Name)</th>
                        <th>分类 (Category)</th>
                        <th>价格 (Price)</th>
                        <th>库存 (Stock)</th>
                        <th>状态 (Status)</th>
                        <th>销量 (Sales)</th>
                        <th>浏览量 (Views)</th>
                        <th>操作 (Actions)</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="product" items="${productList}">
                        <tr>
                            <td><c:out value="${product.id}"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty product.imageUrl}">
                                        <img src="${pageContext.request.contextPath}/${product.imageUrl}" alt="<c:out value="${product.name}"/>" style="width:60px; height:auto; border-radius:3px;">
                                    </c:when>
                                    <c:otherwise>
                                        <img src="${pageContext.request.contextPath}/images/placeholder.png" alt="No Image" style="width:60px; height:auto;">
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td><c:out value="${product.name}"/></td>
                            <td><c:out value="${product.categoryName}"/></td>
                            <td><fmt:formatNumber value="${product.price}" type="currency" currencySymbol="¥"/></td>
                            <td><c:out value="${product.stock}"/></td>
                            <td><c:out value="${product.status}"/></td>
                            <td><c:out value="${product.salesCount}"/></td>
                            <td><c:out value="${product.views}"/></td>
                            <td>
                                <a href="${pageContext.request.contextPath}/admin/loadProductForm?id=${product.id}" class="admin-button edit">编辑 (Edit)</a>
                                <form action="${pageContext.request.contextPath}/admin/deleteProduct" method="POST" style="display:inline;" onsubmit="return confirm('您确定要删除此商品吗？此操作不可撤销。(Are you sure you want to delete this product? This action cannot be undone.)');">
                                    <input type="hidden" name="productId" value="${product.id}">
                                    <button type="submit" class="admin-button delete">删除 (Delete)</button>
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
        color: white !important; /* Important for specificity */
        display: inline-block;
    }
    .admin-button.add-new {
        background-color: #5cb85c; /* Green */
    }
    .admin-button.add-new:hover {
        background-color: #4cae4c;
    }
    .admin-button.edit {
        background-color: #f0ad4e; /* Orange */
    }
     .admin-button.edit:hover {
        background-color: #ec971f;
    }
    .admin-button.delete {
        background-color: #d9534f; /* Red */
    }
    .admin-button.delete:hover {
        background-color: #c9302c;
    }
</style>

<jsp:include page="/jsp/footer.jsp" />
