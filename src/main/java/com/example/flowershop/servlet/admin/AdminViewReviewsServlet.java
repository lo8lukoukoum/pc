package com.example.flowershop.servlet.admin;

import com.example.flowershop.model.AdminReviewView;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/admin/viewReviews")
public class AdminViewReviewsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        // Admin role check is handled by AuthFilter for /admin/*

        String statusFilter = request.getParameter("statusFilter");
        if (statusFilter == null || statusFilter.trim().isEmpty()) {
            statusFilter = "待审核"; // Default to '待审核'
        }

        List<AdminReviewView> reviewList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        StringBuilder sqlBuilder = new StringBuilder(
            "SELECT r.id, r.user_id, u.username, r.product_id, p.name as product_name, r.rating, r.comment, r.image_url, r.review_date, r.is_anonymous, r.status " +
            "FROM reviews r JOIN users u ON r.user_id = u.id JOIN products p ON r.product_id = p.id"
        );

        boolean hasStatusFilter = (!"ALL".equalsIgnoreCase(statusFilter));

        if (hasStatusFilter) {
            sqlBuilder.append(" WHERE r.status = ?");
        }
        sqlBuilder.append(" ORDER BY r.review_date DESC");

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sqlBuilder.toString());

            if (hasStatusFilter) {
                pstmt.setString(1, statusFilter);
            }

            rs = pstmt.executeQuery();
            while (rs.next()) {
                AdminReviewView review = new AdminReviewView();
                review.setId(rs.getInt("id"));
                review.setUserId(rs.getInt("user_id"));
                review.setUsername(rs.getString("username"));
                review.setProductId(rs.getInt("product_id"));
                review.setProductName(rs.getString("product_name"));
                review.setRating(rs.getInt("rating"));
                review.setComment(rs.getString("comment"));
                review.setImageUrl(rs.getString("image_url"));
                review.setReviewDate(rs.getTimestamp("review_date"));
                review.setAnonymous(rs.getBoolean("is_anonymous"));
                review.setStatus(rs.getString("status"));
                reviewList.add(review);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log or handle more gracefully
            request.setAttribute("errorMessage", "Error fetching reviews for admin view: " + e.getMessage());
        } finally {
            DBConnection.closeConnection(conn, pstmt, rs);
        }

        request.setAttribute("reviewList", reviewList);
        request.setAttribute("currentStatusFilter", statusFilter); // Pass current filter back to JSP
        request.getRequestDispatcher("/admin/reviews.jsp").forward(request, response);
    }
}
