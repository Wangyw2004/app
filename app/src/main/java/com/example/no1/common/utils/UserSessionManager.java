package com.example.no1.common.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSessionManager {
    private static final String PREF_NAME = "user_session";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_DISPLAY_NAME = "display_name";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_ROLE = "role";

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

    public void saveUserSession(String username, String displayName, String token, String role) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_DISPLAY_NAME, displayName);
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_ROLE, role);
        editor.apply();
    }

    public void saveGuestSession() {
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.putString(KEY_DISPLAY_NAME, "游客");
        editor.putString(KEY_ROLE, "guest");
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, "");
    }

    public String getDisplayName() {
        return sharedPreferences.getString(KEY_DISPLAY_NAME, "游客");
    }

    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, "");
    }

    public String getRole() {
        return sharedPreferences.getString(KEY_ROLE, "guest");
    }

    public boolean isAdmin() {
        return "admin".equals(getRole());
    }

    public void logout() {
        editor.clear();
        editor.apply();
        saveGuestSession();
    }
}