package com.example.flowershop.servlet.admin;

import com.example.flowershop.model.AdminOrderView;
import com.example.flowershop.model.OrderItemDetail;
// import com.example.flowershop.model.User; // Not needed if AuthFilter handles protection
import com.example.flowershop.util.DBConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
// import javax.servlet.http.HttpSession; // Not needed if AuthFilter handles protection

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@WebServlet("/admin/viewOrderDetail")
public class AdminViewOrderDetailServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        // Admin role check is handled by AuthFilter for /admin/*

        String orderIdStr = request.getParameter("orderId");
        int orderId;

        if (orderIdStr == null || orderIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/viewOrders?error=missingOrderId");
            return;
        }

        try {
            orderId = Integer.parseInt(orderIdStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/viewOrders?error=invalidOrderIdFormat");
            return;
        }

        AdminOrderView order = null;
        List<OrderItemDetail> orderItemsList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmtOrder = null;
        PreparedStatement pstmtItems = null;
        ResultSet rsOrder = null;
        ResultSet rsItems = null;

        String orderSql = "SELECT o.id, o.user_id, o.order_date, o.total_amount, o.status, o.shipping_address, o.contact_phone, u.username as customer_username " +
                          "FROM orders o JOIN users u ON o.user_id = u.id WHERE o.id = ?";
        String itemsSql = "SELECT oi.product_id, oi.quantity, oi.price_at_purchase, p.name as product_name, p.image_url as product_image_url " +
                          "FROM order_items oi JOIN products p ON oi.product_id = p.id WHERE oi.order_id = ?";

        List<String> orderStatusList = Arrays.asList("待付款", "处理中", "待发货", "已发货", "已完成", "已取消");

        try {
            conn = DBConnection.getConnection();

            // Fetch main order details
            pstmtOrder = conn.prepareStatement(orderSql);
            pstmtOrder.setInt(1, orderId);
            rsOrder = pstmtOrder.executeQuery();

            if (rsOrder.next()) {
                order = new AdminOrderView();
                order.setId(rsOrder.getInt("id"));
                order.setUserId(rsOrder.getInt("user_id"));
                order.setCustomerUsername(rsOrder.getString("customer_username"));
                order.setOrderDate(rsOrder.getTimestamp("order_date"));
                order.setTotalAmount(rsOrder.getBigDecimal("total_amount"));
                order.setStatus(rsOrder.getString("status"));
                order.setShippingAddress(rsOrder.getString("shipping_address"));
                order.setContactPhone(rsOrder.getString("contact_phone"));

                // Fetch order items
                pstmtItems = conn.prepareStatement(itemsSql);
                pstmtItems.setInt(1, orderId);
                rsItems = pstmtItems.executeQuery();

                while (rsItems.next()) {
                    OrderItemDetail item = new OrderItemDetail();
                    item.setProductId(rsItems.getInt("product_id"));
                    item.setQuantity(rsItems.getInt("quantity"));
                    item.setPriceAtPurchase(rsItems.getBigDecimal("price_at_purchase"));
                    item.setProductName(rsItems.getString("product_name"));
                    item.setProductImageUrl(rsItems.getString("product_image_url"));
                    BigDecimal subtotal = item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity()));
                    item.setSubtotal(subtotal);
                    orderItemsList.add(item);
                }

                request.setAttribute("order", order);
                request.setAttribute("orderItemsList", orderItemsList);
                request.setAttribute("orderStatusList", orderStatusList);
                request.getRequestDispatcher("/admin/order_detail.jsp").forward(request, response);

            } else {
                // Order not found
                response.sendRedirect(request.getContextPath() + "/admin/viewOrders?error=orderNotFound&orderId=" + orderId);
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Log or handle more gracefully
            request.setAttribute("errorMessage", "Error fetching order details for admin: " + e.getMessage());
            request.getRequestDispatcher("/admin/viewOrders").forward(request, response); // Forward to admin order list page
        } finally {
            DBConnection.closeConnection(null, pstmtItems, rsItems);
            DBConnection.closeConnection(conn, pstmtOrder, rsOrder);
        }
    }
}
