package com.example.no1.features.service.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.no1.R;
import com.example.no1.common.utils.UserSessionManager;
import com.example.no1.features.service.adapters.ServiceRecordAdapter;
import com.example.no1.features.service.models.Service;
import com.example.no1.features.service.viewmodels.ServiceViewModel;
import java.util.List;

public class ServiceRecordActivity extends AppCompatActivity {

    private ServiceViewModel viewModel;
    private UserSessionManager sessionManager;

    private RecyclerView recyclerView;
    private ServiceRecordAdapter adapter;
    private ProgressBar progressBar;
    private TextView emptyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_record);

        sessionManager = UserSessionManager.getInstance(this);

        if (!sessionManager.isLoggedIn()) {
            finish();
            return;
        }

        initViews();
        setupToolbar();
        setupViewModel();
        setupObservers();

        viewModel.loadUserServices(sessionManager.getUsername());
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        emptyText = findViewById(R.id.emptyText);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("我的服务记录");
        }
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(ServiceViewModel.class);
    }

    private void setupObservers() {
        viewModel.getServices().observe(this, services -> {
            if (services == null || services.isEmpty()) {
                emptyText.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                emptyText.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                if (adapter == null) {
                    adapter = new ServiceRecordAdapter(services, service -> {
                        Intent intent = new Intent(this, ServiceDetailActivity.class);
                        intent.putExtra("service_id", service.getId());
                        startActivity(intent);
                    });
                    recyclerView.setAdapter(adapter);
                } else {
                    adapter.updateServices(services);
                }
            }
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}