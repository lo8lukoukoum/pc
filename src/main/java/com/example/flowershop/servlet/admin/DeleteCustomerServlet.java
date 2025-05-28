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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/admin/deleteCustomer")
public class DeleteCustomerServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        // Admin role check is handled by AuthFilter for /admin/*
        // However, we need the loggedInUser for self-deletion check
        User loggedInUser = (session != null) ? (User) session.getAttribute("loggedInUser") : null;
        if (loggedInUser == null) { // Should be caught by AuthFilter, but as a safeguard
            response.sendRedirect(request.getContextPath() + "/login.jsp?authError=adminRequired");
            return;
        }

        String userIdStr = request.getParameter("userId");
        int userIdToDelete;

        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/viewCustomers?deleteError=Missing User ID");
            return;
        }

        try {
            userIdToDelete = Integer.parseInt(userIdStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/viewCustomers?deleteError=Invalid User ID format");
            return;
        }

        // Prevent self-deletion
        if (loggedInUser.getId() == userIdToDelete) {
            response.sendRedirect(request.getContextPath() + "/admin/viewCustomers?deleteError=Admin users cannot delete their own account.");
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnection.getConnection();
            // The schema uses ON DELETE CASCADE for orders, cart, and reviews.
            // This means deleting a user will also delete their associated records in those tables.
            String deleteSql = "DELETE FROM users WHERE id = ?";
            pstmt = conn.prepareStatement(deleteSql);
            pstmt.setInt(1, userIdToDelete);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                response.sendRedirect(request.getContextPath() + "/admin/viewCustomers?deleteSuccess=true&userId=" + userIdToDelete);
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/viewCustomers?deleteError=User not found or could not be deleted (ID: " + userIdToDelete + ").");
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Log the error
            // A more specific error for foreign key might not be needed if ON DELETE CASCADE handles it,
            // but if there were other constraints, this would be the place.
            String errorMessage = "Database error during user deletion: " + e.getMessage();
            response.sendRedirect(request.getContextPath() + "/admin/viewCustomers?deleteError=" + java.net.URLEncoder.encode(errorMessage, "UTF-8"));
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
