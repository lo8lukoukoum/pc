package com.example.flowershop.servlet.admin;

import com.example.flowershop.util.DBConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/admin/deleteProduct")
public class DeleteProductServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        // AuthFilter handles admin check for /admin/*

        String productIdStr = request.getParameter("productId");
        int productId;

        if (productIdStr == null || productIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/viewProducts?deleteError=Missing Product ID");
            return;
        }

        try {
            productId = Integer.parseInt(productIdStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/viewProducts?deleteError=Invalid Product ID format");
            return;
        }

        Connection conn = null;
        PreparedStatement pstmtGetImage = null;
        PreparedStatement pstmtDeleteProduct = null;
        ResultSet rsImage = null;
        String imageUrl = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // 1. (Optional) Get image_url before deleting the product
            String getImageSql = "SELECT image_url FROM products WHERE id = ?";
            pstmtGetImage = conn.prepareStatement(getImageSql);
            pstmtGetImage.setInt(1, productId);
            rsImage = pstmtGetImage.executeQuery();
            if (rsImage.next()) {
                imageUrl = rsImage.getString("image_url");
            }

            // 2. Delete the product from the database
            String deleteSql = "DELETE FROM products WHERE id = ?";
            pstmtDeleteProduct = conn.prepareStatement(deleteSql);
            pstmtDeleteProduct.setInt(1, productId);
            int rowsAffected = pstmtDeleteProduct.executeUpdate();

            if (rowsAffected > 0) {
                // 3. (Optional) Delete the image file from the server
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    String applicationPath = getServletContext().getRealPath("");
                    // Ensure imageUrl is a relative path from webapp root
                    if (imageUrl.startsWith(request.getContextPath())) {
                         imageUrl = imageUrl.substring(request.getContextPath().length());
                    }
                    // Normalize path separators for the server's file system
                    String serverFilePath = applicationPath + File.separator + imageUrl.replace('/', File.separatorChar);
                    File imageFile = new File(serverFilePath);
                    if (imageFile.exists()) {
                        if (imageFile.delete()) {
                            System.out.println("Deleted image file: " + serverFilePath);
                        } else {
                            System.err.println("Failed to delete image file: " + serverFilePath);
                            // Decide if this failure should roll back the DB transaction
                            // For now, we'll proceed with DB commit even if file deletion fails
                        }
                    }
                }
                conn.commit(); // Commit transaction
                response.sendRedirect(request.getContextPath() + "/admin/viewProducts?deleteSuccess=true&productId=" + productId);
            } else {
                conn.rollback(); // Rollback if no rows affected (product not found)
                response.sendRedirect(request.getContextPath() + "/admin/viewProducts?deleteError=Product not found or could not be deleted (ID: " + productId + ")");
            }

        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            String errorMessage = "Database error during product deletion: " + e.getMessage();
            // Check for foreign key violation (MySQL error code 1451)
            if (e.getErrorCode() == 1451) { // Error code for foreign key constraint violation
                errorMessage = "Cannot delete product (ID: " + productId + ") because it is referenced by existing orders or other records. Please remove associated records first.";
            }
            response.sendRedirect(request.getContextPath() + "/admin/viewProducts?deleteError=" + java.net.URLEncoder.encode(errorMessage, "UTF-8"));
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); }
            DBConnection.closeConnection(null, pstmtGetImage, rsImage);
            DBConnection.closeConnection(conn, pstmtDeleteProduct, null); // conn will be closed here
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Redirect GET requests to the product list page, discouraging deletion via GET
        response.sendRedirect(request.getContextPath() + "/admin/viewProducts");
    }
}
