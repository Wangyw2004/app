package com.example.no1.data.remote;
import android.os.Handler;
import android.os.Looper;
import com.example.no1.features.auth.models.LoginRequest;
import com.example.no1.features.auth.models.LoginResponse;
import com.example.no1.features.auth.models.User;

public class ApiService {

    // 模拟网络请求延迟
    private static final int NETWORK_DELAY_MS = 1500;

    public interface LoginCallback {
        void onSuccess(LoginResponse response);
        void onError(String errorMessage);
    }

    // 模拟登录API调用
    public static void login(LoginRequest request, LoginCallback callback) {
        // 模拟网络请求
        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            // 模拟验证逻辑
            String username = request.getUsername();
            String password = request.getPassword();

            // 模拟成功登录的账号：admin/123456 或 test/123456
            if (("admin".equals(username) && "123456".equals(password)) ||
                    ("test".equals(username) && "123456".equals(password))) {

                User user = new User(username, "user_token_" + System.currentTimeMillis());
                LoginResponse response = new LoginResponse(
                        true,
                        "登录成功！欢迎回来，" + username,
                        "token_" + System.currentTimeMillis(),
                        user
                );
                callback.onSuccess(response);

            } else if ("locked".equals(username)) {
                // 模拟账号被锁定
                callback.onError("账号已被锁定，请联系管理员");

            } else {
                // 登录失败
                callback.onError("用户名或密码错误");
            }

        }, NETWORK_DELAY_MS);
    }
}