package com.example.no1.features.auth.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.no1.R;
import com.example.no1.common.utils.UserSessionManager;
import com.example.no1.features.auth.viewmodels.LoginViewModel;
import com.example.no1.main.views.MainActivity;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel viewModel;
    private UserSessionManager sessionManager;

    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginButton;
    private Button skipButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = UserSessionManager.getInstance(this);

        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
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
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
    }

    private void setupObservers() {
        // 关键：观察登录按钮状态
        viewModel.getIsLoginEnabled().observe(this, isEnabled -> {
            loginButton.setEnabled(isEnabled);
            loginButton.setAlpha(isEnabled ? 1.0f : 0.6f);
        });

        viewModel.getLoginResult().observe(this, response -> {
            progressBar.setVisibility(View.GONE);
            loginButton.setEnabled(true);
            loginButton.setText("登录");

            if (response != null && response.isSuccess()) {
                sessionManager.saveUserSession(
                        response.getUser().getUsername(),
                        response.getUser().getDisplayName(),
                        response.getToken()
                );
                Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            progressBar.setVisibility(View.GONE);
            loginButton.setEnabled(true);
            loginButton.setText("登录");
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                progressBar.setVisibility(View.VISIBLE);
                loginButton.setEnabled(false);
                loginButton.setText("登录中...");
            } else {
                progressBar.setVisibility(View.GONE);
                loginButton.setEnabled(true);
                loginButton.setText("登录");
            }
        });
    }

    private void setupListeners() {
        // 用户名输入时更新ViewModel
        usernameInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setUsername(s.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        // 密码输入时更新ViewModel
        passwordInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setPassword(s.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        loginButton.setOnClickListener(v -> {
            viewModel.login();
        });

        skipButton.setOnClickListener(v -> {
            sessionManager.saveGuestSession();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }
}