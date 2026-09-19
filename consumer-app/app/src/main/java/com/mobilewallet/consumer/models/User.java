package com.mobilewallet.consumer.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class User implements Serializable {

    @SerializedName("id")
    private Long id;

    @SerializedName("username")
    private String username;

    @SerializedName("email")
    private String email;

    @SerializedName("phone")
    private String phone;

    @SerializedName("firstName")
    private String firstName;

    @SerializedName("lastName")
    private String lastName;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("role")
    private String role;

    @SerializedName("enabled")
    private Boolean enabled;

    @SerializedName("locked")
    private Boolean locked;

    @SerializedName("wallet")
    private Wallet wallet;

    public User() {}

    // Getters
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getFullName() {
        if (fullName != null) return fullName;
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
    }
    public String getRole() { return role; }
    public Boolean getEnabled() { return enabled; }
    public Boolean getLocked() { return locked; }
    public Wallet getWallet() { return wallet; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setRole(String role) { this.role = role; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
    public void setLocked(Boolean locked) { this.locked = locked; }
    public void setWallet(Wallet wallet) { this.wallet = wallet; }

    public String getInitials() {
        String fn = firstName != null && !firstName.isEmpty() ? firstName.substring(0, 1) : "";
        String ln = lastName != null && !lastName.isEmpty() ? lastName.substring(0, 1) : "";
        return (fn + ln).toUpperCase();
    }
}