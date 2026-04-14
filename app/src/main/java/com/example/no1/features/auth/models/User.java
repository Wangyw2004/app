package com.example.no1.features.auth.models;

public class User {
    private String username;
    private String password;
    private String token;
    private String email;
    private String displayName;
    private String avatar;
    private String role;      // "user" 普通用户, "admin" 管理员
    private long createTime;

    public User(String username, String token) {
        this.username = username;
        this.token = token;
        this.displayName = username;
        this.role = "user";
        this.createTime = System.currentTimeMillis();
    }

    public User(String username, String email, String displayName, String token, String role) {
        this.username = username;
        this.email = email;
        this.displayName = displayName;
        this.token = token;
        this.role = role != null ? role : "user";
        this.createTime = System.currentTimeMillis();
    }

    public User() {
        this.createTime = System.currentTimeMillis();
        this.role = "user";
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public long getCreateTime() { return createTime; }
    public void setCreateTime(long createTime) { this.createTime = createTime; }

    public boolean isAdmin() {
        return "admin".equals(role);
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", displayName='" + displayName + '\'' +
                ", role='" + role + '\'' +
                '}';
    }

    public boolean isValid() {
        return username != null && !username.trim().isEmpty() &&
                token != null && !token.trim().isEmpty();
    }
}