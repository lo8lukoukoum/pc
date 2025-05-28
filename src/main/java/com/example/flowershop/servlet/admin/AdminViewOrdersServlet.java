package com.example.flowershop.servlet.admin;

import com.example.flowershop.model.AdminOrderView;
import com.example.flowershop.util.DBConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
// AuthFilter handles admin check
// import javax.servlet.http.HttpSession;
// import com.example.flowershop.model.User;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/admin/viewOrders")
public class AdminViewOrdersServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        // Admin role check is handled by AuthFilter for /admin/*

        String statusFilter = request.getParameter("statusFilter");
        if (statusFilter == null) {
            statusFilter = "ALL"; // Default to showing all if no filter is applied
        }

        List<AdminOrderView> orderList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        StringBuilder sqlBuilder = new StringBuilder(
            "SELECT o.id, o.user_id, u.username as customer_username, o.order_date, o.total_amount, o.status, o.shipping_address, o.contact_phone " +
            "FROM orders o JOIN users u ON o.user_id = u.id"
        );

        boolean hasStatusFilter = (statusFilter != null && !statusFilter.trim().isEmpty() && !"ALL".equalsIgnoreCase(statusFilter));

        if (hasStatusFilter) {
            sqlBuilder.append(" WHERE o.status = ?");
        }
        sqlBuilder.append(" ORDER BY o.order_date DESC");

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sqlBuilder.toString());

            if (hasStatusFilter) {
                pstmt.setString(1, statusFilter);
            }

            rs = pstmt.executeQuery();
            while (rs.next()) {
                AdminOrderView order = new AdminOrderView();
                order.setId(rs.getInt("id"));
                order.setUserId(rs.getInt("user_id"));
                order.setCustomerUsername(rs.getString("customer_username"));
                order.setOrderDate(rs.getTimestamp("order_date"));
                order.setTotalAmount(rs.getBigDecimal("total_amount"));
                order.setStatus(rs.getString("status"));
                order.setShippingAddress(rs.getString("shipping_address"));
                order.setContactPhone(rs.getString("contact_phone"));
                orderList.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log or handle more gracefully
            request.setAttribute("errorMessage", "Error fetching orders for admin view: " + e.getMessage());
        } finally {
            DBConnection.closeConnection(conn, pstmt, rs);
        }

        request.setAttribute("orderList", orderList);
        request.setAttribute("currentStatusFilter", statusFilter); // Pass current filter back to JSP
        request.getRequestDispatcher("/admin/orders.jsp").forward(request, response);
    }
}
