package com.example.flowershop.servlet;

import com.example.flowershop.model.Product;
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

@WebServlet("/loadReviewForm")
public class LoadReviewFormServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?authError=reviewFormAccess");
            return;
        }

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        // int userId = loggedInUser.getId(); // For future validation

        String productIdStr = request.getParameter("productId");
        String orderIdStr = request.getParameter("orderId");
        int productId;
        int orderId;

        if (productIdStr == null || productIdStr.trim().isEmpty() || orderIdStr == null || orderIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/order_history.jsp?error=missingReviewParams");
            return;
        }

        try {
            productId = Integer.parseInt(productIdStr);
            orderId = Integer.parseInt(orderIdStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/order_history.jsp?error=invalidReviewParamsFormat");
            return;
        }

        Product product = null;
        Connection conn = null;
        PreparedStatement pstmtProduct = null;
        // PreparedStatement pstmtCheckOrder = null; // For future validation
        ResultSet rsProduct = null;
        // ResultSet rsCheckOrder = null; // For future validation


        // SQL to fetch product details
        String productSql = "SELECT id, name, image_url FROM products WHERE id = ?";
        // SQL for future validation (optional):
        // String checkOrderSql = "SELECT COUNT(*) FROM orders o JOIN order_items oi ON o.id = oi.order_id " +
        //                        "WHERE o.id = ? AND o.user_id = ? AND oi.product_id = ? AND o.status = '已完成'";
        // String checkExistingReviewSql = "SELECT COUNT(*) FROM reviews WHERE user_id = ? AND product_id = ? AND order_id = ?";


        try {
            conn = DBConnection.getConnection();

            // Fetch product details
            pstmtProduct = conn.prepareStatement(productSql);
            pstmtProduct.setInt(1, productId);
            rsProduct = pstmtProduct.executeQuery();

            if (rsProduct.next()) {
                product = new Product();
                product.setId(rsProduct.getInt("id"));
                product.setName(rsProduct.getString("name"));
                product.setImageUrl(rsProduct.getString("image_url"));
            } else {
                response.sendRedirect(request.getContextPath() + "/order_history.jsp?error=productNotFoundForReview");
                return;
            }

            // TODO: Optional future validation:
            // 1. Verify user ordered this product in this order and order is completed.
            // pstmtCheckOrder = conn.prepareStatement(checkOrderSql);
            // pstmtCheckOrder.setInt(1, orderId);
            // pstmtCheckOrder.setInt(2, userId);
            // pstmtCheckOrder.setInt(3, productId);
            // rsCheckOrder = pstmtCheckOrder.executeQuery();
            // if (rsCheckOrder.next() && rsCheckOrder.getInt(1) == 0) {
            //     response.sendRedirect(request.getContextPath() + "/order_history.jsp?error=invalidProductOrderForReview");
            //     return;
            // }
            // 2. Verify user hasn't reviewed this product for this order yet.
            // ... (similar logic with checkExistingReviewSql) ...

            request.setAttribute("productToReview", product);
            request.setAttribute("orderIdForReview", orderId);
            request.getRequestDispatcher("/submit_review.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace(); // Log or handle more gracefully
            request.setAttribute("errorMessage", "Error loading review form: " + e.getMessage());
            request.getRequestDispatcher("/order_history.jsp").forward(request, response);
        } finally {
            // DBConnection.closeConnection(null, pstmtCheckOrder, rsCheckOrder);
            DBConnection.closeConnection(conn, pstmtProduct, rsProduct);
        }
    }
}
