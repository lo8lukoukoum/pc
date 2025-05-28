package com.example.flowershop.servlet.admin;

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
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/viewStats")
public class AdminViewStatsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        // Admin role check is handled by AuthFilter for /admin/*

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();

            // Top Selling Products
            List<Map<String, Object>> topSellingProducts = new ArrayList<>();
            String topSellingSql = "SELECT name, sales_count FROM products ORDER BY sales_count DESC LIMIT 10";
            try (PreparedStatement pstmt = conn.prepareStatement(topSellingSql);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> product = new HashMap<>();
                    product.put("name", rs.getString("name"));
                    product.put("sales_count", rs.getInt("sales_count"));
                    topSellingProducts.add(product);
                }
            }
            request.setAttribute("topSellingProducts", topSellingProducts);

            // Most Viewed Products
            List<Map<String, Object>> mostViewedProducts = new ArrayList<>();
            String mostViewedSql = "SELECT name, views FROM products ORDER BY views DESC LIMIT 10";
            try (PreparedStatement pstmt = conn.prepareStatement(mostViewedSql);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> product = new HashMap<>();
                    product.put("name", rs.getString("name"));
                    product.put("views", rs.getInt("views"));
                    mostViewedProducts.add(product);
                }
            }
            request.setAttribute("mostViewedProducts", mostViewedProducts);

            // Total number of users
            String totalUsersSql = "SELECT COUNT(*) as total_users FROM users";
            try (PreparedStatement pstmt = conn.prepareStatement(totalUsersSql);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    request.setAttribute("totalUsers", rs.getInt("total_users"));
                }
            }

            // Total number of products
            String totalProductsSql = "SELECT COUNT(*) as total_products FROM products";
            try (PreparedStatement pstmt = conn.prepareStatement(totalProductsSql);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    request.setAttribute("totalProducts", rs.getInt("total_products"));
                }
            }

            // Total number of orders
            String totalOrdersSql = "SELECT COUNT(*) as total_orders FROM orders";
            try (PreparedStatement pstmt = conn.prepareStatement(totalOrdersSql);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    request.setAttribute("totalOrders", rs.getInt("total_orders"));
                }
            }

            // Total sales revenue
            String totalRevenueSql = "SELECT SUM(total_amount) as total_revenue FROM orders WHERE status = '已完成'";
            try (PreparedStatement pstmt = conn.prepareStatement(totalRevenueSql);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal totalRevenue = rs.getBigDecimal("total_revenue");
                    request.setAttribute("totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);
                } else {
                    request.setAttribute("totalRevenue", BigDecimal.ZERO);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Log or handle more gracefully
            request.setAttribute("statsErrorMessage", "Error fetching statistics: " + e.getMessage());
        } finally {
            DBConnection.closeConnection(conn, null, null); // Close only connection, statements are try-with-resources
        }

        request.getRequestDispatcher("/admin/stats.jsp").forward(request, response);
    }
}
