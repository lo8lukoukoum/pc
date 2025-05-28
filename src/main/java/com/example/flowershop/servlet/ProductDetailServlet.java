package com.example.flowershop.servlet;

import com.example.flowershop.model.Product;
import com.example.flowershop.util.DBConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/productDetail")
public class ProductDetailServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String productIdStr = request.getParameter("id");
        int productId;

        if (productIdStr == null || productIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/products.jsp?error=invalidProductId");
            return;
        }

        try {
            productId = Integer.parseInt(productIdStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/products.jsp?error=invalidProductIdFormat");
            return;
        }

        Connection conn = null;
        PreparedStatement pstmtProduct = null;
        PreparedStatement pstmtUpdateViews = null;
        PreparedStatement pstmtFetchReviews = null;
        ResultSet rsProduct = null;
        ResultSet rsReviews = null;
        Product product = null;
        List<com.example.flowershop.model.Review> reviewList = new ArrayList<>();


        String selectSql = "SELECT p.id, p.name, p.description, p.price, p.stock, p.category_id, c.name as category_name, p.image_url, p.status, p.views, p.creation_date, p.sales_count " +
                           "FROM products p LEFT JOIN categories c ON p.category_id = c.id WHERE p.id = ?";
        String updateViewsSql = "UPDATE products SET views = views + 1 WHERE id = ?";
        String reviewsSql = "SELECT r.rating, r.comment, r.image_url, r.review_date, r.is_anonymous, u.username " +
                            "FROM reviews r JOIN users u ON r.user_id = u.id " +
                            "WHERE r.product_id = ? AND r.status = '已批准' ORDER BY r.review_date DESC";

        try {
            conn = DBConnection.getConnection();

            // Fetch product details
            pstmtProduct = conn.prepareStatement(selectSql);
            pstmtProduct.setInt(1, productId);
            rsProduct = pstmtProduct.executeQuery();

            if (rsProduct.next()) {
                product = new Product();
                product.setId(rsProduct.getInt("id"));
                product.setName(rsProduct.getString("name"));
                product.setDescription(rsProduct.getString("description"));
                product.setPrice(rsProduct.getBigDecimal("price"));
                product.setStock(rsProduct.getInt("stock"));
                product.setCategoryId(rsProduct.getInt("category_id"));
                // product.setCategoryName(rsProduct.getString("category_name")); // Assuming you add this field to Product model
                product.setImageUrl(rsProduct.getString("image_url"));
                product.setStatus(rsProduct.getString("status"));
                product.setViews(rsProduct.getInt("views") + 1); // Increment views for display, DB update below
                product.setCreationDate(rsProduct.getTimestamp("creation_date"));
                product.setSalesCount(rsProduct.getInt("sales_count"));


                // Update views count in DB
                pstmtUpdateViews = conn.prepareStatement(updateViewsSql);
                pstmtUpdateViews.setInt(1, productId);
                pstmtUpdateViews.executeUpdate(); // DB view count updated

                // Fetch approved reviews
                pstmtFetchReviews = conn.prepareStatement(reviewsSql);
                pstmtFetchReviews.setInt(1, productId);
                rsReviews = pstmtFetchReviews.executeQuery();

                while (rsReviews.next()) {
                    com.example.flowershop.model.Review review = new com.example.flowershop.model.Review();
                    review.setRating(rsReviews.getInt("rating"));
                    review.setComment(rsReviews.getString("comment"));
                    review.setImageUrl(rsReviews.getString("image_url"));
                    review.setReviewDate(rsReviews.getTimestamp("review_date"));
                    review.setAnonymous(rsReviews.getBoolean("is_anonymous"));
                    review.setUsername(rsReviews.getString("username")); // Username from users table
                    reviewList.add(review);
                }

                request.setAttribute("product", product);
                request.setAttribute("reviewList", reviewList);
                request.getRequestDispatcher("/product_detail.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/products.jsp?error=productNotFound");
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Log or handle more gracefully
            request.setAttribute("errorMessage", "Error fetching product details: " + e.getMessage());
            request.getRequestDispatcher("/products.jsp").forward(request, response); // Forward to products page with error
        } finally {
            DBConnection.closeConnection(null, pstmtUpdateViews, null); 
            DBConnection.closeConnection(null, pstmtFetchReviews, rsReviews);
            DBConnection.closeConnection(conn, pstmtProduct, rsProduct);
        }
    }
}
