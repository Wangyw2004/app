package com.example.no1.features.notice.views;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.no1.R;
import com.example.no1.common.utils.UserSessionManager;
import com.example.no1.features.notice.viewmodels.NoticeViewModel;
import com.google.android.material.textfield.TextInputEditText;

public class PublishNoticeActivity extends AppCompatActivity {

    private NoticeViewModel viewModel;
    private UserSessionManager sessionManager;

    private TextInputEditText editTitle;
    private TextInputEditText editContent;
    private Button btnPublish;
    private ProgressBar progressBar;

    private String noticeId;  // 非空表示编辑模式
    private String originalTitle;
    private String originalContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_notice);

        sessionManager = UserSessionManager.getInstance(this);

        if (!sessionManager.isAdmin()) {
            Toast.makeText(this, "无权限", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 获取编辑数据
        noticeId = getIntent().getStringExtra("notice_id");
        originalTitle = getIntent().getStringExtra("title");
        originalContent = getIntent().getStringExtra("content");

        initViews();
        setupToolbar();
        setupViewModel();
        setupListeners();

        if (noticeId != null) {
            // 编辑模式
            editTitle.setText(originalTitle);
            editContent.setText(originalContent);
            btnPublish.setText("保存修改");
        }
    }

    private void initViews() {
        editTitle = findViewById(R.id.editTitle);
        editContent = findViewById(R.id.editContent);
        btnPublish = findViewById(R.id.btnPublish);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(noticeId != null ? "编辑通知" : "发布通知");
        }
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(NoticeViewModel.class);
    }

    private void setupListeners() {
        btnPublish.setOnClickListener(v -> publishNotice());
    }

    private void publishNotice() {
        String title = editTitle.getText().toString().trim();
        String content = editContent.getText().toString().trim();

        if (title.isEmpty()) {
            editTitle.setError("请填写标题");
            return;
        }
        if (content.isEmpty()) {
            editContent.setError("请填写内容");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnPublish.setEnabled(false);

        if (noticeId != null) {
            // 编辑模式
            viewModel.updateNotice(noticeId, title, content);
        } else {
            // 发布模式
            viewModel.publishNotice(title, content, sessionManager.getUsername(), sessionManager.getDisplayName());
        }

        viewModel.getErrorMessage().observe(this, error -> {
            progressBar.setVisibility(View.GONE);
            btnPublish.setEnabled(true);
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, noticeId != null ? "保存成功" : "发布成功", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}