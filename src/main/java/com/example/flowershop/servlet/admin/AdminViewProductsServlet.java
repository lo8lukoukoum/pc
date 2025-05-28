package com.example.flowershop.servlet.admin;

import com.example.flowershop.model.Product;
import com.example.flowershop.util.DBConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
// No need to check session/role here as AuthFilter handles it for /admin/*
// import javax.servlet.http.HttpSession;
// import com.example.flowershop.model.User;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/admin/viewProducts")
public class AdminViewProductsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        List<Product> productList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        // AuthFilter is expected to protect /admin/* paths
        // HttpSession session = request.getSession(false);
        // User loggedInUser = (session != null) ? (User) session.getAttribute("loggedInUser") : null;
        // if (loggedInUser == null || !"admin".equals(loggedInUser.getRole())) {
        //     response.sendRedirect(request.getContextPath() + "/login.jsp?authError=adminRequired");
        //     return;
        // }

        String sql = "SELECT p.id, p.name, p.price, p.stock, p.category_id, c.name as category_name, p.image_url, p.status, p.sales_count, p.views, p.description, p.creation_date " +
                     "FROM products p LEFT JOIN categories c ON p.category_id = c.id ORDER BY p.id ASC";

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getInt("id"));
                product.setName(rs.getString("name"));
                product.setPrice(rs.getBigDecimal("price"));
                product.setStock(rs.getInt("stock"));
                product.setCategoryId(rs.getInt("category_id"));
                product.setCategoryName(rs.getString("category_name"));
                product.setImageUrl(rs.getString("image_url"));
                product.setStatus(rs.getString("status"));
                product.setSalesCount(rs.getInt("sales_count"));
                product.setViews(rs.getInt("views"));
                product.setDescription(rs.getString("description")); // Added description
                product.setCreationDate(rs.getTimestamp("creation_date")); // Added creation_date
                productList.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log or handle more gracefully
            request.setAttribute("errorMessage", "Error fetching products for admin view: " + e.getMessage());
        } finally {
            DBConnection.closeConnection(conn, pstmt, rs);
        }

        request.setAttribute("productList", productList);
        request.getRequestDispatcher("/admin/products.jsp").forward(request, response);
    }
}
