package com.example.flowershop.servlet;

import com.example.flowershop.model.User;
import com.example.flowershop.util.DBConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

@WebServlet("/submitReview")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 1,  // 1 MB
                 maxFileSize = 1024 * 1024 * 10, // 10 MB
                 maxRequestSize = 1024 * 1024 * 15 // 15 MB
)
public class SubmitReviewServlet extends HttpServlet {

    private static final String UPLOAD_DIR = "uploads" + File.separator + "review_images";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        // 1. Authentication Check
        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?authError=submitReviewAccess");
            return;
        }

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        int userId = loggedInUser.getId();

        // 2. Retrieve form fields
        String productIdStr = request.getParameter("productId");
        String orderIdStr = request.getParameter("orderId");
        String ratingStr = request.getParameter("rating");
        String comment = request.getParameter("comment");
        String isAnonymousStr = request.getParameter("isAnonymous"); // "true" or null

        Part filePart = request.getPart("reviewImage");
        String imageUrl = null;
        String uniqueFileName = null;

        // 3. Validate inputs
        String errorRedirectUrl = request.getContextPath() + "/submit_review.jsp?productId=" + productIdStr + "&orderId=" + orderIdStr;

        if (productIdStr == null || productIdStr.trim().isEmpty() ||
            orderIdStr == null || orderIdStr.trim().isEmpty() ||
            ratingStr == null || ratingStr.trim().isEmpty() ||
            comment == null || comment.trim().isEmpty()) {
            response.sendRedirect(errorRedirectUrl + "&reviewError=Missing required fields.");
            return;
        }

        int productId;
        int orderId;
        int rating;
        try {
            productId = Integer.parseInt(productIdStr);
            orderId = Integer.parseInt(orderIdStr);
            rating = Integer.parseInt(ratingStr);
            if (rating < 1 || rating > 5) {
                response.sendRedirect(errorRedirectUrl + "&reviewError=Rating must be between 1 and 5.");
                return;
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(errorRedirectUrl + "&reviewError=Invalid number format for ID or rating.");
            return;
        }

        // 4. File Upload Handling
        if (filePart != null && filePart.getSize() > 0) {
            String originalFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            if (originalFileName != null && !originalFileName.isEmpty()) {
                 String fileExtension = "";
                 int i = originalFileName.lastIndexOf('.');
                 if (i > 0) {
                     fileExtension = originalFileName.substring(i); // includes the dot
                 }
                uniqueFileName = UUID.randomUUID().toString() + fileExtension;

                String applicationPath = getServletContext().getRealPath("");
                String uploadFilePath = applicationPath + File.separator + UPLOAD_DIR;

                File uploadDir = new File(uploadFilePath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                try {
                    filePart.write(uploadFilePath + File.separator + uniqueFileName);
                    imageUrl = UPLOAD_DIR + File.separator + uniqueFileName;
                     // On Windows, replace backslashes for web path consistency
                    if (File.separatorChar == '\\') {
                        imageUrl = imageUrl.replace(File.separatorChar, '/');
                    }
                } catch (IOException e) {
                    e.printStackTrace(); // Log this error
                    response.sendRedirect(errorRedirectUrl + "&reviewError=File upload failed: " + e.getMessage());
                    return;
                }
            }
        }

        // 5. Database Operation
        Connection conn = null;
        PreparedStatement pstmt = null;
        // TODO: Add check to prevent duplicate reviews for the same product in the same order by the same user.
        // String checkExistingSql = "SELECT COUNT(*) FROM reviews WHERE user_id = ? AND product_id = ? AND order_id = ?";
        // ... (execute and if count > 0, redirect with error) ...

        String insertSql = "INSERT INTO reviews (user_id, product_id, order_id, rating, comment, image_url, review_date, is_anonymous, status) VALUES (?, ?, ?, ?, ?, ?, NOW(), ?, '待审核')";
        boolean isAnonymous = "true".equals(isAnonymousStr);

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(insertSql);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, productId);
            pstmt.setInt(3, orderId);
            pstmt.setInt(4, rating);
            pstmt.setString(5, comment);
            pstmt.setString(6, imageUrl); // Can be null if no image uploaded
            pstmt.setBoolean(7, isAnonymous);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                response.sendRedirect(request.getContextPath() + "/orderDetail?orderId=" + orderId + "&reviewSuccess=true");
            } else {
                response.sendRedirect(errorRedirectUrl + "&reviewError=Failed to submit review. Please try again.");
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Log the error
            response.sendRedirect(errorRedirectUrl + "&reviewError=Database error: " + e.getMessage().replace(" ", "%20"));
        } finally {
            DBConnection.closeConnection(conn, pstmt, null);
        }
    }
}
