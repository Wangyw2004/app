package com.example.no1.data.remote;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.example.no1.data.local.UserDataSource;
import com.example.no1.features.auth.models.LoginRequest;
import com.example.no1.features.auth.models.LoginResponse;
import com.example.no1.features.auth.models.User;

public class ApiService {

    private static final int NETWORK_DELAY_MS = 1500;
    private static UserDataSource userDataSource;

    public interface LoginCallback {
        void onSuccess(LoginResponse response);
        void onError(String errorMessage);
    }

    // 初始化数据源（需要在 Application 或首次调用时设置）
    public static void init(Context context) {
        if (userDataSource == null) {
            userDataSource = UserDataSource.getInstance(context);
        }
    }

    public static void login(LoginRequest request, LoginCallback callback) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            String username = request.getUsername();
            String password = request.getPassword();

            // 从 UserDataSource 获取用户
            if (userDataSource == null) {
                callback.onError("系统初始化错误");
                return;
            }

            User user = userDataSource.getUserByUsername(username);

            if (user == null) {
                callback.onError("用户名不存在");
                return;
            }

            if (!user.getPassword().equals(password)) {
                callback.onError("密码错误");
                return;
            }

            // 登录成功
            User responseUser = new User(user.getUsername(), "token_" + System.currentTimeMillis());
            responseUser.setDisplayName(user.getDisplayName());
            responseUser.setRole(user.getRole());
            responseUser.setEmail(user.getEmail());
            responseUser.setPhone(user.getPhone());

            String welcomeMessage = "admin".equals(user.getRole()) ?
                    "欢迎管理员 " + user.getDisplayName() + "！您拥有所有管理权限。" :
                    "登录成功！欢迎回来，" + user.getDisplayName();

            LoginResponse response = new LoginResponse(
                    true,
                    welcomeMessage,
                    "token_" + System.currentTimeMillis() + "_" + username,
                    responseUser
            );
            callback.onSuccess(response);

        }, NETWORK_DELAY_MS);
    }
}