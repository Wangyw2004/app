package com.example.no1.features.service.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.example.no1.R;
import com.example.no1.common.utils.UserSessionManager;

public class ServiceFragment extends Fragment {

    private UserSessionManager sessionManager;
    private CardView cardComplaint;
    private CardView cardRepair;
    private CardView cardMyRecords;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service, container, false);

        sessionManager = UserSessionManager.getInstance(requireContext());

        initViews(view);
        setupListeners();

        return view;
    }

    private void initViews(View view) {
        cardComplaint = view.findViewById(R.id.cardComplaint);
        cardRepair = view.findViewById(R.id.cardRepair);
        cardMyRecords = view.findViewById(R.id.cardMyRecords);
    }

    private void setupListeners() {
        cardComplaint.setOnClickListener(v -> {
            if (sessionManager.isLoggedIn()) {
                Intent intent = new Intent(getActivity(), ComplaintActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "请先登录", Toast.LENGTH_SHORT).show();
            }
        });

        cardRepair.setOnClickListener(v -> {
            if (sessionManager.isLoggedIn()) {
                Intent intent = new Intent(getActivity(), RepairActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "请先登录", Toast.LENGTH_SHORT).show();
            }
        });

        cardMyRecords.setOnClickListener(v -> {
            if (sessionManager.isLoggedIn()) {
                Intent intent = new Intent(getActivity(), ServiceRecordActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "请先登录", Toast.LENGTH_SHORT).show();
            }
        });
    }
}