package com.example.no1.data.remote;

import android.os.Handler;
import android.os.Looper;
import com.example.no1.features.auth.models.LoginRequest;
import com.example.no1.features.auth.models.LoginResponse;
import com.example.no1.features.auth.models.User;
import java.util.HashMap;
import java.util.Map;

public class ApiService {

    private static final int NETWORK_DELAY_MS = 1500;

    private static final Map<String, UserInfo> VALID_USERS = new HashMap<>();

    static {
        // 普通用户
        VALID_USERS.put("test", new UserInfo("test", "123456", "test@example.com", "测试用户", "user", true));
        VALID_USERS.put("user1", new UserInfo("user1", "123456", "user1@example.com", "普通用户1", "user", true));
        VALID_USERS.put("user2", new UserInfo("user2", "123456", "user2@example.com", "普通用户2", "user", true));

        // 管理员账号
        VALID_USERS.put("admin", new UserInfo("admin", "123456", "admin@example.com", "系统管理员", "admin", true));

        // 锁定账号
        VALID_USERS.put("locked", new UserInfo("locked", "123456", "locked@example.com", "已锁定用户", "user", false));
    }

    private static class UserInfo {
        String username;
        String password;
        String email;
        String displayName;
        String role;
        boolean isActive;

        UserInfo(String username, String password, String email, String displayName, String role, boolean isActive) {
            this.username = username;
            this.password = password;
            this.email = email;
            this.displayName = displayName;
            this.role = role;
            this.isActive = isActive;
        }
    }

    public interface LoginCallback {
        void onSuccess(LoginResponse response);
        void onError(String errorMessage);
    }

    public static void login(LoginRequest request, LoginCallback callback) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            String username = request.getUsername();
            String password = request.getPassword();

            if (!VALID_USERS.containsKey(username)) {
                callback.onError("用户名不存在");
                return;
            }

            UserInfo userInfo = VALID_USERS.get(username);

            if (!userInfo.isActive) {
                callback.onError("账号已被锁定，请联系管理员");
                return;
            }

            if (!userInfo.password.equals(password)) {
                callback.onError("密码错误");
                return;
            }

            User user = new User(username, "user_token_" + System.currentTimeMillis());
            user.setEmail(userInfo.email);
            user.setDisplayName(userInfo.displayName);
            user.setRole(userInfo.role);

            String welcomeMessage = "admin".equals(userInfo.role) ?
                    "欢迎管理员 " + userInfo.displayName + "！您拥有所有管理权限。" :
                    "登录成功！欢迎回来，" + userInfo.displayName;

            LoginResponse response = new LoginResponse(
                    true,
                    welcomeMessage,
                    "token_" + System.currentTimeMillis() + "_" + username,
                    user
            );
            callback.onSuccess(response);

        }, NETWORK_DELAY_MS);
    }
}