package com.example.flowershop.servlet;

import com.example.flowershop.model.CartDisplayItem;
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

@WebServlet("/checkout")
public class ViewCheckoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        // AuthFilter should handle this, but as a safeguard:
        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?authError=checkoutAccess");
            return;
        }

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        int userId = loggedInUser.getId();

        List<CartDisplayItem> cartItemsList = new ArrayList<>();
        BigDecimal cartTotal = BigDecimal.ZERO;
        Connection conn = null;
        PreparedStatement pstmtCart = null;
        ResultSet rsCart = null;

        String cartSql = "SELECT c.id as cart_item_id, p.id as product_id, p.name, p.price, p.image_url, c.quantity " +
                         "FROM cart c JOIN products p ON c.product_id = p.id WHERE c.user_id = ?";

        try {
            conn = DBConnection.getConnection();
            pstmtCart = conn.prepareStatement(cartSql);
            pstmtCart.setInt(1, userId);
            rsCart = pstmtCart.executeQuery();

            while (rsCart.next()) {
                CartDisplayItem item = new CartDisplayItem();
                item.setCartItemId(rsCart.getInt("cart_item_id"));
                item.setProductId(rsCart.getInt("product_id"));
                item.setProductName(rsCart.getString("name"));
                item.setProductPrice(rsCart.getBigDecimal("price"));
                item.setImageUrl(rsCart.getString("image_url"));
                item.setQuantity(rsCart.getInt("quantity"));

                BigDecimal subtotal = item.getProductPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                item.setSubtotal(subtotal);
                cartItemsList.add(item);
                cartTotal = cartTotal.add(subtotal);
            }

            if (cartItemsList.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/cart.jsp?message=emptyCartForCheckout");
                return;
            }

            // Pre-fill phone number from user's profile (already in session User object)
            request.setAttribute("userPhoneNumber", loggedInUser.getPhoneNumber());

        } catch (SQLException e) {
            e.printStackTrace(); // Log or handle more gracefully
            request.setAttribute("errorMessage", "Error fetching cart for checkout: " + e.getMessage());
            // Forward to cart with an error, as checkout page might not display correctly
            request.getRequestDispatcher("/cart.jsp").forward(request, response);
            return;
        } finally {
            DBConnection.closeConnection(conn, pstmtCart, rsCart);
        }

        request.setAttribute("cartItems", cartItemsList);
        request.setAttribute("cartTotal", cartTotal);
        request.getRequestDispatcher("/checkout.jsp").forward(request, response);
    }
}
