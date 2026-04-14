package com.example.no1.features.service.views;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.no1.R;
import com.example.no1.common.utils.UserSessionManager;
import com.example.no1.features.service.models.Service;
import com.example.no1.features.service.repository.ServiceRepository;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ServiceDetailActivity extends AppCompatActivity {

    private ServiceRepository repository;
    private UserSessionManager sessionManager;

    private TextView tvStatus;
    private TextView tvOrderId;
    private TextView tvType;
    private TextView tvTitle;
    private TextView tvDescription;
    private TextView tvPhone;
    private LinearLayout repairExtraLayout;
    private TextView tvUrgency;
    private TextView tvExpectedTime;
    private TextView tvRemark;
    private LinearLayout progressContainer;

    private String serviceId;
    private Service service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_detail);

        sessionManager = UserSessionManager.getInstance(this);
        repository = ServiceRepository.getInstance(this);

        serviceId = getIntent().getStringExtra("service_id");

        if (serviceId == null) {
            finish();
            return;
        }

        initViews();
        setupToolbar();
        loadData();
    }

    private void initViews() {
        tvStatus = findViewById(R.id.tvStatus);
        tvOrderId = findViewById(R.id.tvOrderId);
        tvType = findViewById(R.id.tvType);
        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        tvPhone = findViewById(R.id.tvPhone);
        repairExtraLayout = findViewById(R.id.repairExtraLayout);
        tvUrgency = findViewById(R.id.tvUrgency);
        tvExpectedTime = findViewById(R.id.tvExpectedTime);
        tvRemark = findViewById(R.id.tvRemark);
        progressContainer = findViewById(R.id.progressContainer);
    }

    private void setupToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("服务详情");
        }
    }

    private void loadData() {
        service = repository.getServiceById(serviceId);
        if (service == null) {
            finish();
            return;
        }

        displayService();
    }

    private void displayService() {
        // 状态
        tvStatus.setText(service.getStatusText());
        tvStatus.setBackgroundColor(ContextCompat.getColor(this, service.getStatusColor()));
        tvStatus.setPadding(32, 8, 32, 8);

        // 单号
        tvOrderId.setText("单号：" + service.getId());

        // 类型
        tvType.setText("类型：" + ("complaint".equals(service.getType()) ? "投诉" : "报修") + " / " + service.getCategory());

        // 标题
        tvTitle.setText(service.getTitle());

        // 描述
        tvDescription.setText(service.getDescription());

        // 电话
        tvPhone.setText(service.getContactPhone());

        // 报修特有字段
        if ("repair".equals(service.getType())) {
            repairExtraLayout.setVisibility(View.VISIBLE);

            String urgencyText;
            switch (service.getUrgency()) {
                case "urgent": urgencyText = "紧急"; break;
                case "very_urgent": urgencyText = "非常紧急"; break;
                default: urgencyText = "普通";
            }
            tvUrgency.setText(urgencyText);
            tvExpectedTime.setText(service.getExpectedTime() != null && !service.getExpectedTime().isEmpty()
                    ? service.getExpectedTime() : "未填写");
            tvRemark.setText(service.getRemark() != null && !service.getRemark().isEmpty()
                    ? service.getRemark() : "无");
        }

        // 处理进度
        displayProgress();
    }

    private void displayProgress() {
        List<Service.Progress> progressList = service.getProgressList();
        if (progressList != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            for (Service.Progress progress : progressList) {
                View itemView = getLayoutInflater().inflate(R.layout.item_progress, progressContainer, false);
                TextView tvContent = itemView.findViewById(R.id.tvProgressContent);
                TextView tvTime = itemView.findViewById(R.id.tvProgressTime);
                TextView tvOperator = itemView.findViewById(R.id.tvProgressOperator);

                tvContent.setText(progress.getContent());
                tvTime.setText(sdf.format(progress.getCreateTime()));
                tvOperator.setText("操作人：" + progress.getOperator());

                progressContainer.addView(itemView);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}