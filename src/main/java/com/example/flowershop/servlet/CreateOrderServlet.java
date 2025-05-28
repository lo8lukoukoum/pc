package com.example.flowershop.servlet;

import com.example.flowershop.model.CartDisplayItem; // Re-using for cart items
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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/createOrder")
public class CreateOrderServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        // 1. Authentication Check
        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?authError=orderCreation");
            return;
        }

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        int userId = loggedInUser.getId();

        // 2. Retrieve Shipping Details
        String recipientName = request.getParameter("recipientName");
        String shippingAddress = request.getParameter("shippingAddress");
        String contactPhone = request.getParameter("contactPhone");

        if (recipientName == null || recipientName.trim().isEmpty() ||
            shippingAddress == null || shippingAddress.trim().isEmpty() ||
            contactPhone == null || contactPhone.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/checkout.jsp?orderError=Missing shipping details");
            return;
        }

        Connection conn = null;
        PreparedStatement pstmtFetchCart = null;
        PreparedStatement pstmtInsertOrder = null;
        PreparedStatement pstmtInsertOrderItem = null;
        PreparedStatement pstmtUpdateStock = null;
        PreparedStatement pstmtClearCart = null;
        ResultSet rsCart = null;
        String errorRedirect = request.getContextPath() + "/checkout.jsp"; // Default error redirect

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // 3. Fetch Cart Items (and check stock)
            List<CartDisplayItem> cartItemsList = new ArrayList<>();
            BigDecimal totalAmount = BigDecimal.ZERO;

            String fetchCartSql = "SELECT c.id as cart_item_id, p.id as product_id, p.name, p.price, c.quantity, p.stock " +
                                  "FROM cart c JOIN products p ON c.product_id = p.id WHERE c.user_id = ?";
            pstmtFetchCart = conn.prepareStatement(fetchCartSql);
            pstmtFetchCart.setInt(1, userId);
            rsCart = pstmtFetchCart.executeQuery();

            while (rsCart.next()) {
                CartDisplayItem item = new CartDisplayItem();
                item.setProductId(rsCart.getInt("product_id"));
                item.setProductName(rsCart.getString("name")); // For error messages
                item.setProductPrice(rsCart.getBigDecimal("price"));
                item.setQuantity(rsCart.getInt("quantity"));
                int stock = rsCart.getInt("stock");

                if (stock < item.getQuantity()) {
                    conn.rollback(); // Rollback before redirecting
                    conn.setAutoCommit(true);
                    response.sendRedirect(errorRedirect + "?orderError=Product '" + item.getProductName() + "' is out of stock or insufficient stock.");
                    return;
                }
                item.setSubtotal(item.getProductPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                cartItemsList.add(item);
                totalAmount = totalAmount.add(item.getSubtotal());
            }

            if (cartItemsList.isEmpty()) {
                conn.rollback(); // No items in cart
                conn.setAutoCommit(true);
                response.sendRedirect(request.getContextPath() + "/cart.jsp?message=Cart is empty, cannot create order.");
                return;
            }

            // 4. Insert into 'orders' table
            String insertOrderSql = "INSERT INTO orders (user_id, order_date, total_amount, status, shipping_address, contact_phone) VALUES (?, NOW(), ?, '待付款', ?, ?)";
            pstmtInsertOrder = conn.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS);
            pstmtInsertOrder.setInt(1, userId);
            pstmtInsertOrder.setBigDecimal(2, totalAmount);
            pstmtInsertOrder.setString(3, shippingAddress);
            pstmtInsertOrder.setString(4, contactPhone);
            pstmtInsertOrder.executeUpdate();

            ResultSet generatedKeys = pstmtInsertOrder.getGeneratedKeys();
            int orderId;
            if (generatedKeys.next()) {
                orderId = generatedKeys.getInt(1);
            } else {
                conn.rollback();
                conn.setAutoCommit(true);
                response.sendRedirect(errorRedirect + "?orderError=Failed to create order (no order ID).");
                return;
            }

            // 5. Insert into 'order_items' table
            String insertOrderItemSql = "INSERT INTO order_items (order_id, product_id, quantity, price_at_purchase) VALUES (?, ?, ?, ?)";
            pstmtInsertOrderItem = conn.prepareStatement(insertOrderItemSql);
            for (CartDisplayItem item : cartItemsList) {
                pstmtInsertOrderItem.setInt(1, orderId);
                pstmtInsertOrderItem.setInt(2, item.getProductId());
                pstmtInsertOrderItem.setInt(3, item.getQuantity());
                pstmtInsertOrderItem.setBigDecimal(4, item.getProductPrice());
                pstmtInsertOrderItem.addBatch();
            }
            pstmtInsertOrderItem.executeBatch();

            // 6. Update Product Stock
            String updateStockSql = "UPDATE products SET stock = stock - ?, sales_count = sales_count + ? WHERE id = ?";
            pstmtUpdateStock = conn.prepareStatement(updateStockSql);
            for (CartDisplayItem item : cartItemsList) {
                pstmtUpdateStock.setInt(1, item.getQuantity());
                pstmtUpdateStock.setInt(2, item.getQuantity()); // Assuming sales_count increases by quantity sold
                pstmtUpdateStock.setInt(3, item.getProductId());
                pstmtUpdateStock.addBatch();
            }
            pstmtUpdateStock.executeBatch();

            // 7. Clear User's Cart
            String clearCartSql = "DELETE FROM cart WHERE user_id = ?";
            pstmtClearCart = conn.prepareStatement(clearCartSql);
            pstmtClearCart.setInt(1, userId);
            pstmtClearCart.executeUpdate();

            conn.commit(); // All operations successful
            response.sendRedirect(request.getContextPath() + "/order_history.jsp?orderSuccess=true&orderId=" + orderId);

        } catch (SQLException e) {
            e.printStackTrace(); // Log the error
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace(); // Log rollback failure
                }
            }
            response.sendRedirect(errorRedirect + "?orderError=Database error during order creation: " + e.getMessage().replace(" ", "%20"));
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            DBConnection.closeConnection(null, pstmtFetchCart, rsCart);
            DBConnection.closeConnection(null, pstmtInsertOrder, null);
            DBConnection.closeConnection(null, pstmtInsertOrderItem, null);
            DBConnection.closeConnection(null, pstmtUpdateStock, null);
            DBConnection.closeConnection(conn, pstmtClearCart, null); // conn will be closed here
        }
    }
}
