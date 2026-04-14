package com.example.no1.features.service.views;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import com.example.no1.R;
import com.example.no1.common.utils.UserSessionManager;
import com.example.no1.features.service.models.Service;
import com.example.no1.features.service.repository.ServiceRepository;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AdminServiceDetailActivity extends AppCompatActivity {

    private ServiceRepository repository;
    private UserSessionManager sessionManager;

    private TextView tvStatus;
    private TextView tvType;
    private TextView tvOrderId;
    private TextView tvUserName;
    private TextView tvTitle;
    private TextView tvCategory;
    private TextView tvDescription;
    private TextView tvPhone;
    private LinearLayout repairExtraLayout;
    private TextView tvUrgency;
    private TextView tvExpectedTime;
    private TextView tvRemark;
    private LinearLayout progressContainer;
    private Button btnUpdateStatus;
    private Button btnAddProgress;

    private String serviceId;
    private Service service;

    private String[] statusOptions = {"待受理", "处理中", "已完成"};
    private String[] statusValues = {"pending", "processing", "completed"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_service_detail);

        sessionManager = UserSessionManager.getInstance(this);
        repository = ServiceRepository.getInstance(this);

        if (!sessionManager.isAdmin()) {
            finish();
            return;
        }

        serviceId = getIntent().getStringExtra("service_id");
        if (serviceId == null) {
            finish();
            return;
        }

        initViews();
        setupToolbar();
        loadData();
        setupListeners();
    }

    private void initViews() {
        tvStatus = findViewById(R.id.tvStatus);
        tvType = findViewById(R.id.tvType);
        tvOrderId = findViewById(R.id.tvOrderId);
        tvUserName = findViewById(R.id.tvUserName);
        tvTitle = findViewById(R.id.tvTitle);
        tvCategory = findViewById(R.id.tvCategory);
        tvDescription = findViewById(R.id.tvDescription);
        tvPhone = findViewById(R.id.tvPhone);
        repairExtraLayout = findViewById(R.id.repairExtraLayout);
        tvUrgency = findViewById(R.id.tvUrgency);
        tvExpectedTime = findViewById(R.id.tvExpectedTime);
        tvRemark = findViewById(R.id.tvRemark);
        progressContainer = findViewById(R.id.progressContainer);
        btnUpdateStatus = findViewById(R.id.btnUpdateStatus);
        btnAddProgress = findViewById(R.id.btnAddProgress);
    }

    private void setupToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("工单详情");
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

        // 类型
        if ("complaint".equals(service.getType())) {
            tvType.setText("投诉");
            tvType.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_orange_dark));
        } else {
            tvType.setText("报修");
            tvType.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark));
        }
        tvType.setPadding(16, 4, 16, 4);

        // 单号
        tvOrderId.setText("单号：" + service.getId());

        // 提交人
        tvUserName.setText("提交人：" + service.getUserName() + " (" + service.getContactPhone() + ")");

        // 标题
        tvTitle.setText(service.getTitle());

        // 类型
        tvCategory.setText("类型：" + service.getCategory());

        // 描述
        tvDescription.setText(service.getDescription());

        // 电话
        tvPhone.setText(service.getContactPhone());

        // 报修特有
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
        progressContainer.removeAllViews();
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

    private void setupListeners() {
        btnUpdateStatus.setOnClickListener(v -> showUpdateStatusDialog());
        btnAddProgress.setOnClickListener(v -> showAddProgressDialog());
    }

    private void showUpdateStatusDialog() {
        int currentIndex = 0;
        for (int i = 0; i < statusValues.length; i++) {
            if (statusValues[i].equals(service.getStatus())) {
                currentIndex = i;
                break;
            }
        }

        new AlertDialog.Builder(this)
                .setTitle("更新状态")
                .setSingleChoiceItems(statusOptions, currentIndex, (dialog, which) -> {
                    String newStatus = statusValues[which];
                    if (!newStatus.equals(service.getStatus())) {
                        updateStatus(newStatus);
                    }
                    dialog.dismiss();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void updateStatus(String newStatus) {
        String statusText;
        switch (newStatus) {
            case "processing": statusText = "处理中"; break;
            case "completed": statusText = "已完成"; break;
            default: statusText = "待受理";
        }

        String progressContent = "状态已更新为：" + statusText;
        repository.updateServiceStatus(serviceId, newStatus, progressContent, sessionManager.getDisplayName());

        Toast.makeText(this, "状态已更新", Toast.LENGTH_SHORT).show();
        loadData(); // 刷新
    }

    private void showAddProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("添加处理记录");

        View view = getLayoutInflater().inflate(R.layout.dialog_add_progress, null);
        TextInputEditText editContent = view.findViewById(R.id.editProgressContent);
        builder.setView(view);
        builder.setPositiveButton("添加", (dialog, which) -> {
            String content = editContent.getText().toString().trim();
            if (content.isEmpty()) {
                Toast.makeText(this, "请输入处理内容", Toast.LENGTH_SHORT).show();
                return;
            }
            repository.updateServiceStatus(serviceId, service.getStatus(), content, sessionManager.getDisplayName());
            Toast.makeText(this, "处理记录已添加", Toast.LENGTH_SHORT).show();
            loadData();
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}