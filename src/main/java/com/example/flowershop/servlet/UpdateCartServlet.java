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
import java.sql.SQLException;

@WebServlet("/updateCart")
public class UpdateCartServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?authError=cartUpdate");
            return;
        }

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        int userId = loggedInUser.getId();

        String action = request.getParameter("action");
        String cartItemIdStr = request.getParameter("cartItemId");
        int cartItemId;

        if (cartItemIdStr == null || cartItemIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart.jsp?cartError=Missing cart item ID");
            return;
        }

        try {
            cartItemId = Integer.parseInt(cartItemIdStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/cart.jsp?cartError=Invalid cart item ID format");
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        String redirectUrl = request.getContextPath() + "/cart.jsp";

        try {
            conn = DBConnection.getConnection();

            if ("update".equals(action)) {
                String quantityStr = request.getParameter("quantity");
                if (quantityStr == null || quantityStr.trim().isEmpty()) {
                    response.sendRedirect(redirectUrl + "?cartError=Missing quantity for update");
                    return;
                }
                int quantity;
                try {
                    quantity = Integer.parseInt(quantityStr);
                    if (quantity <= 0) { // Allow quantity 0 for removal via update logic too, if desired
                         // If quantity is 0, treat as remove for robustness, or enforce quantity >=1
                        if (quantity == 0) { // Treat as remove
                            String deleteSql = "DELETE FROM cart WHERE id = ? AND user_id = ?";
                            pstmt = conn.prepareStatement(deleteSql);
                            pstmt.setInt(1, cartItemId);
                            pstmt.setInt(2, userId);
                            redirectUrl += "?removeSuccess=true";
                        } else { // quantity < 0, invalid
                             response.sendRedirect(redirectUrl + "?cartError=Quantity must be positive");
                             return;
                        }
                    } else { // quantity > 0
                        String updateSql = "UPDATE cart SET quantity = ? WHERE id = ? AND user_id = ?";
                        pstmt = conn.prepareStatement(updateSql);
                        pstmt.setInt(1, quantity);
                        pstmt.setInt(2, cartItemId);
                        pstmt.setInt(3, userId);
                        redirectUrl += "?updateSuccess=true";
                    }
                } catch (NumberFormatException e) {
                    response.sendRedirect(redirectUrl + "?cartError=Invalid quantity format");
                    return;
                }
            } else if ("remove".equals(action)) {
                String deleteSql = "DELETE FROM cart WHERE id = ? AND user_id = ?";
                pstmt = conn.prepareStatement(deleteSql);
                pstmt.setInt(1, cartItemId);
                pstmt.setInt(2, userId);
                redirectUrl += "?removeSuccess=true";
            } else {
                response.sendRedirect(redirectUrl + "?cartError=Invalid action");
                return;
            }

            if (pstmt != null) {
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected == 0) {
                    // This could mean the cart item didn't belong to the user or didn't exist
                    redirectUrl = request.getContextPath() + "/cart.jsp?cartError=Item not found or action failed";
                }
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Log the error
            redirectUrl = request.getContextPath() + "/cart.jsp?cartError=Database error: " + e.getMessage().replace(" ", "%20");
        } finally {
            DBConnection.closeConnection(conn, pstmt, null);
        }

        response.sendRedirect(redirectUrl);
    }
}
