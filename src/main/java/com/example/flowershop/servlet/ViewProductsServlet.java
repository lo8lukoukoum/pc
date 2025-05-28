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
import java.util.ArrayList;
import java.util.List;

@WebServlet("/products")
public class ViewProductsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        List<Product> productList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String searchQuery = request.getParameter("searchQuery");
        request.setAttribute("searchQuery", searchQuery); // Pass back to JSP

        StringBuilder sqlBuilder = new StringBuilder("SELECT id, name, description, price, image_url, status, category_id, stock, creation_date, sales_count, views FROM products WHERE status = '上架'");

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            sqlBuilder.append(" AND (name LIKE ? OR description LIKE ?)");
        }
        // Add ORDER BY if needed, e.g., sqlBuilder.append(" ORDER BY creation_date DESC");

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sqlBuilder.toString());

            int paramIndex = 1;
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                String searchPattern = "%" + searchQuery.trim() + "%";
                pstmt.setString(paramIndex++, searchPattern);
                pstmt.setString(paramIndex++, searchPattern);
            }

            rs = pstmt.executeQuery();

            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getInt("id"));
                product.setName(rs.getString("name"));
                product.setDescription(rs.getString("description"));
                product.setPrice(rs.getBigDecimal("price"));
                product.setImageUrl(rs.getString("image_url"));
                product.setStatus(rs.getString("status"));
                product.setCategoryId(rs.getInt("category_id"));
                product.setStock(rs.getInt("stock"));
                product.setCreationDate(rs.getTimestamp("creation_date"));
                product.setSalesCount(rs.getInt("sales_count"));
                product.setViews(rs.getInt("views"));
                productList.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log or handle more gracefully
            request.setAttribute("errorMessage", "Error fetching products: " + e.getMessage());
            // Optionally forward to an error page or the same page with an error message
        } finally {
            DBConnection.closeConnection(conn, pstmt, rs);
        }

        request.setAttribute("productList", productList);
        request.getRequestDispatcher("products.jsp").forward(request, response);
    }
}
