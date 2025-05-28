package com.example.flowershop.servlet.admin;

import com.example.flowershop.model.User;
import com.example.flowershop.util.DBConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
// AuthFilter handles admin check
// import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@WebServlet("/admin/saveCustomer")
public class SaveCustomerServlet extends HttpServlet {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        // Admin role check is handled by AuthFilter for /admin/*

        String userIdStr = request.getParameter("userId");
        String username = request.getParameter("username"); // Username is only editable on add
        String email = request.getParameter("email");
        String phoneNumber = request.getParameter("phoneNumber");
        String role = request.getParameter("role");
        String personalSignature = request.getParameter("personalSignature");
        String password = request.getParameter("password"); // Only for add mode

        boolean isEditMode = (userIdStr != null && !userIdStr.trim().isEmpty());
        int userId = 0;

        List<String> errors = new ArrayList<>();

        // Validation
        if (!isEditMode) { // Add mode
            if (username == null || username.trim().isEmpty()) errors.add("Username is required.");
            if (password == null || password.isEmpty()) errors.add("Password is required for new user.");
        }
        if (email == null || email.trim().isEmpty()) errors.add("Email is required.");
        else if (!EMAIL_PATTERN.matcher(email).matches()) errors.add("Invalid email format.");
        if (role == null || role.trim().isEmpty()) errors.add("Role is required.");
        else if (!Arrays.asList("customer", "admin").contains(role)) errors.add("Invalid role selected.");


        if (!errors.isEmpty()) {
            repopulateFormAndForward(request, response, errors, isEditMode);
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmtCheck = null;
        ResultSet rsCheck = null;

        try {
            conn = DBConnection.getConnection();

            // Check for username uniqueness in add mode
            if (!isEditMode) {
                String checkUsernameSql = "SELECT id FROM users WHERE username = ?";
                pstmtCheck = conn.prepareStatement(checkUsernameSql);
                pstmtCheck.setString(1, username);
                rsCheck = pstmtCheck.executeQuery();
                if (rsCheck.next()) {
                    errors.add("Username '" + username + "' already exists.");
                    repopulateFormAndForward(request, response, errors, isEditMode);
                    return;
                }
            }
             // Optional: Check for email uniqueness (if required, more complex if email can be changed by user)
            // String checkEmailSql = "SELECT id FROM users WHERE email = ? AND id != ?"; (for edit mode)
            // String checkEmailSqlAdd = "SELECT id FROM users WHERE email = ?"; (for add mode)
            // ... execute and add error if email exists ...

            User user = new User();
            if (isEditMode) {
                userId = Integer.parseInt(userIdStr); // Checked for non-empty earlier
                user.setId(userId);
                // Username is not updated in edit mode, role might be
                user.setEmail(email);
                user.setPhoneNumber(phoneNumber);
                user.setRole(role);
                user.setPersonalSignature(personalSignature);

                String sql = "UPDATE users SET email = ?, phone_number = ?, role = ?, personal_signature = ? WHERE id = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, user.getEmail());
                pstmt.setString(2, user.getPhoneNumber());
                pstmt.setString(3, user.getRole());
                pstmt.setString(4, user.getPersonalSignature());
                pstmt.setInt(5, user.getId());

            } else { // Add mode
                user.setUsername(username);
                user.setPassword(password); // Store as plain text as per current spec
                user.setEmail(email);
                user.setPhoneNumber(phoneNumber);
                user.setRole(role);
                user.setPersonalSignature(personalSignature);

                String sql = "INSERT INTO users (username, password, email, phone_number, role, personal_signature, registration_date) VALUES (?, ?, ?, ?, ?, ?, NOW())";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, user.getUsername());
                pstmt.setString(2, user.getPassword());
                pstmt.setString(3, user.getEmail());
                pstmt.setString(4, user.getPhoneNumber());
                pstmt.setString(5, user.getRole());
                pstmt.setString(6, user.getPersonalSignature());
            }

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                response.sendRedirect(request.getContextPath() + "/admin/viewCustomers?saveSuccess=true&userId=" + (isEditMode ? userId : "new"));
            } else {
                errors.add("Failed to save customer. No rows affected (customer might not exist for update).");
                repopulateFormAndForward(request, response, errors, isEditMode);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            errors.add("Database error: " + e.getMessage());
            repopulateFormAndForward(request, response, errors, isEditMode);
        } catch (NumberFormatException e) { // For parsing userId in edit mode
            errors.add("Invalid User ID format for update.");
            repopulateFormAndForward(request, response, errors, isEditMode);
        } finally {
            DBConnection.closeConnection(null, pstmtCheck, rsCheck);
            DBConnection.closeConnection(conn, pstmt, null);
        }
    }

    private void repopulateFormAndForward(HttpServletRequest request, HttpServletResponse response, List<String> errors, boolean isEditMode) throws ServletException, IOException {
        request.setAttribute("errorMessage", String.join("<br>", errors));

        User customerData = new User();
        if (isEditMode) {
            try {
                customerData.setId(Integer.parseInt(request.getParameter("userId")));
            } catch (NumberFormatException ignored) {}
        }
        customerData.setUsername(request.getParameter("username")); // Will be null if readonly and not submitted, or submitted value
        customerData.setEmail(request.getParameter("email"));
        customerData.setPhoneNumber(request.getParameter("phoneNumber"));
        customerData.setRole(request.getParameter("role"));
        customerData.setPersonalSignature(request.getParameter("personalSignature"));
        
        request.setAttribute("customerToEdit", customerData);
        request.setAttribute("isEditMode", isEditMode);
        List<String> roleList = Arrays.asList("customer", "admin");
        request.setAttribute("roleList", roleList);

        request.getRequestDispatcher("/admin/customer_form.jsp").forward(request, response);
    }
}
