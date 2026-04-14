package com.example.no1.common.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSessionManager {
    private static final String PREF_NAME = "user_session";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_NICKNAME = "nickname";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_ROLE = "role";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_BIRTH_YEAR = "birth_year";

    private static UserSessionManager instance;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private UserSessionManager(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static synchronized UserSessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new UserSessionManager(context);
        }
        return instance;
    }

    public void saveUserSession(String username, String nickname, String token, String role,
                                String email, String gender, int birthYear) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_NICKNAME, nickname != null ? nickname : username);
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_ROLE, role);
        editor.putString(KEY_EMAIL, email != null ? email : "");
        editor.putString(KEY_GENDER, gender != null ? gender : "保密");
        editor.putInt(KEY_BIRTH_YEAR, birthYear > 0 ? birthYear : 2000);
        editor.apply();
    }

    public void saveGuestSession() {
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.putString(KEY_NICKNAME, "游客");
        editor.putString(KEY_ROLE, "guest");
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, "");
    }

    public String getNickname() {
        return sharedPreferences.getString(KEY_NICKNAME, "游客");
    }

    public String getDisplayName() {
        return getNickname();
    }

    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, "");
    }

    public String getRole() {
        return sharedPreferences.getString(KEY_ROLE, "guest");
    }

    public String getEmail() {
        return sharedPreferences.getString(KEY_EMAIL, "");
    }

    public String getGender() {
        return sharedPreferences.getString(KEY_GENDER, "保密");
    }

    public int getBirthYear() {
        return sharedPreferences.getInt(KEY_BIRTH_YEAR, 2000);
    }

    public boolean isAdmin() {
        return "admin".equals(getRole());
    }

    public void updateUserInfo(String nickname, String gender, int birthYear, String email) {
        editor.putString(KEY_NICKNAME, nickname);
        editor.putString(KEY_GENDER, gender);
        editor.putInt(KEY_BIRTH_YEAR, birthYear);
        if (email != null) {
            editor.putString(KEY_EMAIL, email);
        }
        editor.apply();
    }

    public void logout() {
        editor.clear();
        editor.apply();
        saveGuestSession();
    }
}