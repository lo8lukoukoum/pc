<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:include page="jsp/header.jsp" />

<section id="registration-form">
    <h2>用户注册 (User Registration)</h2>

    <%-- Placeholder for displaying error messages from the servlet --%>
    <div style="color: red;">
        <%
            String errorMessage = (String) request.getAttribute("errorMessage");
            if (errorMessage != null) {
                out.println("<p>" + errorMessage + "</p>");
            }
        %>
    </div>

    <form action="${pageContext.request.contextPath}/register" method="POST" onsubmit="return validateForm();">
        <p>
            <label for="username">用户名:</label>
            <input type="text" id="username" name="username" required>
            <span id="usernameError" style="color: red;"></span>
        </p>
        <p>
            <label for="password">密码:</label>
            <input type="password" id="password" name="password" required>
            <span id="passwordError" style="color: red;"></span>
        </p>
        <p>
            <label for="confirmPassword">确认密码:</label>
            <input type="password" id="confirmPassword" name="confirmPassword" required>
            <span id="confirmPasswordError" style="color: red;"></span>
        </p>
        <p>
            <label for="email">电子邮箱:</label>
            <input type="email" id="email" name="email">
            <span id="emailError" style="color: red;"></span>
        </p>
        <p>
            <label for="phoneNumber">电话号码:</label>
            <input type="text" id="phoneNumber" name="phoneNumber">
        </p>
        <p>
            <label for="personalSignature">个人签名:</label>
            <textarea id="personalSignature" name="personalSignature" rows="3"></textarea>
        </p>
        <p>
            <button type="submit">注册 (Register)</button>
        </p>
    </form>
</section>

<script>
    function validateForm() {
        var username = document.getElementById('username').value.trim();
        var password = document.getElementById('password').value;
        var confirmPassword = document.getElementById('confirmPassword').value;
        var email = document.getElementById('email').value.trim();

        var isValid = true;

        // Clear previous error messages
        document.getElementById('usernameError').textContent = '';
        document.getElementById('passwordError').textContent = '';
        document.getElementById('confirmPasswordError').textContent = '';
        document.getElementById('emailError').textContent = '';

        // Validate Username
        if (username === "") {
            document.getElementById('usernameError').textContent = '用户名不能为空 (Username cannot be empty)';
            isValid = false;
        }

        // Validate Password
        if (password === "") {
            document.getElementById('passwordError').textContent = '密码不能为空 (Password cannot be empty)';
            isValid = false;
        } else if (password.length < 6) {
            document.getElementById('passwordError').textContent = '密码长度至少为6位 (Password must be at least 6 characters)';
            isValid = false;
        }


        // Validate Confirm Password
        if (confirmPassword === "") {
            document.getElementById('confirmPasswordError').textContent = '确认密码不能为空 (Confirm password cannot be empty)';
            isValid = false;
        } else if (password !== confirmPassword) {
            document.getElementById('confirmPasswordError').textContent = '两次输入的密码不一致 (Passwords do not match)';
            isValid = false;
        }
        
        // Validate Email (basic format)
        if (email !== "" && !/^\S+@\S+\.\S+$/.test(email)) {
            document.getElementById('emailError').textContent = '请输入有效的邮箱地址 (Please enter a valid email address)';
            isValid = false;
        }


        return isValid;
    }
</script>

<jsp:include page="jsp/footer.jsp" />
