package com.example.flowershop.model;

import java.math.BigDecimal;

public class OrderItemDetail {
    private int productId;
    private int quantity;
    private BigDecimal priceAtPurchase;
    private String productName;
    private String productImageUrl;
    private BigDecimal subtotal;

    // Default constructor
    public OrderItemDetail() {
    }

    // Parameterized constructor
    public OrderItemDetail(int productId, int quantity, BigDecimal priceAtPurchase, String productName, String productImageUrl) {
        this.productId = productId;
        this.quantity = quantity;
        this.priceAtPurchase = priceAtPurchase;
        this.productName = productName;
        this.productImageUrl = productImageUrl;
        if (this.priceAtPurchase != null) {
            this.subtotal = this.priceAtPurchase.multiply(BigDecimal.valueOf(this.quantity));
        } else {
            this.subtotal = BigDecimal.ZERO;
        }
    }

    // Getters and Setters
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        // Recalculate subtotal if quantity changes
        if (this.priceAtPurchase != null) {
            this.subtotal = this.priceAtPurchase.multiply(BigDecimal.valueOf(this.quantity));
        }
    }

    public BigDecimal getPriceAtPurchase() {
        return priceAtPurchase;
    }

    public void setPriceAtPurchase(BigDecimal priceAtPurchase) {
        this.priceAtPurchase = priceAtPurchase;
        // Recalculate subtotal if price changes
        if (this.priceAtPurchase != null) {
            this.subtotal = this.priceAtPurchase.multiply(BigDecimal.valueOf(this.quantity));
        }
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        // Typically, subtotal is calculated, so direct setting might be less common
        // or could be used for overriding.
        this.subtotal = subtotal;
    }

    @Override
    public String toString() {
        return "OrderItemDetail{" +
                "productId=" + productId +
                ", quantity=" + quantity +
                ", priceAtPurchase=" + priceAtPurchase +
                ", productName='" + productName + '\'' +
                ", productImageUrl='" + productImageUrl + '\'' +
                ", subtotal=" + subtotal +
                '}';
    }
}
