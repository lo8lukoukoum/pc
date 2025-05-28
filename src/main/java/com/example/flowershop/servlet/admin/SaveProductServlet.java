package com.example.flowershop.servlet.admin;

import com.example.flowershop.model.Category;
import com.example.flowershop.model.Product;
import com.example.flowershop.util.DBConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@WebServlet("/admin/saveProduct")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 1,  // 1 MB
                 maxFileSize = 1024 * 1024 * 10, // 10 MB
                 maxRequestSize = 1024 * 1024 * 15 // 15 MB
)
public class SaveProductServlet extends HttpServlet {

    private static final String UPLOAD_DIR = "uploads" + File.separator + "product_images";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        // AuthFilter handles admin check

        String productIdStr = request.getParameter("productId");
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String priceStr = request.getParameter("price");
        String stockStr = request.getParameter("stock");
        String categoryIdStr = request.getParameter("categoryId");
        String status = request.getParameter("status");
        String existingImageUrl = request.getParameter("existingImageUrl");
        Part filePart = request.getPart("productImage");

        BigDecimal price = null;
        int stock = 0;
        int categoryId = 0;
        boolean isEditMode = (productIdStr != null && !productIdStr.trim().isEmpty());

        // Validation
        List<String> errors = new ArrayList<>();
        if (name == null || name.trim().isEmpty()) errors.add("Product name is required.");
        if (priceStr == null || priceStr.trim().isEmpty()) errors.add("Price is required.");
        else {
            try {
                price = new BigDecimal(priceStr);
                if (price.compareTo(BigDecimal.ZERO) < 0) errors.add("Price cannot be negative.");
            } catch (NumberFormatException e) { errors.add("Invalid price format."); }
        }
        if (stockStr == null || stockStr.trim().isEmpty()) errors.add("Stock is required.");
        else {
            try {
                stock = Integer.parseInt(stockStr);
                if (stock < 0) errors.add("Stock cannot be negative.");
            } catch (NumberFormatException e) { errors.add("Invalid stock format."); }
        }
        if (categoryIdStr == null || categoryIdStr.trim().isEmpty()) errors.add("Category is required.");
        else {
            try {
                categoryId = Integer.parseInt(categoryIdStr);
            } catch (NumberFormatException e) { errors.add("Invalid category ID format."); }
        }
        if (status == null || status.trim().isEmpty()) errors.add("Status is required.");

        if (!errors.isEmpty()) {
            repopulateFormAndForward(request, response, errors, isEditMode, productIdStr);
            return;
        }

        // File Upload Handling
        String newImageUrl = null;
        if (filePart != null && filePart.getSize() > 0) {
            String originalFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            if (originalFileName != null && !originalFileName.isEmpty()) {
                String fileExtension = "";
                int i = originalFileName.lastIndexOf('.');
                if (i > 0) fileExtension = originalFileName.substring(i);
                String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

                String applicationPath = getServletContext().getRealPath("");
                String uploadFilePath = applicationPath + File.separator + UPLOAD_DIR;
                File uploadDir = new File(uploadFilePath);
                if (!uploadDir.exists()) uploadDir.mkdirs();

                try {
                    filePart.write(uploadFilePath + File.separator + uniqueFileName);
                    newImageUrl = UPLOAD_DIR + File.separator + uniqueFileName;
                    if (File.separatorChar == '\\') newImageUrl = newImageUrl.replace(File.separatorChar, '/');
                } catch (IOException e) {
                    e.printStackTrace();
                    errors.add("File upload failed: " + e.getMessage());
                    repopulateFormAndForward(request, response, errors, isEditMode, productIdStr);
                    return;
                }
            }
        } else {
            if (isEditMode) newImageUrl = existingImageUrl;
        }

        // Database Operation
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql;

        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setCategoryId(categoryId);
        product.setImageUrl(newImageUrl);
        product.setStatus(status);

