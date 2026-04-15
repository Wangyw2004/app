package com.example.no1.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.example.no1.features.auth.models.User;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class UserDataSource {
    private static final String PREF_NAME = "user_prefs";
    private static final String KEY_USERS = "users_list";

    private SharedPreferences sharedPreferences;
    private Gson gson;
    private static UserDataSource instance;

    private UserDataSource(Context context) {
        sharedPreferences = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static synchronized UserDataSource getInstance(Context context) {
        if (instance == null) {
            instance = new UserDataSource(context);
        }
        return instance;
    }

    public void saveUsers(List<User> users) {
        String json = gson.toJson(users);
        sharedPreferences.edit().putString(KEY_USERS, json).apply();
    }

    public List<User> loadUsers() {
        String json = sharedPreferences.getString(KEY_USERS, "");
        if (json.isEmpty()) {
            return getDefaultUsers();
        }
        Type type = new TypeToken<List<User>>(){}.getType();
        List<User> users = gson.fromJson(json, type);
        return users != null ? users : getDefaultUsers();
    }

    private List<User> getDefaultUsers() {
        List<User> users = new ArrayList<>();

        // 管理员账号
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword("123456");
        admin.setDisplayName("系统管理员");
        admin.setRole("admin");
        admin.setEmail("admin@example.com");
        users.add(admin);

        // 测试账号
        User test = new User();
        test.setUsername("test");
        test.setPassword("123456");
        test.setDisplayName("测试用户");
        test.setRole("user");
        test.setEmail("test@example.com");
        users.add(test);

        return users;
    }

    public void addUser(User user) {
        List<User> users = loadUsers();
        users.add(user);
        saveUsers(users);
    }

    public boolean isUsernameExists(String username) {
        List<User> users = loadUsers();
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public User getUserByUsername(String username) {
        List<User> users = loadUsers();
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }
    // 在 UserDataSource.java 中添加

    public boolean updatePassword(String username, String oldPassword, String newPassword) {
        List<User> users = loadUsers();
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                if (!user.getPassword().equals(oldPassword)) {
                    return false;
                }
                user.setPassword(newPassword);
                saveUsers(users);
                return true;
            }
        }
        return false;
    }

    public boolean resetPassword(String username) {
        List<User> users = loadUsers();
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                user.setPassword("123456");
                saveUsers(users);
                return true;
            }
        }
        return false;
    }
    // 在 UserDataSource 类中添加以下方法

    /**
     * 获取所有用户
     */
    public List<User> getAllUsers() {
        return loadUsers();
    }

    /**
     * 删除用户
     * @param username 用户名
     * @return true-成功, false-失败
     */
    public boolean deleteUser(String username) {
        List<User> users = loadUsers();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(username)) {
                users.remove(i);
                saveUsers(users);
                return true;
            }
        }
        return false;
    }


}