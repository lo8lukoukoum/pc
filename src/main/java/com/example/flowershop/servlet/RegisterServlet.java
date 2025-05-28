package com.example.flowershop.servlet;

import com.example.flowershop.model.User;
import com.example.flowershop.util.DBConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String email = request.getParameter("email");
        String phoneNumber = request.getParameter("phoneNumber");
        String personalSignature = request.getParameter("personalSignature");

        // Server-side Validation
        if (username == null || username.trim().isEmpty() ||
            password == null || password.isEmpty() ||
            confirmPassword == null || confirmPassword.isEmpty()) {
            request.setAttribute("errorMessage", "用户名、密码和确认密码不能为空 (Username, password, and confirm password cannot be empty)");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        if (!password.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "两次输入的密码不一致 (Passwords do not match)");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        if (password.length() < 6) {
            request.setAttribute("errorMessage", "密码长度至少为6位 (Password must be at least 6 characters)");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        Connection conn = null;
        PreparedStatement pstmtCheckUser = null;
        PreparedStatement pstmtInsertUser = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();

            // Check if username already exists
            String checkUserSql = "SELECT id FROM users WHERE username = ?";
            pstmtCheckUser = conn.prepareStatement(checkUserSql);
            pstmtCheckUser.setString(1, username);
            rs = pstmtCheckUser.executeQuery();

            if (rs.next()) {
                request.setAttribute("errorMessage", "用户名 '" + username + "' 已存在 (Username '" + username + "' already exists)");
                request.getRequestDispatcher("register.jsp").forward(request, response);
                return;
            }

            // If validation passes, insert new user
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(password); // In a real application, hash the password
            newUser.setEmail(email);
            newUser.setPhoneNumber(phoneNumber);
            newUser.setPersonalSignature(personalSignature);
            newUser.setRole("customer"); // Default role
            newUser.setRegistrationDate(new Timestamp(new Date().getTime()));


            String insertUserSql = "INSERT INTO users (username, password, email, phone_number, personal_signature, role, registration_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
            pstmtInsertUser = conn.prepareStatement(insertUserSql);
            pstmtInsertUser.setString(1, newUser.getUsername());
            pstmtInsertUser.setString(2, newUser.getPassword());
            pstmtInsertUser.setString(3, newUser.getEmail());
            pstmtInsertUser.setString(4, newUser.getPhoneNumber());
            pstmtInsertUser.setString(5, newUser.getPersonalSignature());
            pstmtInsertUser.setString(6, newUser.getRole());
            pstmtInsertUser.setTimestamp(7, newUser.getRegistrationDate());

            int rowsAffected = pstmtInsertUser.executeUpdate();

            if (rowsAffected > 0) {
                response.sendRedirect("login.jsp?registrationSuccess=true");
            } else {
                request.setAttribute("errorMessage", "注册失败，请稍后再试 (Registration failed, please try again later)");
                request.getRequestDispatcher("register.jsp").forward(request, response);
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Log the error
            request.setAttribute("errorMessage", "数据库操作失败: " + e.getMessage() + " (Database operation failed)");
            request.getRequestDispatcher("register.jsp").forward(request, response);
        } finally {
            DBConnection.closeConnection(conn, pstmtCheckUser, rs);
            // Note: pstmtInsertUser is separate, so if checkUser fails, insertUser might not be initialized.
            // A more robust way would be to close pstmtInsertUser in its own try-catch or ensure it's non-null.
            if (pstmtInsertUser != null) {
                 try {
                    pstmtInsertUser.close();
                 } catch (SQLException e) {
                    e.printStackTrace();
                 }
            }
        }
    }
}
