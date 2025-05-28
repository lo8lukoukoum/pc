package com.example.flowershop.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false); // false means do not create new session if one doesn't exist

        if (session != null) {
            session.invalidate();
        }

        // Redirect to login page with a logout success message
        response.sendRedirect(request.getContextPath() + "/login.jsp?logoutSuccess=true");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Delegate POST requests to doGet, as the logout action is the same
        doGet(request, response);
    }
}