        try {
            conn = DBConnection.getConnection();
            if (isEditMode) {
                product.setId(Integer.parseInt(productIdStr));
                sql = "UPDATE products SET name=?, description=?, price=?, stock=?, category_id=?, image_url=?, status=? WHERE id=?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, product.getName());
                pstmt.setString(2, product.getDescription());
                pstmt.setBigDecimal(3, product.getPrice());
                pstmt.setInt(4, product.getStock());
                pstmt.setInt(5, product.getCategoryId());
                pstmt.setString(6, product.getImageUrl());
                pstmt.setString(7, product.getStatus());
                pstmt.setInt(8, product.getId());
            } else {
                sql = "INSERT INTO products (name, description, price, stock, category_id, image_url, status, creation_date, sales_count, views) VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), 0, 0)";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, product.getName());
                pstmt.setString(2, product.getDescription());
                pstmt.setBigDecimal(3, product.getPrice());
                pstmt.setInt(4, product.getStock());
                pstmt.setInt(5, product.getCategoryId());
                pstmt.setString(6, product.getImageUrl());
                pstmt.setString(7, product.getStatus());
            }

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                response.sendRedirect(request.getContextPath() + "/admin/viewProducts?saveSuccess=true");
            } else {
                errors.add("Failed to save product. No rows affected.");
                repopulateFormAndForward(request, response, errors, isEditMode, productIdStr);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            errors.add("Database error: " + e.getMessage());
            repopulateFormAndForward(request, response, errors, isEditMode, productIdStr);
        } catch (NumberFormatException e) { // For productId parsing in edit mode
             errors.add("Invalid Product ID for update.");
             repopulateFormAndForward(request, response, errors, isEditMode, productIdStr);
        } finally {
            DBConnection.closeConnection(conn, pstmt, null);
        }
    }

    private void repopulateFormAndForward(HttpServletRequest request, HttpServletResponse response, List<String> errors, boolean isEditMode, String productIdStr) throws ServletException, IOException {
        // Set errors to display on form
        request.setAttribute("errorMessage", String.join("<br>", errors));

        // Repopulate form fields from parameters
        Product productData = new Product();
        if (isEditMode && productIdStr != null && !productIdStr.isEmpty()) {
            try { productData.setId(Integer.parseInt(productIdStr)); } catch (NumberFormatException ignored) {}
        }
        productData.setName(request.getParameter("name"));
        productData.setDescription(request.getParameter("description"));
        try { productData.setPrice(new BigDecimal(request.getParameter("price"))); } catch (Exception ignored) {}
        try { productData.setStock(Integer.parseInt(request.getParameter("stock"))); } catch (Exception ignored) {}
        try { productData.setCategoryId(Integer.parseInt(request.getParameter("categoryId"))); } catch (Exception ignored) {}
        productData.setStatus(request.getParameter("status"));
        productData.setImageUrl(request.getParameter("existingImageUrl")); // Keep existing if new upload failed or not provided

        request.setAttribute("productToEdit", productData);
        request.setAttribute("isEditMode", isEditMode);

        // Fetch categories again (as in LoadProductFormServlet)
        List<Category> categoryList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmtCategories = null;
        ResultSet rsCategories = null;
        try {
            conn = DBConnection.getConnection();
            String categoriesSql = "SELECT id, name FROM categories ORDER BY name ASC";
            pstmtCategories = conn.prepareStatement(categoriesSql);
            rsCategories = pstmtCategories.executeQuery();
            while (rsCategories.next()) {
                Category category = new Category();
                category.setId(rsCategories.getInt("id"));
                category.setName(rsCategories.getString("name"));
                categoryList.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log this error, but form might still be usable without categories if it fails
            request.setAttribute("errorMessage", String.join("<br>", errors) + "<br>Could not reload categories: " + e.getMessage());
        } finally {
            DBConnection.closeConnection(conn, pstmtCategories, rsCategories);
        }
        request.setAttribute("categoryList", categoryList);

        request.getRequestDispatcher("/admin/product_form.jsp").forward(request, response);
    }
}
