package com.example.no1.features.post.views;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.no1.R;
import com.example.no1.common.utils.UserSessionManager;
import com.example.no1.features.post.viewmodels.CreatePostViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class CreatePostActivity extends AppCompatActivity {

    private CreatePostViewModel viewModel;
    private UserSessionManager sessionManager;

    private TextInputEditText titleInput;
    private TextInputEditText contentInput;
    private TextInputLayout titleLayout;
    private TextInputLayout contentLayout;
    private Button submitButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        sessionManager = UserSessionManager.getInstance(this);

        initViews();
        setupViewModel();
        setupObservers();
        setupListeners();
    }

    private void initViews() {
        titleInput = findViewById(R.id.titleInput);
        contentInput = findViewById(R.id.contentInput);
        titleLayout = findViewById(R.id.titleLayout);
        contentLayout = findViewById(R.id.contentLayout);
        submitButton = findViewById(R.id.submitButton);
        progressBar = findViewById(R.id.progressBar);

        // 设置工具栏
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("发布新帖");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(CreatePostViewModel.class);
    }

    private void setupObservers() {
        viewModel.getTitleError().observe(this, error -> {
            if (titleLayout != null) titleLayout.setError(error);
        });

        viewModel.getContentError().observe(this, error -> {
            if (contentLayout != null) contentLayout.setError(error);
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            submitButton.setEnabled(!isLoading);
            submitButton.setText(isLoading ? "发布中..." : "发布");
        });

        viewModel.getIsCreated().observe(this, isCreated -> {
            if (isCreated) {
                Toast.makeText(this, "发布成功！", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners() {
        titleInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setTitle(s.toString());
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        contentInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setContent(s.toString());
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        submitButton.setOnClickListener(v -> {
            String authorId = sessionManager.getUsername();
            String authorName = getIntent().getStringExtra("username");
            if (authorName == null) authorName = sessionManager.getDisplayName();

            viewModel.createPost(authorId, authorName);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}