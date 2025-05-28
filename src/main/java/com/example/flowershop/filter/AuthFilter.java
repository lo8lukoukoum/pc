package com.example.flowershop.filter;

import com.example.flowershop.model.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(urlPatterns = {"/user/*", "/admin/*", "/cart", "/cart.jsp", "/checkout", "/checkout.jsp", "/order_history.jsp", "/orderHistory", "/addToCart", "/updateCart", "/createOrder", "/orderDetail", "/loadReviewForm", "/submitReview"})
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Can be left empty or used for loading configuration
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false); // false = don't create if it doesn't exist

        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        // Authentication Check
        boolean isLoggedIn = (session != null && session.getAttribute("loggedInUser") != null);

        // Check if the path is a protected path (excluding /admin/ for now, as it has specific role check)
        boolean isProtectedUserPath = path.startsWith("/user/") || path.equals("/cart.jsp") || path.equals("/cart") || path.equals("/checkout.jsp") || path.equals("/checkout") || path.equals("/order_history.jsp") || path.equals("/orderHistory") || path.equals("/addToCart") || path.equals("/updateCart") || path.equals("/createOrder") || path.equals("/orderDetail") || path.equals("/loadReviewForm") || path.equals("/submitReview");

        if (isProtectedUserPath && !isLoggedIn) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp?authError=true");
            return;
        }

        // Authorization Check (Role-based for admin paths)
        if (path.startsWith("/admin/")) {
            if (!isLoggedIn) {
                // If not logged in at all, redirect to login
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp?authError=adminRequired");
                return;
            }
            // If logged in, check role
            User user = (User) session.getAttribute("loggedInUser");
            boolean isAdmin = "admin".equals(user.getRole());

            if (!isAdmin) {
                // Logged in but not an admin
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/index.jsp?authError=adminOnly");
                return;
            }
        }

        // If all checks pass, allow the request to proceed
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Can be left empty
    }
}
