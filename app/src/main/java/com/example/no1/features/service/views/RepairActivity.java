package com.example.no1.features.service.views;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.example.no1.R;
import com.example.no1.common.utils.UserSessionManager;
import com.example.no1.features.service.models.Service;
import com.example.no1.features.service.viewmodels.ServiceViewModel;
import com.google.android.material.textfield.TextInputEditText;

public class RepairActivity extends AppCompatActivity {

    private ServiceViewModel viewModel;
    private UserSessionManager sessionManager;

    private Spinner spinnerCategory;
    private RadioGroup radioUrgency;
    private TextInputEditText editTitle;
    private TextInputEditText editDescription;
    private TextInputEditText editExpectedTime;
    private TextInputEditText editPhone;
    private TextInputEditText editRemark;
    private Button btnSubmit;
    private ProgressBar progressBar;

    private String[] categories = {"水电故障", "家电维修", "管道疏通", "墙地面裂", "门锁问题", "其他"};
    private String selectedCategory = "水电故障";
    private String selectedUrgency = "normal";

    private Observer<String> errorObserver;
    private boolean isSubmitting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair);

        sessionManager = UserSessionManager.getInstance(this);

        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupToolbar();
        setupSpinner();
        setupRadioGroup();
        setupViewModel();
        setupListeners();
    }

    private void initViews() {
        spinnerCategory = findViewById(R.id.spinnerCategory);
        radioUrgency = findViewById(R.id.radioUrgency);
        editTitle = findViewById(R.id.editTitle);
        editDescription = findViewById(R.id.editDescription);
        editExpectedTime = findViewById(R.id.editExpectedTime);
        editPhone = findViewById(R.id.editPhone);
        editRemark = findViewById(R.id.editRemark);
        btnSubmit = findViewById(R.id.btnSubmit);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("物业报修");
        }
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = categories[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupRadioGroup() {
        radioUrgency.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioNormal) {
                selectedUrgency = "normal";
            } else if (checkedId == R.id.radioUrgent) {
                selectedUrgency = "urgent";
            } else if (checkedId == R.id.radioVeryUrgent) {
                selectedUrgency = "very_urgent";
            }
        });
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(ServiceViewModel.class);

        // 重置错误消息，避免上次的状态影响
        viewModel.resetErrorMessage();

        errorObserver = error -> {
            if (!isSubmitting) return; // 只有在提交状态时才处理

            progressBar.setVisibility(View.GONE);
            btnSubmit.setEnabled(true);
            isSubmitting = false;

            if (error != null && !error.isEmpty()) {
                // 有错误信息，显示错误
                Toast.makeText(RepairActivity.this, error, Toast.LENGTH_SHORT).show();
            } else {
                // 成功
                Toast.makeText(RepairActivity.this, "报修提交成功", Toast.LENGTH_SHORT).show();
                finish();
            }
        };

        viewModel.getErrorMessage().observe(this, errorObserver);
    }

    private void setupListeners() {
        btnSubmit.setOnClickListener(v -> submitRepair());
    }

    private void submitRepair() {
        String title = editTitle.getText().toString().trim();
        String description = editDescription.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();

        if (title.isEmpty()) {
            editTitle.setError("请填写标题");
            return;
        }
        if (description.isEmpty()) {
            editDescription.setError("请填写详细描述");
            return;
        }
        if (phone.isEmpty()) {
            editPhone.setError("请填写联系电话");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSubmit.setEnabled(false);
        isSubmitting = true;

        Service service = new Service();
        service.setCategory(selectedCategory);
        service.setTitle(title);
        service.setDescription(description);
        service.setContactPhone(phone);
        service.setUrgency(selectedUrgency);
        service.setExpectedTime(editExpectedTime.getText().toString().trim());
        service.setRemark(editRemark.getText().toString().trim());
        service.setUserId(sessionManager.getUsername());
        service.setUserName(sessionManager.getDisplayName());

        viewModel.submitRepair(service);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (viewModel != null && errorObserver != null) {
            viewModel.getErrorMessage().removeObserver(errorObserver);
        }
    }
}