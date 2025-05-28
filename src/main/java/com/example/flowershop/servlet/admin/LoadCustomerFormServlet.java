package com.example.flowershop.servlet.admin;

import com.example.flowershop.model.User;
import com.example.flowershop.util.DBConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
// AuthFilter handles admin check
// import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

@WebServlet("/admin/loadCustomerForm")
public class LoadCustomerFormServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        // Admin role check is handled by AuthFilter for /admin/*

        User customerToEdit = null;
        boolean isEditMode = false;
        String userIdStr = request.getParameter("id"); // Renamed from userId to id to match link in customers.jsp

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        if (userIdStr != null && !userIdStr.trim().isEmpty()) {
            isEditMode = true;
            try {
                int userId = Integer.parseInt(userIdStr);
                conn = DBConnection.getConnection();
                String sql = "SELECT id, username, email, phone_number, role, personal_signature, registration_date FROM users WHERE id = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, userId);
                rs = pstmt.executeQuery();

                if (rs.next()) {
                    customerToEdit = new User();
                    customerToEdit.setId(rs.getInt("id"));
                    customerToEdit.setUsername(rs.getString("username"));
                    customerToEdit.setEmail(rs.getString("email"));
                    customerToEdit.setPhoneNumber(rs.getString("phone_number"));
                    customerToEdit.setRole(rs.getString("role"));
                    customerToEdit.setPersonalSignature(rs.getString("personal_signature"));
                    customerToEdit.setRegistrationDate(rs.getTimestamp("registration_date"));
                    // Password is not loaded
                } else {
                    request.setAttribute("errorMessage", "Customer with ID " + userId + " not found.");
                    isEditMode = false; // Reset as customer not found
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Invalid Customer ID format.");
                isEditMode = false; // Reset on error
            } catch (SQLException e) {
                e.printStackTrace(); // Log or handle more gracefully
                request.setAttribute("errorMessage", "Database error loading customer: " + e.getMessage());
                isEditMode = false; // Reset on error
            } finally {
                DBConnection.closeConnection(conn, pstmt, rs);
            }
        }

        request.setAttribute("customerToEdit", customerToEdit);
        request.setAttribute("isEditMode", isEditMode);
        List<String> roleList = Arrays.asList("customer", "admin"); // Define roles for dropdown
        request.setAttribute("roleList", roleList);

        request.getRequestDispatcher("/admin/customer_form.jsp").forward(request, response);
    }
}
