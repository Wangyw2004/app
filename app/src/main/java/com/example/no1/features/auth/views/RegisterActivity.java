package com.example.no1.features.auth.views;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.no1.R;
import com.example.no1.data.repository.UserRepository;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private UserRepository userRepository;

    private TextInputEditText editUsername;
    private TextInputEditText editNickname;
    private TextInputEditText editPassword;
    private TextInputEditText editConfirmPassword;
    private TextInputEditText editEmail;
    private TextInputEditText editPhone;

    private TextInputLayout usernameLayout;
    private TextInputLayout nicknameLayout;
    private TextInputLayout passwordLayout;
    private TextInputLayout confirmPasswordLayout;
    private TextInputLayout emailLayout;
    private TextInputLayout phoneLayout;

    private Button btnRegister;
    private ProgressBar progressBar;
    private TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userRepository = UserRepository.getInstance(this);

        // 重置注册状态，避免上次成功状态影响
        userRepository.resetRegisterState();

        initViews();
        setupToolbar();
        setupListeners();
        setupObservers();
    }

    private void initViews() {
        editUsername = findViewById(R.id.editUsername);
        editNickname = findViewById(R.id.editNickname);
        editPassword = findViewById(R.id.editPassword);
        editConfirmPassword = findViewById(R.id.editConfirmPassword);
        editEmail = findViewById(R.id.editEmail);
        editPhone = findViewById(R.id.editPhone);

        usernameLayout = findViewById(R.id.usernameLayout);
        nicknameLayout = findViewById(R.id.nicknameLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout);
        emailLayout = findViewById(R.id.emailLayout);
        phoneLayout = findViewById(R.id.phoneLayout);

        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);
        tvLogin = findViewById(R.id.tvLogin);
    }

    private void setupToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("注册账号");
        }
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> register());
        tvLogin.setOnClickListener(v -> {
            finish();
        });

        // 实时验证
        editUsername.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateUsername();
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        editNickname.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateNickname();
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        editPassword.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePassword();
                validateConfirmPassword();
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        editConfirmPassword.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateConfirmPassword();
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        editEmail.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateEmail();
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        editPhone.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePhone();
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });
    }

    private void setupObservers() {
        userRepository.getIsRegisterSuccess().observe(this, success -> {
            if (success) {
                progressBar.setVisibility(View.GONE);
                btnRegister.setEnabled(true);
                showSuccessDialog();
                // 重置状态，避免下次进入时自动触发
                userRepository.resetRegisterState();
            }
        });

        userRepository.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                progressBar.setVisibility(View.GONE);
                btnRegister.setEnabled(true);
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateUsername() {
        String username = editUsername.getText().toString().trim();
        if (username.isEmpty()) {
            usernameLayout.setError("请输入用户名");
            return false;
        }
        String pattern = "^[a-zA-Z0-9_]{3,20}$";
        if (!Pattern.matches(pattern, username)) {
            usernameLayout.setError("用户名必须为3-20位，只能包含字母、数字和下划线");
            return false;
        }
        usernameLayout.setError(null);
        return true;
    }

    private boolean validateNickname() {
        String nickname = editNickname.getText().toString().trim();
        if (nickname.isEmpty()) {
            nicknameLayout.setError("请输入昵称");
            return false;
        }
        if (nickname.length() < 2 || nickname.length() > 12) {
            nicknameLayout.setError("昵称长度应为2-12位");
            return false;
        }
        nicknameLayout.setError(null);
        return true;
    }

    private boolean validatePassword() {
        String password = editPassword.getText().toString().trim();
        if (password.isEmpty()) {
            passwordLayout.setError("请输入密码");
            return false;
        }
        if (password.length() < 6 || password.length() > 20) {
            passwordLayout.setError("密码长度应为6-20位");
            return false;
        }
        boolean hasLetter = false;
        boolean hasDigit = false;
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) hasLetter = true;
            if (Character.isDigit(c)) hasDigit = true;
        }
        if (!hasLetter || !hasDigit) {
            passwordLayout.setError("密码必须同时包含字母和数字");
            return false;
        }
        passwordLayout.setError(null);
        return true;
    }

    private boolean validateConfirmPassword() {
        String password = editPassword.getText().toString().trim();
        String confirmPassword = editConfirmPassword.getText().toString().trim();
        if (confirmPassword.isEmpty()) {
            confirmPasswordLayout.setError("请确认密码");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordLayout.setError("两次输入的密码不一致");
            return false;
        }
        confirmPasswordLayout.setError(null);
        return true;
    }

    private boolean validateEmail() {
        String email = editEmail.getText().toString().trim();
        if (email.isEmpty()) {
            emailLayout.setError(null);
            return true;
        }
        String pattern = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (!Pattern.matches(pattern, email)) {
            emailLayout.setError("邮箱格式不正确");
            return false;
        }
        emailLayout.setError(null);
        return true;
    }

    private boolean validatePhone() {
        String phone = editPhone.getText().toString().trim();
        if (phone.isEmpty()) {
            phoneLayout.setError(null);
            return true;
        }
        String pattern = "^1[3-9]\\d{9}$";
        if (!Pattern.matches(pattern, phone)) {
            phoneLayout.setError("手机号格式不正确");
            return false;
        }
        phoneLayout.setError(null);
        return true;
    }

    private boolean validateAll() {
        return validateUsername() && validateNickname() &&
                validatePassword() && validateConfirmPassword() &&
                validateEmail() && validatePhone();
    }

    private void register() {
        if (!validateAll()) {
            return;
        }

        String username = editUsername.getText().toString().trim();
        String nickname = editNickname.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);

        userRepository.register(username, password, nickname,
                email.isEmpty() ? null : email,
                phone.isEmpty() ? null : phone);

        // 确保注册成功对话框显示时，状态已经同步
        userRepository.getIsRegisterSuccess().observe(this, success -> {
            if (success) {
                progressBar.setVisibility(View.GONE);
                btnRegister.setEnabled(true);
                showSuccessDialog();
                userRepository.resetRegisterState();
            }
        });
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("注册成功")
                .setMessage("欢迎加入！请登录体验完整功能")
                .setPositiveButton("去登录", (dialog, which) -> {
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}