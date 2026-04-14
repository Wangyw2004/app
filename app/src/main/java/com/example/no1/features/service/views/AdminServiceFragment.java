package com.example.no1.features.service.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.no1.R;
import com.example.no1.common.utils.UserSessionManager;
import com.example.no1.features.service.models.Service;
import com.example.no1.features.service.viewmodels.ServiceViewModel;
import java.util.List;

public class AdminServiceFragment extends Fragment {

    private ServiceViewModel viewModel;
    private UserSessionManager sessionManager;

    private TextView tvPendingCount;
    private TextView tvProcessingCount;
    private TextView tvCompletedCount;
    private CardView cardAllOrders;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_service, container, false);

        sessionManager = UserSessionManager.getInstance(requireContext());

        if (!sessionManager.isAdmin()) {
            requireActivity().finish();
            return view;
        }

        initViews(view);
        setupViewModel();
        setupListeners();

        viewModel.loadAllServices();

        return view;
    }

    private void initViews(View view) {
        tvPendingCount = view.findViewById(R.id.tvPendingCount);
        tvProcessingCount = view.findViewById(R.id.tvProcessingCount);
        tvCompletedCount = view.findViewById(R.id.tvCompletedCount);
        cardAllOrders = view.findViewById(R.id.cardAllOrders);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(ServiceViewModel.class);

        viewModel.getServices().observe(getViewLifecycleOwner(), services -> {
            if (services != null) {
                updateStatistics(services);
            }
        });
    }

    private void updateStatistics(List<Service> services) {
        int pending = 0;
        int processing = 0;
        int completed = 0;

        for (Service service : services) {
            switch (service.getStatus()) {
                case "pending":
                    pending++;
                    break;
                case "processing":
                    processing++;
                    break;
                case "completed":
                    completed++;
                    break;
            }
        }

        tvPendingCount.setText(String.valueOf(pending));
        tvProcessingCount.setText(String.valueOf(processing));
        tvCompletedCount.setText(String.valueOf(completed));
    }

    private void setupListeners() {
        cardAllOrders.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AdminServiceRecordActivity.class);
            startActivity(intent);
        });
    }
}