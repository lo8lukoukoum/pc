package com.example.flowershop.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBConnection {

    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/flower_shop?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&characterEncoding=UTF-8";
    private static final String USER = "root"; // Replace if your MySQL username is different
    private static final String PASS = "root"; // IMPORTANT: Replace with your actual MySQL password

    static {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            // Consider a more robust error handling mechanism
            e.printStackTrace(); 
            throw new RuntimeException("Failed to load JDBC driver: " + JDBC_DRIVER, e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    public static void closeConnection(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        try {
            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log or handle more gracefully
        }
        try {
            if (pstmt != null && !pstmt.isClosed()) {
                pstmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log or handle more gracefully
        }
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log or handle more gracefully
        }
    }
    
    // Overloaded closer for when there's no ResultSet
    public static void closeConnection(Connection conn, PreparedStatement pstmt) {
        closeConnection(conn, pstmt, null);
    }

    // Optional: Main method for quick connection testing
    public static void main(String[] args) {
        Connection conn = null;
        try {
            System.out.println("Connecting to database...");
            conn = getConnection();
            if (conn != null) {
                System.out.println("Database connection successful!");
            } else {
                System.out.println("Failed to make connection!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Database connection failed: " + e.getMessage());
        } finally {
            if (conn != null) {
                closeConnection(conn, null, null);
                System.out.println("Database connection closed.");
            }
        }
    }
}
