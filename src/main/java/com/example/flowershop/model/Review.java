package com.example.flowershop.model;

import java.sql.Timestamp;

public class Review {
    private int id;
    private int userId;
    private int productId;
    private int orderId;
    private int rating;
    private String comment;
    private String imageUrl;
    private String videoUrl;
    private Timestamp reviewDate;
    private boolean isAnonymous;
    private String status;
    private String username; // For displaying reviewer's name from JOIN

    // Default constructor
    public Review() {
    }

    // Parameterized constructor
    public Review(int id, int userId, int productId, int orderId, int rating, String comment, String imageUrl, String videoUrl, Timestamp reviewDate, boolean isAnonymous, String status) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.orderId = orderId;
        this.rating = rating;
        this.comment = comment;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
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

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
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

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", userId=" + userId +
                ", productId=" + productId +
                ", orderId=" + orderId +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", reviewDate=" + reviewDate +
                ", isAnonymous=" + isAnonymous +
                ", status='" + status + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
