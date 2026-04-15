package com.example.no1.data.repository;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import com.example.no1.data.local.UserDataSource;
import com.example.no1.features.auth.models.User;
import java.util.UUID;

public class UserRepository {
    private static UserRepository instance;
    private UserDataSource dataSource;
    private MutableLiveData<Boolean> isRegisterSuccess;
    private MutableLiveData<String> errorMessage;

    private UserRepository(Context context) {
        dataSource = UserDataSource.getInstance(context);
        isRegisterSuccess = new MutableLiveData<>(false);
        errorMessage = new MutableLiveData<>();
    }

    public static synchronized UserRepository getInstance(Context context) {
        if (instance == null) {
            instance = new UserRepository(context);
        }
        return instance;
    }

    public MutableLiveData<Boolean> getIsRegisterSuccess() {
        return isRegisterSuccess;
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void register(String username, String password, String nickname,
                         String email, String phone) {
        // 验证用户名是否存在
        if (dataSource.isUsernameExists(username)) {
            errorMessage.setValue("用户名已存在");
            return;
        }

        // 创建新用户
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setDisplayName(nickname);
        newUser.setEmail(email != null ? email : "");
        newUser.setPhone(phone != null ? phone : "");
        newUser.setRole("user");
        newUser.setToken("token_" + UUID.randomUUID().toString());

        dataSource.addUser(newUser);
        isRegisterSuccess.setValue(true);
        errorMessage.setValue(null);
    }

    public void resetRegisterState() {
        isRegisterSuccess.setValue(false);
        errorMessage.setValue(null);
    }
}