package com.example.flowershop.model;

import java.sql.Timestamp;

public class User {
    private int id;
    private String username;
    private String password;
    private String role;
    private Timestamp registrationDate;
    private String email;
    private String phoneNumber;
    private String personalSignature;

    // Default constructor
    public User() {
    }

    // Parameterized constructor
    public User(int id, String username, String password, String role, Timestamp registrationDate, String email, String phoneNumber, String personalSignature) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.registrationDate = registrationDate;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.personalSignature = personalSignature;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Timestamp getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Timestamp registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPersonalSignature() {
        return personalSignature;
    }

    public void setPersonalSignature(String personalSignature) {
        this.personalSignature = personalSignature;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + "********" + '\'' + // Mask password
                ", role='" + role + '\'' +
                ", registrationDate=" + registrationDate +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", personalSignature='" + personalSignature + '\'' +
                '}';
    }
}
