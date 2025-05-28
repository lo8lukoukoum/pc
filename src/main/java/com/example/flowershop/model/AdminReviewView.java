package com.example.flowershop.model;

import java.sql.Timestamp;

public class AdminReviewView {
    private int id; // review id
    private int userId;
    private String username; // reviewer's username
    private int productId;
    private String productName;
    private int rating;
    private String comment;
    private String imageUrl;
    private Timestamp reviewDate;
    private boolean isAnonymous;
    private String status;

    // Default constructor
    public AdminReviewView() {
    }

    // Parameterized constructor
    public AdminReviewView(int id, int userId, String username, int productId, String productName, int rating, String comment, String imageUrl, Timestamp reviewDate, boolean isAnonymous, String status) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.productId = productId;
        this.productName = productName;
        this.rating = rating;
        this.comment = comment;
        this.imageUrl = imageUrl;
        this.reviewDate = reviewDate;
        this.isAnonymous = isAnonymous;
        this.status = status;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Timestamp getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(Timestamp reviewDate) {
        this.reviewDate = reviewDate;
    }

    public boolean isAnonymous() {
        return isAnonymous;
    }

    public void setAnonymous(boolean anonymous) {
        isAnonymous = anonymous;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "AdminReviewView{" +
                "id=" + id +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", reviewDate=" + reviewDate +
                ", isAnonymous=" + isAnonymous +
                ", status='" + status + '\'' +
                '}';
    }
}
