<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/jsp/header.jsp" />

<section id="admin-customer-form-section" style="padding: 20px; max-width: 600px; margin: auto;">
    <h2>
        <c:choose>
            <c:when test="${isEditMode}">编辑客户信息 (Edit Customer Information)</c:when>
            <c:otherwise>添加新客户 (Add New Customer)</c:otherwise>
        </c:choose>
    </h2>

    <c:if test="${not empty errorMessage}">
        <span class="error-message"><c:out value="${errorMessage}"/></span>
    </c:if>
    <c:if test="${not empty param.saveError}">
        <span class="error-message"><c:out value="${param.saveError}"/></span>
    </c:if>

    <form action="${pageContext.request.contextPath}/admin/saveCustomer" method="POST">
        <c:if test="${isEditMode}">
            <input type="hidden" name="userId" value="${customerToEdit.id}">
        </c:if>

        <div class="form-group">
            <label for="username">用户名 (Username):</label>
            <input type="text" id="username" name="username" value="<c:out value='${customerToEdit.username}'/>" 
                   <c:if test="${isEditMode}">readonly</c:if> <c:if test="${!isEditMode}">required</c:if>>
            <c:if test="${isEditMode}"><small> (Username cannot be changed for existing users)</small></c:if>
        </div>

        <div class="form-group">
            <label for="email">电子邮箱 (Email):</label>
            <input type="email" id="email" name="email" value="<c:out value='${customerToEdit.email}'/>" required>
        </div>

        <div class="form-group">
            <label for="phoneNumber">电话号码 (Phone Number):</label>
            <input type="tel" id="phoneNumber" name="phoneNumber" value="<c:out value='${customerToEdit.phoneNumber}'/>">
        </div>

        <div class="form-group">
            <label for="role">角色 (Role):</label>
            <select id="role" name="role" required>
                <c:forEach var="roleValue" items="${roleList}">
                    <option value="${roleValue}" ${roleValue == customerToEdit.role ? 'selected' : ''}>
                        <c:out value="${roleValue}"/>
                    </option>
                </c:forEach>
            </select>
        </div>

        <div class="form-group">
            <label for="personalSignature">个人签名 (Personal Signature):</label>
            <textarea id="personalSignature" name="personalSignature" rows="3"><c:out value='${customerToEdit.personalSignature}'/></textarea>
        </div>
        
        <c:if test="${!isEditMode}">
             <div class="form-group">
                <label for="password">密码 (Password for New User):</label>
                <input type="password" id="password" name="password" required>
            </div>
        </c:if>


        <div class="form-actions" style="margin-top: 20px;">
            <button type="submit" class="admin-button save">保存更改 (Save Changes)</button>
            <a href="${pageContext.request.contextPath}/admin/viewCustomers" class="admin-button cancel">取消 (Cancel)</a>
        </div>
    </form>
</section>

<style>
    .form-group {
        margin-bottom: 15px;
    }
    .form-group label {
        display: block;
        margin-bottom: 5px;
        font-weight: bold;
    }
    .form-group input[type="text"],
    .form-group input[type="email"],
    .form-group input[type="tel"],
    .form-group input[type="password"],
    .form-group textarea,
    .form-group select {
        width: 100%;
        padding: 10px;
        border: 1px solid #ccc;
        border-radius: 4px;
        box-sizing: border-box;
    }
    .form-group input[readonly] {
        background-color: #e9ecef;
    }
    .form-group textarea {
        resize: vertical;
    }
    .admin-button {
        padding: 10px 15px;
        text-decoration: none;
        border-radius: 4px;
        border: none;
        cursor: pointer;
        color: white !important;
        display: inline-block;
        margin-right: 10px;
    }
    .admin-button.save {
        background-color: #5cb85c; /* Green */
    }
    .admin-button.save:hover {
        background-color: #4cae4c;
    }
    .admin-button.cancel {
        background-color: #777; /* Gray */
    }
    .admin-button.cancel:hover {
        background-color: #5a5a5a;
    }
</style>

<jsp:include page="/jsp/footer.jsp" />
