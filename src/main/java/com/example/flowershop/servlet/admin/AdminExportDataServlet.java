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
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

@WebServlet("/admin/exportData")
public class AdminExportDataServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        // Admin role check is handled by AuthFilter for /admin/*

        String type = request.getParameter("type");

        if ("sales".equalsIgnoreCase(type)) {
            exportSalesData(request, response);
        } else if ("products".equalsIgnoreCase(type)) {
            // Placeholder for product export
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().println("商品数据导出功能待实现。(Product data export to be implemented.)");
        }
        else {
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().println("无效的导出类型。(Invalid export type.)");
        }
    }

    private void exportSalesData(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setCharacterEncoding("UTF-8"); // Using UTF-8
        response.setHeader("Content-Disposition", "attachment; filename=\"sales_data.csv\"");

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT o.id as order_id, u.username as customer_username, o.order_date, " +
                     "p.name as product_name, oi.quantity, oi.price_at_purchase, " +
                     "(oi.quantity * oi.price_at_purchase) as item_total, " +
                     "o.total_amount as order_total, o.status as order_status, " +
                     "o.shipping_address, o.contact_phone " +
                     "FROM orders o JOIN users u ON o.user_id = u.id " +
                     "JOIN order_items oi ON o.id = oi.order_id " +
                     "JOIN products p ON oi.product_id = p.id " +
                     "WHERE o.status = '已完成' " + // Fetching only completed orders for sales data
                     "ORDER BY o.order_date DESC, o.id ASC, oi.id ASC";

        try (PrintWriter out = response.getWriter()) {
            // Write BOM for UTF-8 to help Excel open it correctly with Chinese characters
            out.write('\uFEFF');

            // Write CSV Header
            out.println("\"订单ID\",\"客户用户名\",\"订单日期\",\"商品名称\",\"数量\",\"购买时单价\",\"商品总价\",\"订单总金额\",\"订单状态\",\"收货地址\",\"联系电话\"");

            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            while (rs.next()) {
                StringBuilder sb = new StringBuilder();
                sb.append(escapeCsvField(rs.getString("order_id"))).append(",");
                sb.append(escapeCsvField(rs.getString("customer_username"))).append(",");
                sb.append(escapeCsvField(dateFormat.format(rs.getTimestamp("order_date")))).append(",");
                sb.append(escapeCsvField(rs.getString("product_name"))).append(",");
                sb.append(escapeCsvField(String.valueOf(rs.getInt("quantity")))).append(",");
                sb.append(escapeCsvField(String.valueOf(rs.getBigDecimal("price_at_purchase")))).append(",");
                sb.append(escapeCsvField(String.valueOf(rs.getBigDecimal("item_total")))).append(",");
                sb.append(escapeCsvField(String.valueOf(rs.getBigDecimal("order_total")))).append(",");
                sb.append(escapeCsvField(rs.getString("order_status"))).append(",");
                sb.append(escapeCsvField(rs.getString("shipping_address"))).append(",");
                sb.append(escapeCsvField(rs.getString("contact_phone")));
                out.println(sb.toString());
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Log or handle more gracefully
            // If an error occurs, the response might be partially written or headers might be set.
            // It's hard to send a clean error page at this point. Logging is important.
            response.reset(); // Try to reset response if possible
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().println("Error generating CSV: " + e.getMessage());
        } finally {
            DBConnection.closeConnection(conn, pstmt, rs);
        }
    }

    private String escapeCsvField(String field) {
        if (field == null) {
            return "\"\""; // Represent null as empty quoted string
        }
        // Replace quotes with double quotes and enclose in quotes if it contains comma, quote or newline
        if (field.contains(",") || field.contains("\"") || field.contains("\n") || field.contains("\r")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return "\"" + field + "\""; // Always quote for consistency and to handle potential leading/trailing spaces
    }
}
