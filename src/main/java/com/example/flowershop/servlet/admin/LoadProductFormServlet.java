package com.example.flowershop.servlet.admin;

import com.example.flowershop.model.Category;
import com.example.flowershop.model.Product;
// import com.example.flowershop.model.User; // Not needed if AuthFilter handles protection
import com.example.flowershop.util.DBConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
// import javax.servlet.http.HttpSession; // Not needed if AuthFilter handles protection

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/admin/loadProductForm")
public class LoadProductFormServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        // AuthFilter is expected to handle admin authentication/authorization for /admin/*

        Connection conn = null;
        PreparedStatement pstmtCategories = null;
        PreparedStatement pstmtProduct = null;
        ResultSet rsCategories = null;
        ResultSet rsProduct = null;

        List<Category> categoryList = new ArrayList<>();
        Product productToEdit = null;
        boolean isEditMode = false;

        try {
            conn = DBConnection.getConnection();

            // 1. Fetch all categories
            String categoriesSql = "SELECT id, name FROM categories ORDER BY name ASC";
            pstmtCategories = conn.prepareStatement(categoriesSql);
            rsCategories = pstmtCategories.executeQuery();
            while (rsCategories.next()) {
                Category category = new Category();
                category.setId(rsCategories.getInt("id"));
                category.setName(rsCategories.getString("name"));
                categoryList.add(category);
            }
            request.setAttribute("categoryList", categoryList);

            // 2. Check for productId (edit mode)
            String productIdStr = request.getParameter("id"); // Using "id" as parameter name to match products.jsp
            if (productIdStr != null && !productIdStr.trim().isEmpty()) {
                try {
                    int productId = Integer.parseInt(productIdStr);
                    String productSql = "SELECT * FROM products WHERE id = ?";
                    pstmtProduct = conn.prepareStatement(productSql);
                    pstmtProduct.setInt(1, productId);
                    rsProduct = pstmtProduct.executeQuery();

                    if (rsProduct.next()) {
                        productToEdit = new Product();
                        productToEdit.setId(rsProduct.getInt("id"));
                        productToEdit.setName(rsProduct.getString("name"));
                        productToEdit.setDescription(rsProduct.getString("description"));
                        productToEdit.setPrice(rsProduct.getBigDecimal("price"));
                        productToEdit.setStock(rsProduct.getInt("stock"));
                        productToEdit.setCategoryId(rsProduct.getInt("category_id"));
                        productToEdit.setImageUrl(rsProduct.getString("image_url"));
                        productToEdit.setStatus(rsProduct.getString("status"));
                        // Other fields like sales_count, views, creation_date can be loaded if needed for display on form
                        isEditMode = true;
                        request.setAttribute("productToEdit", productToEdit);
                    } else {
                        // Product ID provided but not found, perhaps show an error or redirect
                        request.setAttribute("errorMessage", "Product with ID " + productId + " not found.");
                    }
                } catch (NumberFormatException e) {
                    request.setAttribute("errorMessage", "Invalid Product ID format.");
                }
            }
            request.setAttribute("isEditMode", isEditMode);

        } catch (SQLException e) {
            e.printStackTrace(); // Log or handle more gracefully
            request.setAttribute("errorMessage", "Database error loading product form: " + e.getMessage());
        } finally {
            DBConnection.closeConnection(null, pstmtCategories, rsCategories); // Close categories resources
            DBConnection.closeConnection(conn, pstmtProduct, rsProduct);   // Close product resources (conn closed here)
        }

        request.getRequestDispatcher("/admin/product_form.jsp").forward(request, response);
    }
}
