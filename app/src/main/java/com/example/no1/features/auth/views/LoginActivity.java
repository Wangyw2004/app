package com.example.no1.features.auth.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import com.example.no1.R;
import com.example.no1.common.utils.UserSessionManager;
import com.example.no1.features.auth.models.LoginResponse;
import com.example.no1.features.auth.models.User;
import com.example.no1.features.auth.viewmodels.LoginViewModel;
import com.example.no1.main.views.MainActivity;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel viewModel;
    private UserSessionManager sessionManager;

    private EditText usernameInput;
    private EditText passwordInput;
    private TextInputLayout usernameLayout;
    private TextInputLayout passwordLayout;
    private Button loginButton;
    private Button skipButton;
    private ProgressBar progressBar;
    private TextView messageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = UserSessionManager.getInstance(this);

        if (sessionManager.isLoggedIn()) {
            navigateToMain();
            return;
        }

        initViews();
        setupViewModel();
        setupObservers();
        setupListeners();
    }

    private void initViews() {
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        skipButton = findViewById(R.id.skipButton);
        progressBar = findViewById(R.id.progressBar);
        messageText = findViewById(R.id.messageText);

        usernameLayout = findViewById(R.id.usernameLayout);
        passwordLayout = findViewById(R.id.passwordLayout);

        loginButton.setEnabled(false);
        loginButton.setAlpha(0.6f);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
    }

    private void setupObservers() {
        viewModel.getUsernameError().observe(this, error -> {
            if (usernameLayout != null) usernameLayout.setError(error);
        });

        viewModel.getPasswordError().observe(this, error -> {
            if (passwordLayout != null) passwordLayout.setError(error);
        });

        viewModel.getIsLoginEnabled().observe(this, isEnabled -> {
            loginButton.setEnabled(isEnabled);
            loginButton.setAlpha(isEnabled ? 1.0f : 0.6f);
        });

        // 在 setupObservers() 方法中，登录成功的观察者里
        viewModel.getLoginResult().observe(this, response -> {
            if (response != null && response.isSuccess()) {
                User user = response.getUser();
                sessionManager.saveUserSession(
                        user.getUsername(),
                        user.getDisplayName(),  // 昵称
                        response.getToken(),
                        user.getRole(),
                        user.getEmail() != null ? user.getEmail() : "",
                        "保密",  // 默认性别
                        2000    // 默认出生年份
                );
                showMessage(response.getMessage(), true);
                navigateToMain();
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            progressBar.setVisibility(View.GONE);
            loginButton.setEnabled(true);
            loginButton.setText("登录");
            if (error != null && !error.isEmpty()) {
                showMessage(error, false);
                passwordInput.setText("");
                viewModel.setPassword("");
                passwordInput.requestFocus();
            }
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                progressBar.setVisibility(View.VISIBLE);
                loginButton.setEnabled(false);
                loginButton.setText("登录中...");
                loginButton.setAlpha(0.6f);
                skipButton.setEnabled(false);
                skipButton.setAlpha(0.6f);
            } else {
                progressBar.setVisibility(View.GONE);
                loginButton.setEnabled(viewModel.getIsLoginEnabled().getValue() != null &&
                        viewModel.getIsLoginEnabled().getValue());
                loginButton.setText("登录");
                loginButton.setAlpha(loginButton.isEnabled() ? 1.0f : 0.6f);
                skipButton.setEnabled(true);
                skipButton.setAlpha(1.0f);
            }
        });
    }

    private void setupListeners() {
        usernameInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setUsername(s.toString());
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        passwordInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setPassword(s.toString());
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        passwordInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE && loginButton.isEnabled()) {
                viewModel.login();
                return true;
            }
            return false;
        });

        loginButton.setOnClickListener(v -> viewModel.login());

        skipButton.setOnClickListener(v -> {
            sessionManager.saveGuestSession();
            navigateToMain();
        });
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void showMessage(String message, boolean isSuccess) {
        messageText.setText(message);
        messageText.setVisibility(View.VISIBLE);

        if (isSuccess) {
            messageText.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
        } else {
            messageText.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        }

        messageText.postDelayed(() -> {
            if (messageText != null) {
                messageText.setVisibility(View.GONE);
            }
        }, 3000);
    }
}