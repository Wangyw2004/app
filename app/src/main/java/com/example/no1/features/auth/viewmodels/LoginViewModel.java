package com.example.no1.features.auth.viewmodels;
import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.no1.features.auth.repository.AuthRepository;
import com.example.no1.features.auth.models.LoginResponse;
import com.example.no1.common.utils.ValidationUtils;

public class LoginViewModel extends AndroidViewModel {

    private AuthRepository repository;

    // 输入字段的LiveData
    private MutableLiveData<String> username = new MutableLiveData<>("");
    private MutableLiveData<String> password = new MutableLiveData<>("");
    // 登录按钮是否可用的LiveData
    private MutableLiveData<Boolean> isLoginEnabled = new MutableLiveData<>(false);

    // 验证错误的LiveData
    private MutableLiveData<String> usernameError = new MutableLiveData<>();
    private MutableLiveData<String> passwordError = new MutableLiveData<>();

    public LoginViewModel(Application application) {
        super(application);
        repository = AuthRepository.getInstance();
    }

    // Getters for LiveData
    public LiveData<String> getUsername() { return username; }
    public LiveData<String> getPassword() { return password; }
    public LiveData<String> getUsernameError() { return usernameError; }
    public LiveData<String> getPasswordError() { return passwordError; }
    public LiveData<LoginResponse> getLoginResult() { return repository.getLoginResult(); }
    public LiveData<String> getErrorMessage() { return repository.getErrorMessage(); }
    public LiveData<Boolean> getIsLoading() { return repository.getIsLoading(); }
    public LiveData<Boolean> getIsLoginEnabled() {
        return isLoginEnabled;
    }
    // 设置用户名
    public void setUsername(String user) {
        username.setValue(user);
        // 实时验证用户名
        if (user != null && !user.trim().isEmpty()) {
            if (!ValidationUtils.isValidUsername(user)) {
                usernameError.setValue("用户名必须为3-20个字符，只能包含字母、数字和下划线");
            } else {
                usernameError.setValue(null);
            }
        } else {
            usernameError.setValue(null);
        }
        validateLoginEnabled();
    }

    // 设置密码
    public void setPassword(String pwd) {
        password.setValue(pwd);
        // 实时验证密码
        if (pwd != null && !pwd.trim().isEmpty()) {
            if (!ValidationUtils.isValidPassword(pwd)) {
                passwordError.setValue("密码长度至少为6个字符");
            } else {
                passwordError.setValue(null);
            }
        } else {
            passwordError.setValue(null);
        }
        validateLoginEnabled();
    }

    // 执行登录
    public void login() {
        String currentUsername = username.getValue();
        String currentPassword = password.getValue();

        // 最终验证
        boolean isValid = true;

        if (currentUsername == null || currentUsername.trim().isEmpty()) {
            usernameError.setValue("请输入用户名");
            isValid = false;
        } else if (!ValidationUtils.isValidUsername(currentUsername)) {
            usernameError.setValue("用户名必须为3-20个字符，只能包含字母、数字和下划线");
            isValid = false;
        }

        if (currentPassword == null || currentPassword.trim().isEmpty()) {
            passwordError.setValue("请输入密码");
            isValid = false;
        } else if (!ValidationUtils.isValidPassword(currentPassword)) {
            passwordError.setValue("密码长度至少为6个字符");
            isValid = false;
        }

        if (isValid) {
            repository.performLogin(currentUsername, currentPassword);
        }
    }
    // 验证登录按钮是否可用 - 核心逻辑
    private void validateLoginEnabled() {
        String currentUsername = username.getValue();
        String currentPassword = password.getValue();

        boolean enabled = false;

        // 检查用户名和密码都不为空，且没有错误
        if (currentUsername != null && !currentUsername.trim().isEmpty() &&
                currentPassword != null && !currentPassword.trim().isEmpty() &&
                usernameError.getValue() == null &&
                passwordError.getValue() == null) {
            enabled = true;
        }

        isLoginEnabled.setValue(enabled);  // ← 设置按钮状态
    }
    // 清除登录结果（用于重新登录）
    public void clearLoginResult() {
        repository.clearData();
    }

}