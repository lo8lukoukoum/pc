<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/jsp/header.jsp" />

<section id="admin-stats-section" style="padding: 20px;">
    <h2>数据与统计 (Data & Statistics)</h2>

    <c:if test="${not empty statsErrorMessage}">
        <span class="error-message"><c:out value="${statsErrorMessage}"/></span>
    </c:if>

    <div class="stats-summary" style="margin-bottom: 30px; display: flex; flex-wrap: wrap; gap: 20px;">
        <div class="stat-card"><strong>总用户数:</strong> <c:out value="${totalUsers}"/></div>
        <div class="stat-card"><strong>总商品数:</strong> <c:out value="${totalProducts}"/></div>
        <div class="stat-card"><strong>总订单数:</strong> <c:out value="${totalOrders}"/></div>
        <div class="stat-card"><strong>总销售额 (已完成):</strong> <fmt:formatNumber value="${totalRevenue}" type="currency" currencySymbol="¥"/></div>
    </div>

    <div class="stats-columns" style="display: flex; flex-wrap: wrap; gap: 30px;">
        <div class="stats-column" style="flex: 1; min-width: 300px;">
            <h3>热销商品 Top 10 (Top 10 Selling Products)</h3>
            <c:choose>
                <c:when test="${not empty topSellingProducts}">
                    <ol>
                        <c:forEach var="product" items="${topSellingProducts}">
                            <li><c:out value="${product.name}"/> (销量: <c:out value="${product.sales_count}"/>)</li>
                        </c:forEach>
                    </ol>
                </c:when>
                <c:otherwise><p>暂无销售数据。(No sales data available.)</p></c:otherwise>
            </c:choose>
        </div>

        <div class="stats-column" style="flex: 1; min-width: 300px;">
            <h3>浏览量 Top 10 (Top 10 Viewed Products)</h3>
            <c:choose>
                <c:when test="${not empty mostViewedProducts}">
                    <ol>
                        <c:forEach var="product" items="${mostViewedProducts}">
                            <li><c:out value="${product.name}"/> (浏览量: <c:out value="${product.views}"/>)</li>
                        </c:forEach>
                    </ol>
                </c:when>
                <c:otherwise><p>暂无浏览数据。(No view data available.)</p></c:otherwise>
            </c:choose>
        </div>
    </div>

    <div class="export-data-section" style="margin-top: 30px; padding-top:20px; border-top:1px solid #eee;">
        <h3>导出数据 (Export Data)</h3>
        <p>
            <a href="${pageContext.request.contextPath}/admin/exportData?type=sales" class="admin-button export">导出销售数据 (Export Sales Data)</a>
            <button type="button" class="admin-button export" onclick="alert('导出商品数据功能待实现 (Export Product Data to be implemented)');">导出商品数据 (Export Product Data)</button>
        </p>
    </div>

</section>

<style>
    .stats-summary {
        display: flex;
        gap: 20px;
        margin-bottom: 20px;
    }
    .stat-card {
        background-color: #f8f9fa;
        border: 1px solid #dee2e6;
        border-radius: .25rem;
        padding: 15px;
        flex-grow: 1;
        text-align: center;
        font-size: 1.1em;
    }
    .stat-card strong {
        display: block;
        margin-bottom: 5px;
        color: #495057;
    }
    .stats-columns {
        display: flex;
        gap: 20px;
        flex-wrap: wrap; /* Allow wrapping on smaller screens */
    }
    .stats-column {
        flex: 1; /* Each column takes equal width */
        padding: 15px;
        background-color: #fff;
        border: 1px solid #ddd;
        border-radius: 5px;
    }
    .stats-column h3 {
        margin-top: 0;
        border-bottom: 1px solid #eee;
        padding-bottom: 10px;
        margin-bottom: 10px;
    }
    .stats-column ol {
        padding-left: 20px;
    }
    .stats-column ol li {
        margin-bottom: 8px;
    }
    .admin-button.export {
        background-color: #17a2b8; /* Info Blue */
        color: white !important;
        padding: 8px 15px;
        text-decoration: none;
        border-radius: 4px;
        border: none;
        cursor: pointer;
        margin-right: 10px;
    }
    .admin-button.export:hover {
        background-color: #138496;
    }
</style>

<jsp:include page="/jsp/footer.jsp" />
