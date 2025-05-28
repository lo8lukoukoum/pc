package com.example.flowershop.servlet.admin;

import com.example.flowershop.model.User;
import com.example.flowershop.util.DBConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/admin/resetPassword") // Corrected annotation from task description
public class ResetCustomerPasswordServlet extends HttpServlet {

    private static final String ALPHANUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static SecureRandom random = new SecureRandom();

    private static String generateRandomPassword(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Password length must be positive.");
        }
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHANUMERIC_STRING.charAt(random.nextInt(ALPHANUMERIC_STRING.length())));
        }
        return sb.toString();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        // Admin role check is handled by AuthFilter for /admin/*
        User loggedInUser = (session != null) ? (User) session.getAttribute("loggedInUser") : null;
        if (loggedInUser == null) { // Should be caught by AuthFilter, but as a safeguard
            response.sendRedirect(request.getContextPath() + "/login.jsp?authError=adminRequired");
            return;
        }

        String userIdStr = request.getParameter("userId");
        int userIdToReset;

        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/viewCustomers?resetError=Missing User ID");
            return;
        }

        try {
            userIdToReset = Integer.parseInt(userIdStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/viewCustomers?resetError=Invalid User ID format");
            return;
        }

        // Optional: Prevent admin from resetting their own password through this interface
        // if (loggedInUser.getId() == userIdToReset) {
        //     response.sendRedirect(request.getContextPath() + "/admin/viewCustomers?resetError=Cannot reset your own password via this form.");
        //     return;
        // }

        String newTemporaryPassword = generateRandomPassword(8);
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE users SET password = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newTemporaryPassword);
            pstmt.setInt(2, userIdToReset);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                // IMPORTANT: Displaying password like this is a security risk. For project demonstration only.
                response.sendRedirect(request.getContextPath() + "/admin/viewCustomers?resetSuccess=true&userId=" + userIdToReset + "&tempPass=" + java.net.URLEncoder.encode(newTemporaryPassword, "UTF-8"));
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/viewCustomers?resetError=User not found or password not reset (ID: " + userIdToReset + ").");
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Log the error
            String errorMessage = "Database error during password reset: " + e.getMessage();
            response.sendRedirect(request.getContextPath() + "/admin/viewCustomers?resetError=" + java.net.URLEncoder.encode(errorMessage, "UTF-8"));
        } finally {
            DBConnection.closeConnection(conn, pstmt, null);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Redirect GET requests to the customer list page
        response.sendRedirect(request.getContextPath() + "/admin/viewCustomers");
    }
}
