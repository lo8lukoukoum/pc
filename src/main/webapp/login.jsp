<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="jsp/header.jsp" />

<section id="login-form">
    <h2>用户登录 (User Login)</h2>

    <c:if test="${param.logoutSuccess == 'true'}">
        <p style="color:green;">您已成功退出登录。(You have successfully logged out.)</p>
    </c:if>

    <c:if test="${param.registrationSuccess == 'true'}">
        <p style="color:green;">注册成功！现在可以登录了。(Registration successful! You can now log in.)</p>
    </c:if>

    <c:if test="${not empty requestScope.errorMessage}">
        <p style="color:red;"><c:out value="${requestScope.errorMessage}"/></p>
    </c:if>

    <form action="${pageContext.request.contextPath}/login" method="POST">
        <p>
            <label for="username">用户名:</label>
            <input type="text" id="username" name="username" required>
        </p>
        <p>
            <label for="password">密码:</label>
            <input type="password" id="password" name="password" required>
        </p>
        <p>
            <button type="submit">登录 (Login)</button>
        </p>
    </form>

    <p>
        还没有账户? <a href="${pageContext.request.contextPath}/register.jsp">立即注册 (Register here)</a>
    </p>
</section>

<jsp:include page="jsp/footer.jsp" />
