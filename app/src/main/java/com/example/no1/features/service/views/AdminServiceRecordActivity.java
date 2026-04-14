package com.example.no1.features.service.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.no1.R;
import com.example.no1.common.utils.UserSessionManager;
import com.example.no1.features.service.adapters.AdminServiceRecordAdapter;
import com.example.no1.features.service.models.Service;
import com.example.no1.features.service.viewmodels.ServiceViewModel;
import java.util.ArrayList;
import java.util.List;

public class AdminServiceRecordActivity extends AppCompatActivity {

    private ServiceViewModel viewModel;
    private UserSessionManager sessionManager;

    private RecyclerView recyclerView;
    private AdminServiceRecordAdapter adapter;
    private ProgressBar progressBar;
    private TextView emptyText;
    private Spinner spinnerStatus;

    private List<Service> allServices = new ArrayList<>();
    private String selectedStatus = "all";
    private String[] statusOptions = {"全部", "待受理", "处理中", "已完成"};
    private String[] statusValues = {"all", "pending", "processing", "completed"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_service_record);

        sessionManager = UserSessionManager.getInstance(this);

        if (!sessionManager.isAdmin()) {
            finish();
            return;
        }

        initViews();
        setupToolbar();
        setupSpinner();
        setupViewModel();
        setupObservers();

        viewModel.loadAllServices();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        emptyText = findViewById(R.id.emptyText);
        spinnerStatus = findViewById(R.id.spinnerStatus);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("全部工单");
        }
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statusOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);
        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedStatus = statusValues[position];
                filterServices();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(ServiceViewModel.class);
    }

    private void setupObservers() {
        viewModel.getServices().observe(this, services -> {
            if (services != null) {
                allServices = services;
                filterServices();
            }
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
    }

    private void filterServices() {
        List<Service> filtered = new ArrayList<>();

        if ("all".equals(selectedStatus)) {
            filtered = allServices;
        } else {
            for (Service service : allServices) {
                if (service.getStatus().equals(selectedStatus)) {
                    filtered.add(service);
                }
            }
        }

        if (filtered.isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            if (adapter == null) {
                adapter = new AdminServiceRecordAdapter(filtered, service -> {
                    Intent intent = new Intent(this, AdminServiceDetailActivity.class);
                    intent.putExtra("service_id", service.getId());
                    startActivity(intent);
                });
                recyclerView.setAdapter(adapter);
            } else {
                adapter.updateServices(filtered);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}