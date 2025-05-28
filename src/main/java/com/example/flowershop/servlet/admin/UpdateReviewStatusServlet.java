package com.example.flowershop.servlet.admin;

import com.example.flowershop.util.DBConnection;
// import com.example.flowershop.model.User; // Not strictly needed if AuthFilter is solely responsible

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
// import javax.servlet.http.HttpSession; // Not strictly needed if AuthFilter is solely responsible

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

@WebServlet("/admin/updateReviewStatus")
public class UpdateReviewStatusServlet extends HttpServlet {

    private static final List<String> VALID_REVIEW_STATUSES = Arrays.asList(
            "待审核", "已批准", "已拒绝"
    );

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        // Admin role check is handled by AuthFilter for /admin/*
        // HttpSession session = request.getSession(false);
        // User loggedInUser = (session != null) ? (User) session.getAttribute("loggedInUser") : null;
        // if (loggedInUser == null || !"admin".equals(loggedInUser.getRole())) {
        //     response.sendRedirect(request.getContextPath() + "/login.jsp?authError=adminRequired");
        //     return;
        // }

        String reviewIdStr = request.getParameter("reviewId");
        String newStatus = request.getParameter("newStatus");
        int reviewId;

        String redirectUrl = request.getContextPath() + "/admin/viewReviews"; // Base redirect
        String currentFilter = request.getParameter("currentStatusFilter"); // To preserve filter, if possible
        if (currentFilter != null && !currentFilter.trim().isEmpty() && !"ALL".equalsIgnoreCase(currentFilter)) {
            redirectUrl += "?statusFilter=" + java.net.URLEncoder.encode(currentFilter, "UTF-8");
        } else {
            redirectUrl += "?statusFilter=ALL"; // Default or if no filter was active
        }


        // Validate reviewId
        if (reviewIdStr == null || reviewIdStr.trim().isEmpty()) {
            response.sendRedirect(redirectUrl + "&statusUpdateError=Review ID is missing.");
            return;
        }
        try {
            reviewId = Integer.parseInt(reviewIdStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(redirectUrl + "&statusUpdateError=Invalid Review ID format.");
            return;
        }

        // Validate newStatus
        if (newStatus == null || newStatus.trim().isEmpty() || !VALID_REVIEW_STATUSES.contains(newStatus)) {
            response.sendRedirect(redirectUrl + "&statusUpdateError=Invalid or missing new status for review " + reviewId + ".");
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE reviews SET status = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, reviewId);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                response.sendRedirect(redirectUrl + "&statusUpdateSuccess=true&reviewId=" + reviewId + "&newStatus=" + java.net.URLEncoder.encode(newStatus, "UTF-8"));
            } else {
                response.sendRedirect(redirectUrl + "&statusUpdateError=Review not found or status not changed (ID: " + reviewId + ").");
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Log the error
            response.sendRedirect(redirectUrl + "&statusUpdateError=Database error: " + e.getMessage().replace(" ", "%20"));
        } finally {
            DBConnection.closeConnection(conn, pstmt, null);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Redirect GET requests to the reviews list page
        response.sendRedirect(request.getContextPath() + "/admin/viewReviews");
    }
}
