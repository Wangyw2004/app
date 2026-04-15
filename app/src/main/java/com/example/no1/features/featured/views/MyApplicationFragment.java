package com.example.no1.features.featured.views;

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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.no1.R;
import com.example.no1.common.utils.UserSessionManager;
import com.example.no1.features.featured.models.FeaturedPost;
import com.example.no1.features.featured.viewmodels.FeaturedViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MyApplicationFragment extends Fragment {

    private FeaturedViewModel viewModel;
    private UserSessionManager sessionManager;

    private RecyclerView recyclerView;
    private MyApplicationAdapter adapter;
    private FloatingActionButton fabSubmit;
    private ProgressBar progressBar;
    private TextView emptyText;

    // 将接口定义在 Fragment 类中（非静态）
    public interface OnItemClickListener {
        void onClick(FeaturedPost post);
    }

    public interface OnDeleteClickListener {
        void onDelete(FeaturedPost post);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_application, container, false);

        sessionManager = UserSessionManager.getInstance(requireContext());

        initViews(view);
        setupViewModel();
        setupObservers();
        setupListeners();

        if (sessionManager.isLoggedIn()) {
            viewModel.loadUserApplications(sessionManager.getUsername());
            fabSubmit.setVisibility(View.VISIBLE);
        } else {
            fabSubmit.setVisibility(View.GONE);
        }

        return view;
    }
    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        fabSubmit = view.findViewById(R.id.fabSubmit);
        progressBar = view.findViewById(R.id.progressBar);
        emptyText = view.findViewById(R.id.emptyText);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(FeaturedViewModel.class);
    }

    private void setupObservers() {
        viewModel.getUserApplications().observe(getViewLifecycleOwner(), posts -> {
            if (posts == null || posts.isEmpty()) {
                emptyText.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                emptyText.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                if (adapter == null) {
                    adapter = new MyApplicationAdapter(posts,
                            post -> {
                                if ("pending".equals(post.getStatus()) || "rejected".equals(post.getStatus())) {
                                    Intent intent = new Intent(getActivity(), SubmitFeaturedActivity.class);
                                    intent.putExtra("featured_id", post.getId());
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(getActivity(), FeaturedDetailActivity.class);
                                    intent.putExtra("featured_id", post.getId());
                                    startActivity(intent);
                                }
                            },
                            this::showDeleteConfirmDialog
                    );
                    recyclerView.setAdapter(adapter);
                } else {
                    adapter.updatePosts(posts);
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
        fabSubmit.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SubmitFeaturedActivity.class);
            startActivity(intent);
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null && sessionManager != null && sessionManager.isLoggedIn()) {
            viewModel.loadUserApplications(sessionManager.getUsername());
            // 登录用户显示发布按钮
            if (fabSubmit != null) {
                fabSubmit.setVisibility(View.VISIBLE);
            }
        } else {
            // 游客隐藏发布按钮
            if (fabSubmit != null) {
                fabSubmit.setVisibility(View.GONE);
            }
        }
    }
    private void showDeleteConfirmDialog(FeaturedPost post) {
        new AlertDialog.Builder(requireContext())
                .setTitle("确认删除")
                .setMessage("确定要删除《" + post.getTitle() + "》吗？")
                .setPositiveButton("删除", (dialog, which) -> {
                    viewModel.deleteFeatured(post.getId(), false, sessionManager.getUsername());
                    // 添加延迟刷新
                    new android.os.Handler().postDelayed(() -> {
                        viewModel.loadUserApplications(sessionManager.getUsername());
                    }, 500);
                })
                .setNegativeButton("取消", null)
                .show();
    }
    // 适配器 - 内部类，但接口已移到外部
    class MyApplicationAdapter extends RecyclerView.Adapter<MyApplicationAdapter.ViewHolder> {
        private List<FeaturedPost> posts;
        private OnItemClickListener listener;
        private OnDeleteClickListener deleteListener;

        MyApplicationAdapter(List<FeaturedPost> posts, OnItemClickListener listener, OnDeleteClickListener deleteListener) {
            this.posts = posts;
            this.listener = listener;
            this.deleteListener = deleteListener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_my_application, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            FeaturedPost post = posts.get(position);
            holder.bind(post);
        }

        @Override
        public int getItemCount() {
            return posts == null ? 0 : posts.size();
        }

        void updatePosts(List<FeaturedPost> newPosts) {
            this.posts = newPosts;
            notifyDataSetChanged();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvStatus, tvTime, tvRejectReason, btnEdit, btnDelete;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvStatus = itemView.findViewById(R.id.tvStatus);
                tvTime = itemView.findViewById(R.id.tvTime);
                tvRejectReason = itemView.findViewById(R.id.tvRejectReason);
                btnEdit = itemView.findViewById(R.id.btnEdit);
                btnDelete = itemView.findViewById(R.id.btnDelete);
            }

            void bind(FeaturedPost post) {
                tvTitle.setText(post.getTitle());
                tvStatus.setText(post.getStatusText());
                tvStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), post.getStatusColor()));

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                tvTime.setText("提交时间：" + sdf.format(post.getCreateTime()));

                if ("rejected".equals(post.getStatus()) && post.getRejectReason() != null) {
                    tvRejectReason.setVisibility(View.VISIBLE);
                    tvRejectReason.setText("拒绝原因：" + post.getRejectReason());
                } else {
                    tvRejectReason.setVisibility(View.GONE);
                }

                btnEdit.setOnClickListener(v -> {
                    if (listener != null) listener.onClick(post);
                });

                btnDelete.setOnClickListener(v -> {
                    if (deleteListener != null) deleteListener.onDelete(post);
                });
            }
        }
    }
}