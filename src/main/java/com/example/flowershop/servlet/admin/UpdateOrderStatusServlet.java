package com.example.flowershop.servlet.admin;

import com.example.flowershop.util.DBConnection;
// import com.example.flowershop.model.User; // Not strictly needed if AuthFilter is solely responsible

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
// import javax.servlet.http.HttpSession; // Not strictly needed if AuthFilter is solely responsible

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

@WebServlet("/admin/updateOrderStatus")
public class UpdateOrderStatusServlet extends HttpServlet {

    private static final List<String> VALID_ORDER_STATUSES = Arrays.asList(
            "待付款", "处理中", "待发货", "已发货", "已完成", "已取消"
    );

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        // Admin role check is handled by AuthFilter for /admin/*
        // HttpSession session = request.getSession(false);
        // User loggedInUser = (session != null) ? (User) session.getAttribute("loggedInUser") : null;
        // if (loggedInUser == null || !"admin".equals(loggedInUser.getRole())) {
        //     response.sendRedirect(request.getContextPath() + "/login.jsp?authError=adminRequired");
        //     return;
        // }

        String orderIdStr = request.getParameter("orderId");
        String newStatus = request.getParameter("newStatus");
        int orderId;

        String redirectUrl = request.getContextPath() + "/admin/viewOrderDetail";
        if (orderIdStr != null && !orderIdStr.trim().isEmpty()) {
            redirectUrl += "?orderId=" + orderIdStr;
        } else {
            redirectUrl = request.getContextPath() + "/admin/viewOrders?error=MissingOrderIdForStatusUpdate"; // Fallback if orderId is missing
        }


        // Validate orderId
        if (orderIdStr == null || orderIdStr.trim().isEmpty()) {
            response.sendRedirect(redirectUrl + "&statusUpdateError=Order ID is missing.");
            return;
        }
        try {
            orderId = Integer.parseInt(orderIdStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(redirectUrl + "&statusUpdateError=Invalid Order ID format.");
            return;
        }

        // Validate newStatus
        if (newStatus == null || newStatus.trim().isEmpty() || !VALID_ORDER_STATUSES.contains(newStatus)) {
            response.sendRedirect(redirectUrl + "&statusUpdateError=Invalid or missing new status.");
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE orders SET status = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, orderId);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                // TODO: Implement side effects for status changes in future tasks
                // e.g., if newStatus is "已取消", consider stock replenishment
                // if newStatus is "已发货", record shipping details
                response.sendRedirect(redirectUrl + "&statusUpdateSuccess=true");
            } else {
                response.sendRedirect(redirectUrl + "&statusUpdateError=Order not found or status not changed (ID: " + orderId + ").");
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Log the error
            response.sendRedirect(redirectUrl + "&statusUpdateError=Database error: " + e.getMessage().replace(" ", "%20"));
        } finally {
            DBConnection.closeConnection(conn, pstmt, null);
        }
    }
}
