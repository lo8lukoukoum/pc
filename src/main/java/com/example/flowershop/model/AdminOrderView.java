package com.example.flowershop.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class AdminOrderView {
    private int id; // order id
    private int userId;
    private String customerUsername;
    private Timestamp orderDate;
    private BigDecimal totalAmount;
    private String status;
    private String shippingAddress;
    private String contactPhone;

    // Default constructor
    public AdminOrderView() {
    }

    // Parameterized constructor
    public AdminOrderView(int id, int userId, String customerUsername, Timestamp orderDate, BigDecimal totalAmount, String status, String shippingAddress, String contactPhone) {
        this.id = id;
        this.userId = userId;
        this.customerUsername = customerUsername;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.shippingAddress = shippingAddress;
        this.contactPhone = contactPhone;
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

    public String getCustomerUsername() {
        return customerUsername;
    }

    public void setCustomerUsername(String customerUsername) {
        this.customerUsername = customerUsername;
    }

    public Timestamp getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    @Override
    public String toString() {
        return "AdminOrderView{" +
                "id=" + id +
                ", userId=" + userId +
                ", customerUsername='" + customerUsername + '\'' +
                ", orderDate=" + orderDate +
                ", totalAmount=" + totalAmount +
                ", status='" + status + '\'' +
                ", shippingAddress='" + shippingAddress + '\'' +
                ", contactPhone='" + contactPhone + '\'' +
                '}';
    }
}
