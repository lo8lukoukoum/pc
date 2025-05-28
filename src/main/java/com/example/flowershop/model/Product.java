package com.example.flowershop.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Product {
    private int id;
    private String name;
    private String description;
    private BigDecimal price;
    private int stock;
    private int categoryId;
    private String imageUrl;
    private String status;
    private Timestamp creationDate;
    private int salesCount;
    private int views;

    // Default constructor
    public Product() {
    }

    // Parameterized constructor
    public Product(int id, String name, String description, BigDecimal price, int stock, int categoryId, String imageUrl, String status, Timestamp creationDate, int salesCount, int views) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.categoryId = categoryId;
        this.imageUrl = imageUrl;
        this.status = status;
        this.creationDate = creationDate;
        this.salesCount = salesCount;
        this.views = views;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    public int getSalesCount() {
        return salesCount;
    }

    public void setSalesCount(int salesCount) {
        this.salesCount = salesCount;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                ", categoryId=" + categoryId +
                ", imageUrl='" + imageUrl + '\'' +
                ", status='" + status + '\'' +
                ", creationDate=" + creationDate +
                ", salesCount=" + salesCount +
                ", views=" + views +
                '}';
    }
}
