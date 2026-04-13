// 位置：app/src/main/java/com/example/no1/models/User.java
package com.example.no1.features.auth.models;

public class User {
    private String username;
    private String password;
    private String token;
    private String email;
    private String displayName;
    private String avatar;
    private long createTime;

    // 构造函数1：基本登录信息
    public User(String username, String token) {
        this.username = username;
        this.token = token;
        this.displayName = username;
        this.createTime = System.currentTimeMillis();
    }

    // 构造函数2：完整信息
    public User(String username, String email, String displayName, String token) {
        this.username = username;
        this.email = email;
        this.displayName = displayName;
        this.token = token;
        this.createTime = System.currentTimeMillis();
    }

    // 构造函数3：空构造函数
    public User() {
        this.createTime = System.currentTimeMillis();
    }

    // Getters and Setters
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    // 辅助方法
    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", displayName='" + displayName + '\'' +
                ", createTime=" + createTime +
                '}';
    }

    // 检查用户信息是否完整
    public boolean isValid() {
        return username != null && !username.trim().isEmpty() &&
                token != null && !token.trim().isEmpty();
    }
}