package com.example.no1.features.notice.views;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.no1.R;
import com.example.no1.features.notice.models.Notice;
import com.example.no1.features.notice.repository.NoticeRepository;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class NoticeDetailActivity extends AppCompatActivity {

    private NoticeRepository repository;

    private TextView tvTitle;
    private TextView tvAuthor;
    private TextView tvTime;
    private TextView tvContent;
    private TextView tvViewCount;

    private String noticeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_detail);

        repository = NoticeRepository.getInstance(this);

        noticeId = getIntent().getStringExtra("notice_id");
        if (noticeId == null) {
            finish();
            return;
        }

        initViews();
        setupToolbar();
        loadNotice();
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tvTitle);
        tvAuthor = findViewById(R.id.tvAuthor);
        tvTime = findViewById(R.id.tvTime);
        tvContent = findViewById(R.id.tvContent);
        tvViewCount = findViewById(R.id.tvViewCount);
    }

    private void setupToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("通知详情");
        }
    }

    private void loadNotice() {
        Notice notice = repository.getNoticeById(noticeId);
        if (notice == null) {
            finish();
            return;
        }

        // 增加阅读次数
        repository.incrementViewCount(noticeId);

        displayNotice(notice);
    }

    private void displayNotice(Notice notice) {
        tvTitle.setText(notice.getTitle());
        tvAuthor.setText("发布人：" + notice.getAuthor());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        tvTime.setText("时间：" + sdf.format(notice.getCreateTime()));

        tvContent.setText(notice.getContent());
        tvViewCount.setText("阅读次数：" + notice.getViewCount());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}