package com.example.no1.features.notice.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.no1.R;
import com.example.no1.common.utils.UserSessionManager;
import com.example.no1.features.notice.adapters.NoticeAdapter;
import com.example.no1.features.notice.models.Notice;
import com.example.no1.features.notice.viewmodels.NoticeViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class NoticeFragment extends Fragment {

    private NoticeViewModel viewModel;
    private UserSessionManager sessionManager;

    private RecyclerView recyclerView;
    private NoticeAdapter adapter;
    private ProgressBar progressBar;
    private TextView emptyText;
    private FloatingActionButton fabPublish;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notice, container, false);

        sessionManager = UserSessionManager.getInstance(requireContext());

        initViews(view);
        setupViewModel();
        setupObservers();
        setupListeners();

        viewModel.loadNotices();

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        emptyText = view.findViewById(R.id.emptyText);
        fabPublish = view.findViewById(R.id.fabPublish);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 只有管理员才能看到发布按钮
        if (sessionManager.isAdmin()) {
            fabPublish.setVisibility(View.VISIBLE);
        } else {
            fabPublish.setVisibility(View.GONE);
        }
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(NoticeViewModel.class);
    }

    private void setupObservers() {
        viewModel.getNotices().observe(getViewLifecycleOwner(), notices -> {
            if (notices == null || notices.isEmpty()) {
                emptyText.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                emptyText.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                boolean isAdmin = sessionManager.isAdmin();

                if (adapter == null) {
                    adapter = new NoticeAdapter(notices, isAdmin,
                            notice -> {
                                // 查看详情
                                Intent intent = new Intent(getActivity(), NoticeDetailActivity.class);
                                intent.putExtra("notice_id", notice.getId());
                                startActivity(intent);
                            },
                            notice -> {
                                // 编辑通知（仅管理员）
                                Intent intent = new Intent(getActivity(), PublishNoticeActivity.class);
                                intent.putExtra("notice_id", notice.getId());
                                intent.putExtra("title", notice.getTitle());
                                intent.putExtra("content", notice.getContent());
                                startActivity(intent);
                            },
                            notice -> {
                                // 删除通知（仅管理员）
                                showDeleteConfirmDialog(notice);
                            }
                    );
                    recyclerView.setAdapter(adapter);
                } else {
                    adapter.updateNotices(notices);
                }
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners() {
        fabPublish.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PublishNoticeActivity.class);
            startActivity(intent);
        });
    }

    private void showDeleteConfirmDialog(Notice notice) {
        new AlertDialog.Builder(requireContext())
                .setTitle("确认删除")
                .setMessage("确定要删除《" + notice.getTitle() + "》吗？")
                .setPositiveButton("删除", (dialog, which) -> {
                    viewModel.deleteNotice(notice.getId());
                    Toast.makeText(getContext(), "通知已删除", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null)
                .show();
    }
}