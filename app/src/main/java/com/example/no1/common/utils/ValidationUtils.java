package com.example.no1.common.utils;

import java.util.regex.Pattern;

public class ValidationUtils {

    // 用户名验证：至少3个字符，只能包含字母、数字和下划线
    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        String pattern = "^[a-zA-Z0-9_]{3,20}$";
        return Pattern.matches(pattern, username);
    }

    // 密码验证：至少6个字符
    public static boolean isValidPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return false;
        }
        return password.length() >= 6;
    }

    // 邮箱验证（可选）
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String pattern = "^[A-Za-z0-9+_.-]+@(.+)$";
        return Pattern.matches(pattern, email);
    }
}