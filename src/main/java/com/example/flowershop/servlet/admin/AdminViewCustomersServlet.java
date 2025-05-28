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
import java.util.ArrayList;
import java.util.List;

@WebServlet("/admin/viewCustomers")
public class AdminViewCustomersServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        // Admin role check is handled by AuthFilter for /admin/*

        List<User> customerList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT id, username, email, phone_number, role, registration_date, personal_signature FROM users ORDER BY registration_date DESC";

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPhoneNumber(rs.getString("phone_number"));
                user.setRole(rs.getString("role"));
                user.setRegistrationDate(rs.getTimestamp("registration_date"));
                user.setPersonalSignature(rs.getString("personal_signature"));
                // Password is not fetched or set for security reasons
                customerList.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log or handle more gracefully
            request.setAttribute("errorMessage", "Error fetching customers for admin view: " + e.getMessage());
        } finally {
            DBConnection.closeConnection(conn, pstmt, rs);
        }

        request.setAttribute("customerList", customerList);
        request.getRequestDispatcher("/admin/customers.jsp").forward(request, response);
    }
}
