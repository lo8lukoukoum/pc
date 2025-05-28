<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/jsp/header.jsp" />

<section id="admin-customer-list-section" style="padding: 20px;">
    <h2>客户管理 (Customer Management)</h2>

    <%-- Optional: Add New Customer Link --%>
    <p style="margin-bottom: 15px;">
        <a href="${pageContext.request.contextPath}/admin/customer_form.jsp" class="admin-button add-new">添加新客户 (Add New Customer)</a>
    </p>

    <c:if test="${not empty errorMessage}">
        <p style="color:red;"><c:out value="${errorMessage}"/></p>
    </c:if>

    <c:choose>
        <c:when test="${empty customerList}">
            <p>没有找到客户。(No customers found.)</p>
        </c:when>
        <c:otherwise>
            <table class="admin-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>用户名 (Username)</th>
                        <th>电子邮箱 (Email)</th>
                        <th>电话号码 (Phone)</th>
                        <th>角色 (Role)</th>
                        <th>注册日期 (Reg. Date)</th>
                        <th>个人签名 (Signature)</th>
                        <th>操作 (Actions)</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="user" items="${customerList}">
                        <tr>
                            <td><c:out value="${user.id}"/></td>
                            <td><c:out value="${user.username}"/></td>
                            <td><c:out value="${user.email}"/></td>
                            <td><c:out value="${user.phoneNumber}"/></td>
                            <td><c:out value="${user.role}"/></td>
                            <td><fmt:formatDate value="${user.registrationDate}" pattern="yyyy-MM-dd HH:mm"/></td>
                            <td><c:out value="${user.personalSignature}"/></td>
                            <td>
                                <a href="${pageContext.request.contextPath}/admin/customer_form.jsp?id=${user.id}" class="admin-button edit">编辑 (Edit)</a>
                                <form action="${pageContext.request.contextPath}/admin/deleteCustomer" method="POST" style="display:inline;" onsubmit="return confirm('您确定要删除此客户吗？此操作不可撤销。(Are you sure you want to delete this customer? This action cannot be undone.)');">
                                    <input type="hidden" name="userId" value="${user.id}">
                                    <button type="submit" class="admin-button delete">删除 (Delete)</button>
                                </form>
                                <form action="${pageContext.request.contextPath}/admin/resetCustomerPassword" method="POST" style="display:inline;" onsubmit="return confirm('您确定要为此客户重置密码吗？(Are you sure you want to reset password for this customer?)');">
                                    <input type="hidden" name="userId" value="${user.id}">
                                    <button type="submit" class="admin-button reset-password">重置密码 (Reset Password)</button>
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
        margin-top: 3px; /* For spacing if buttons wrap */
        color: white !important; /* Important for specificity */
        display: inline-block;
        border: none;
        cursor: pointer;
    }
    .admin-button.add-new { background-color: #5cb85c; } /* Green */
    .admin-button.add-new:hover { background-color: #4cae4c; }
    .admin-button.edit { background-color: #f0ad4e; } /* Orange */
    .admin-button.edit:hover { background-color: #ec971f; }
    .admin-button.delete { background-color: #d9534f; } /* Red */
    .admin-button.delete:hover { background-color: #c9302c; }
    .admin-button.reset-password { background-color: #007bff; } /* Blue */
    .admin-button.reset-password:hover { background-color: #0056b3; }
</style>

<jsp:include page="/jsp/footer.jsp" />
