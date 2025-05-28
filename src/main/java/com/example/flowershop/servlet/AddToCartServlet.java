package com.example.flowershop.servlet;

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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

@WebServlet("/addToCart")
public class AddToCartServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        // 1. Authentication Check (Defense in depth, AuthFilter should also cover this)
        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?authError=cart");
            return;
        }

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        int userId = loggedInUser.getId();

        String productIdStr = request.getParameter("productId");
        String quantityStr = request.getParameter("quantity");
        int productId;
        int quantity;

        // 2. Validate parameters
        if (productIdStr == null || productIdStr.trim().isEmpty() || quantityStr == null || quantityStr.trim().isEmpty()) {
            // Redirect back or show an error
            response.sendRedirect(request.getContextPath() + "/products.jsp?error=invalidCartParams");
            return;
        }

        try {
            productId = Integer.parseInt(productIdStr);
            quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                throw new NumberFormatException("Quantity must be positive");
            }
        } catch (NumberFormatException e) {
            // Redirect back with error
            String referer = request.getHeader("Referer"); // Get the previous page
            if (referer != null && !referer.isEmpty()) {
                 response.sendRedirect(referer + (referer.contains("?") ? "&" : "?") + "cartError=Invalid quantity");
            } else {
                 response.sendRedirect(request.getContextPath() + "/products.jsp?cartError=Invalid quantity");
            }
            return;
        }

        Connection conn = null;
        PreparedStatement pstmtCheck = null;
        PreparedStatement pstmtUpdate = null;
        PreparedStatement pstmtInsert = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            // Check product stock first (optional here, but good for early check)
            // String stockCheckSql = "SELECT stock FROM products WHERE id = ?";
            // ... execute and check if quantity <= stock ...

            // 3. Database Interaction
            String checkCartSql = "SELECT id, quantity FROM cart WHERE user_id = ? AND product_id = ?";
            pstmtCheck = conn.prepareStatement(checkCartSql);
            pstmtCheck.setInt(1, userId);
            pstmtCheck.setInt(2, productId);
            rs = pstmtCheck.executeQuery();

            if (rs.next()) {
                // Item exists, update quantity
                int existingCartId = rs.getInt("id");
                // int existingQuantity = rs.getInt("quantity"); // Not used directly as we add to existing
                String updateCartSql = "UPDATE cart SET quantity = quantity + ? WHERE id = ?";
                pstmtUpdate = conn.prepareStatement(updateCartSql);
                pstmtUpdate.setInt(1, quantity);
                pstmtUpdate.setInt(2, existingCartId);
                pstmtUpdate.executeUpdate();
            } else {
                // Item doesn't exist, insert new row
                String insertCartSql = "INSERT INTO cart (user_id, product_id, quantity, added_date) VALUES (?, ?, ?, ?)";
                pstmtInsert = conn.prepareStatement(insertCartSql);
                pstmtInsert.setInt(1, userId);
                pstmtInsert.setInt(2, productId);
                pstmtInsert.setInt(3, quantity);
                pstmtInsert.setTimestamp(4, new Timestamp(new Date().getTime()));
                pstmtInsert.executeUpdate();
            }

            // 4. Redirect to cart page
            response.sendRedirect(request.getContextPath() + "/cart.jsp?addSuccess=true&productId=" + productId);

        } catch (SQLException e) {
            e.printStackTrace(); // Log the error
            // Redirect back with error
            String referer = request.getHeader("Referer");
             if (referer != null && !referer.isEmpty()) {
                 response.sendRedirect(referer + (referer.contains("?") ? "&" : "?") + "cartError=DB Error: " + e.getMessage().replace(" ", "%20"));
            } else {
                 response.sendRedirect(request.getContextPath() + "/products.jsp?cartError=DB Error");
            }
        } finally {
            DBConnection.closeConnection(null, pstmtUpdate, null); // Separate closing
            DBConnection.closeConnection(null, pstmtInsert, null); // Separate closing
            DBConnection.closeConnection(conn, pstmtCheck, rs); // Closes conn if others are null
        }
    }
}
