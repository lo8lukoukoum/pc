package com.example.flowershop.servlet;

import com.example.flowershop.model.Order;
import com.example.flowershop.model.OrderItemDetail;
import com.example.flowershop.model.User;
import com.example.flowershop.util.DBConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/orderDetail")
public class ViewOrderDetailServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?authError=orderDetailAccess");
            return;
        }

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        int userId = loggedInUser.getId();

        String orderIdStr = request.getParameter("orderId");
        int orderId;

        if (orderIdStr == null || orderIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/order_history.jsp?error=missingOrderId");
            return;
        }

        try {
            orderId = Integer.parseInt(orderIdStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/order_history.jsp?error=invalidOrderIdFormat");
            return;
        }

        Order order = null;
        List<OrderItemDetail> orderItemsList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmtOrder = null;
        PreparedStatement pstmtItems = null;
        ResultSet rsOrder = null;
        ResultSet rsItems = null;

        String orderSql = "SELECT id, order_date, total_amount, status, shipping_address, contact_phone FROM orders WHERE id = ? AND user_id = ?";
        String itemsSql = "SELECT oi.product_id, oi.quantity, oi.price_at_purchase, p.name as product_name, p.image_url as product_image_url " +
                          "FROM order_items oi JOIN products p ON oi.product_id = p.id WHERE oi.order_id = ?";

        try {
            conn = DBConnection.getConnection();

            // Fetch main order details
            pstmtOrder = conn.prepareStatement(orderSql);
            pstmtOrder.setInt(1, orderId);
            pstmtOrder.setInt(2, userId);
            rsOrder = pstmtOrder.executeQuery();

            if (rsOrder.next()) {
                order = new Order();
                order.setId(rsOrder.getInt("id"));
                order.setOrderDate(rsOrder.getTimestamp("order_date"));
                order.setTotalAmount(rsOrder.getBigDecimal("total_amount"));
                order.setStatus(rsOrder.getString("status"));
                order.setShippingAddress(rsOrder.getString("shipping_address"));
                order.setContactPhone(rsOrder.getString("contact_phone"));
                order.setUserId(userId); // Set user ID for completeness

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
                    // Calculate subtotal
                    BigDecimal subtotal = item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity()));
                    item.setSubtotal(subtotal);
                    orderItemsList.add(item);
                }

                request.setAttribute("order", order);
                request.setAttribute("orderItemsList", orderItemsList);
                request.getRequestDispatcher("/order_detail.jsp").forward(request, response);

            } else {
                // Order not found or doesn't belong to user
                response.sendRedirect(request.getContextPath() + "/order_history.jsp?error=orderNotFound");
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Log or handle more gracefully
            request.setAttribute("errorMessage", "Error fetching order details: " + e.getMessage());
            // Forward to order history page as order_detail page might not display correctly
            request.getRequestDispatcher("/order_history.jsp").forward(request, response);
        } finally {
            DBConnection.closeConnection(null, pstmtItems, rsItems); // Close items statement and its result set
            DBConnection.closeConnection(conn, pstmtOrder, rsOrder); // Close order statement, its result set, and connection
        }
    }
}
